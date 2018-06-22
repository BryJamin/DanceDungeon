package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.AvailableActionsCompnent;
import com.bryjamin.dancedungeon.ecs.components.battle.CurrentTurnComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.factories.unit.UnitData;
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
    private ActionQueueSystem actionQueueSystem;

    private ComponentMapper<AvailableActionsCompnent> availMapper;
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


    public void start() {
        processingFlag = true;
    }

    public void stop() {
        processingFlag = false;
    }


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
                start();
            }
        }
    }

    @Override
    public void inserted(Entity e) {
        availMapper.get(e).reset();
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
        playerTurnObservable.notifyObservers(this);
    }


    /**
     * Resets the Available actions of the entities inside the builder
     * @param builder
     */
    private void populateTurnEntities(Aspect.Builder builder){

        IntBag bag = world.getAspectSubscriptionManager().get(builder).getEntities();

        for(int i = 0; i < bag.size(); i++){

            Entity e = world.getEntity(bag.get(i));

            //TODO Create a CURRENT TURN COMPONENT, THAT MARKS THAT THE ENTITY IS PART OF THE CURRENT TURN
            //TODO THEN PERFORM A TURN WHERE AFTER IT IS OVER YOU REMOVE THE COMPOENT
            //TODO REPEAT UNTIL THE ENTITY NO LONGER EXISTS


            skillMapper.get(e).endTurn();
            availMapper.get(e).reset();
            e.edit().add(new CurrentTurnComponent());
            currentTurnEntities.add(e);
        }


    }

    public void setUp(TURN turn) {

        this.turn = turn;
        currentTurnEntities.clear();



        if (turn == ENEMY) {
            populateTurnEntities(Aspect.all(UtilityAiComponent.class, AvailableActionsCompnent.class, SkillsComponent.class, UnitComponent.class, EnemyComponent.class));
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


    @Override
    protected void processSystem() {


        if(actionQueueSystem.isProcessing()) return;


        switch (turn) {

            case INTENT:
                if(!world.getSystem(DisplayEnemyIntentUISystem.class).checkForAndReleaseStoreAttack()){
                    setUp(ENEMY);
                }
                return;

            case PLAYER:
                return;

        }


        switch (battleState) {

            case NEXT_TURN:

                IntBag bag = world.getAspectSubscriptionManager()
                        .get(Aspect.all(UtilityAiComponent.class, CurrentTurnComponent.class, AvailableActionsCompnent.class))
                        .getEntities();


                if (bag.size() <= 0) { //Sets up New set of turn entities when current entiteis are finished
                    switch (turn) {
                        case ENEMY:
                            setUp(PLAYER);
                            enemyTurnObservable.notifyObservers(this);
                            break;
                    }

                    return;
                }

                currentEntity = world.getEntity(bag.get(0));
                battleState = STATE.WAITING;
                break;


            case WAITING:
                if(calculateAiTurn(availMapper.get(currentEntity))){
                    battleState = STATE.NEXT_TURN;
                };
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
    private boolean calculateAiTurn(AvailableActionsCompnent availableActionsCompnent){

        switch (availableActionsCompnent.aiState) {

            case DECIDING:

                UnitData unitData = unitM.get(currentEntity).getUnitData();

                if(unitData.stun > 0){
                    availableActionsCompnent.aiState = AvailableActionsCompnent.AIState.TURN_END;
                } else if (availableActionsCompnent.hasActions()) {
                    availableActionsCompnent.aiState = AvailableActionsCompnent.AIState.WAITING;
                    utilityAiSystem.calculateMove(currentEntity);
                } else {
                    availableActionsCompnent.aiState = AvailableActionsCompnent.AIState.TURN_END;
                }

                break;

            case WAITING:

                //Before making a decision check it see if an action is currently playing
                if (!actionQueueSystem.isProcessing()) {
                    availableActionsCompnent.aiState = AvailableActionsCompnent.AIState.DECIDING;
                }

                break;

            case TURN_END:
                availableActionsCompnent.aiState = AvailableActionsCompnent.AIState.DECIDING;
                currentEntity.edit().remove(CurrentTurnComponent.class);
                //Once the turn is over set the turn State to the Next Turn
                return true;
        }

        return false;

    }

    public void setProcessingFlag(boolean processingFlag) {
        this.processingFlag = processingFlag;
    }


    public boolean isAllActionsComplete(){

        for(Entity e : currentTurnEntities){
            AvailableActionsCompnent tc = availMapper.get(e);
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

