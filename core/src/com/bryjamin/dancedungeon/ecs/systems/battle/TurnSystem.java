package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.AvailableActionsCompnent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.utils.observer.Observable;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;

import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.PLAYER;
import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ENEMY;
import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.INTENT;

/**
 * Created by BB on 21/10/2017.
 * <p>
 * System used to keep track of blob and enemy turns
 */

public class TurnSystem extends EntitySystem implements Observer{

    private UtilityAiSystem utilityAiSystem;
    private BattleDeploymentSystem battleDeploymentSystem;
    private BattleScreenUISystem battleScreenUISystem;

    private ComponentMapper<AvailableActionsCompnent> turnMapper;
    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerControlledComponent> playerMapper;

    private ComponentMapper<SkillsComponent> skillMapper;
    private ComponentMapper<UnitComponent> unitM;


    private Observable enemyTurnObservable = new Observable();
    private Observable playerTurnObservable = new Observable();

    private ComponentMapper<UtilityAiComponent> utilityAiMapper;

    private ComponentMapper<CoordinateComponent> coordinateMapper;

    private Array<Entity> currentTurnEntities = new Array<Entity>();
    private Entity currentEntity;

    private boolean processingFlag = false;


    private enum STATE {
        WAITING, NEXT_TURN
    }

    private STATE battleState = STATE.NEXT_TURN;


    public enum TURN { //ENEMY - Enemy Turn, //PLAYER - Ally Turn, //INTENT - Stored enemy actions
        ENEMY, PLAYER, INTENT
    }

    public TURN turn = INTENT;

    public TURN getTurn() {
        return turn;
    }

    @SuppressWarnings("unchecked")
    public TurnSystem() {
        super(Aspect.all(AvailableActionsCompnent.class, SkillsComponent.class, UnitComponent.class).one(UtilityAiComponent.class, PlayerControlledComponent.class));
    }

    @Override
    protected void initialize() {
        battleDeploymentSystem.getObservers().addObserver(this);
    }

    @Override
    public void update(Object o) {
        if(o.getClass() == BattleDeploymentSystem.class){
            if(!((BattleDeploymentSystem) o).isProcessing()){
                processingFlag = true;
            }
        }
    }

    @Override
    public void inserted(Entity e) {
        turnMapper.get(e).reset();
    }

    @Override
    public void removed(Entity e) {
        currentTurnEntities.removeValue(e, true);

        try {
            if (currentEntity.equals(e)) {
                battleState = STATE.NEXT_TURN;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void endAllyTurn(){
        setUp(INTENT);
        battleState = STATE.NEXT_TURN;
        playerTurnObservable.notifyObservers(this);
    }


    private void populateTurnEntities(Aspect.Builder builder){

        IntBag bag = world.getAspectSubscriptionManager().get(builder).getEntities();

        System.out.println("BAG SIZE " + turn + " " + bag.size());

        for(int i = 0; i < bag.size(); i++){

            Entity e = world.getEntity(bag.get(i));

            if(this.getEntities().contains(e)) {

                System.out.println("IN HERE BUCKO");

                skillMapper.get(e).endTurn();
                turnMapper.get(e).reset();
                currentTurnEntities.add(e);

            }
        }


    }

    public void setUp(TURN turn) {

        this.turn = turn;
        currentTurnEntities.clear();

        if (turn == ENEMY) {
            populateTurnEntities(Aspect.all(AvailableActionsCompnent.class, SkillsComponent.class, UnitComponent.class, EnemyComponent.class));
        } else if (turn == PLAYER) {
            populateTurnEntities(Aspect.all(AvailableActionsCompnent.class, SkillsComponent.class, UnitComponent.class, PlayerControlledComponent.class));

        }
        battleState = STATE.NEXT_TURN;
        processingFlag = true;
    }


    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }

    //TODO organise, as it is quite messy
    @Override
    protected void processSystem() {

        if(turn == PLAYER) return;

        if(world.getSystem(ActionQueueSystem.class).isProcessing()) return;

        if(turn == INTENT){

            switch (battleState){

                case NEXT_TURN:

                    if(world.getSystem(DisplayEnemyIntentUISystem.class).releaseAttack()){
                        battleState = STATE.WAITING;
                    } else {
                        setUp(ENEMY);
                    }
                    break;
                case WAITING:

                    if (!world.getSystem(ActionQueueSystem.class).isProcessing()) {
                        battleState = STATE.NEXT_TURN;
                    }
                    break;
            }

            return;
        }


        switch (battleState) {

            case NEXT_TURN:

                //TODO what if the set up has an entity that is less than zero?
                if (currentTurnEntities.size <= 0) { //Sets up New set of turn entities when current entiteis are finished
                    switch (turn) {
                        case ENEMY:
                            setUp(PLAYER);
                            enemyTurnObservable.notifyObservers(this);
                            break;
                        case PLAYER:
                            setUp(ENEMY);
                            break;
                    }
                }

                //If the next turn has an array size of zero, switch to the previous turn
                if(currentTurnEntities.size <= 0){
                    turn = turn == ENEMY ? PLAYER : ENEMY;
                    return;
                }

                currentEntity = currentTurnEntities.pop();
                battleState = STATE.WAITING;
                break;


            case WAITING:
                AvailableActionsCompnent availableActionsCompnent = currentEntity.getComponent(AvailableActionsCompnent.class);
                if (!playerMapper.has(currentEntity)) {
                    calculateAiTurn(availableActionsCompnent);
                }
                break;


        }

    }

    public boolean isTurn(TURN turn){
        return this.turn == turn;
    }

    /**
     * Runs a entity's turn if it is not being controlled by the player
     * If an entity has no actions left, it's turn is automatically ended.
     * @param availableActionsCompnent
     */
    private void calculateAiTurn(AvailableActionsCompnent availableActionsCompnent){



        switch (availableActionsCompnent.aiState) {

            case DECIDING:

                UnitData unitData = unitM.get(currentEntity).getUnitData();

                if(unitData.stun > 0){
                    availableActionsCompnent.aiState = AvailableActionsCompnent.AIState.TURN_END;
                } else if (!availableActionsCompnent.hasActions()) {
                    System.out.println("NO ACTIONS");

                    availableActionsCompnent.aiState = AvailableActionsCompnent.AIState.TURN_END;
                } else {
                    availableActionsCompnent.aiState = AvailableActionsCompnent.AIState.WAITING;
                    utilityAiSystem.calculateMove(currentEntity);
                }

                break;

            case WAITING:

                //Before making a decision check it see if an action is currently playing
                if (!world.getSystem(ActionQueueSystem.class).isProcessing()) {
                    availableActionsCompnent.aiState = AvailableActionsCompnent.AIState.DECIDING;
                }

                break;

            case TURN_END: //Once the turn is over set the turn State to the Next Turn

                System.out.println("TURN END");

                battleState = STATE.NEXT_TURN;
                break;
        }

    }

    public void setProcessingFlag(boolean processingFlag) {
        this.processingFlag = processingFlag;
    }


    public boolean isAllActionsComplete(){

        for(Entity e : currentTurnEntities){
            AvailableActionsCompnent tc = turnMapper.get(e);
            if(tc.attackActionAvailable || tc.movementActionAvailable)
                return false;
        }

        return true;


    }


    public void addPlayerTurnObserver(Observer o){
        this.playerTurnObservable.addObserver(o);
    }


    public void addEnemyTurnObserver(Observer o){
        this.enemyTurnObservable.addObserver(o);
    }


}

