package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;

import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ALLY;
import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ENEMY;

/**
 * Created by BB on 21/10/2017.
 * <p>
 * System used to keep track of blob and enemy turns
 */

public class TurnSystem extends EntitySystem {

    private ComponentMapper<TurnComponent> turnMapper;

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerControlledComponent> playerMapper;

    private ComponentMapper<SkillsComponent> skillMapper;
    private ComponentMapper<StatComponent> statMapper;


    private ComponentMapper<UtilityAiComponent> utilityAiMapper;

    private ComponentMapper<CoordinateComponent> coordinateMapper;

    private Array<Entity> currentTurnEntities = new Array<Entity>();

    private Entity currentEntity;

    private Array<Entity> enemyTurnEntities = new Array<Entity>();
    private Array<Entity> allyTurnEntities = new Array<Entity>();

    private TurnComponent currentTurnComponent;

    private boolean processingFlag = true;


    private enum STATE {
        WAITING, NEXT_TURN
    }

    private STATE battleState = STATE.NEXT_TURN;


    public enum TURN {
        ENEMY, ALLY
    }

    private TURN turn = ENEMY;

    public TURN getTurn() {
        return turn;
    }

    @SuppressWarnings("unchecked")
    public TurnSystem() {
        super(Aspect.all(TurnComponent.class, SkillsComponent.class, StatComponent.class).one(UtilityAiComponent.class, PlayerControlledComponent.class));
    }


    @Override
    public void inserted(Entity e) {

        if (enemyMapper.has(e)) {
            enemyTurnEntities.add(e);
        } else if (playerMapper.has(e)) {
            allyTurnEntities.add(e);
        }

        turnMapper.get(e).reset();

    }

    @Override
    public void removed(Entity e) {
        enemyTurnEntities.removeValue(e, true);
        allyTurnEntities.removeValue(e, true);
        currentTurnEntities.removeValue(e, true);

        try {
            if (currentEntity.equals(e)) {
                battleState = STATE.NEXT_TURN;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void setUp(TURN turn) {

        this.turn = turn;
        currentTurnEntities.clear();

        if (turn == ENEMY) {
            currentTurnEntities.addAll(enemyTurnEntities);
        } else if (turn == ALLY) {
            currentTurnEntities.addAll(allyTurnEntities);
           // world.getSystem(SelectedTargetSystem.class).reselectEntityAfterActionComplete();
        }

        for (Entity e : currentTurnEntities) { //Resets the turn for each entity
            skillMapper.get(e).endTurn();
            turnMapper.get(e).reset();
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

        //if (world.getSystem(ActionCameraSystem.class).isProcessing()) return;


        switch (battleState) {

            case NEXT_TURN:

                //TODO what if the set up has an entity that is less than zero?
                if (currentTurnEntities.size <= 0) { //Sets up New set of turn entities when current entiteis are finished
                    switch (turn) {
                        case ENEMY:
                            setUp(ALLY);
                            break;
                        case ALLY:
                            setUp(ENEMY);
                            break;
                    }
                }

                //If the next turn has an array size of zero, switch to the previous turn
                if(currentTurnEntities.size <= 0){
                    turn = turn == ENEMY ? ALLY : ENEMY;
                    return;
                }

                currentEntity = currentTurnEntities.pop();
                battleState = STATE.WAITING;
                break;


            case WAITING:
                TurnComponent turnComponent = currentEntity.getComponent(TurnComponent.class);
                if (!playerMapper.has(currentEntity)) {
                    calculateAiTurn(turnComponent);
                }
                break;


        }

    }


    /**
     * Runs a entity's turn if it is not being controlled by the player
     * If an entity has no actions left, it's turn is automatically ended.
     * @param turnComponent
     */
    private void calculateAiTurn(TurnComponent turnComponent){

        switch (turnComponent.state) {

            case DECIDING:

                StatComponent statComponent = statMapper.get(currentEntity);

                if(statComponent.stun > 0){
                    statComponent.stun--;
                    turnComponent.state = TurnComponent.State.END;
                } else if (!turnComponent.hasActions()) {
                    turnComponent.state = TurnComponent.State.END;
                } else {

                    turnComponent.state = TurnComponent.State.WAITING;

                    if (utilityAiMapper.has(currentEntity)) {
                        utilityAiMapper.get(currentEntity).utilityAiCalculator.performAction(world, currentEntity);
                    }

                }

                break;

            case WAITING:

                //Before making a decision check it see if an action is currently playing
                if (!world.getSystem(ActionCameraSystem.class).isProcessing()) {
                    turnComponent.state = TurnComponent.State.DECIDING;
                }

                break;

            case END: //Once the turn is over set the turn State to the Next Turn
                battleState = STATE.NEXT_TURN;
                break;
        }

    }



}

