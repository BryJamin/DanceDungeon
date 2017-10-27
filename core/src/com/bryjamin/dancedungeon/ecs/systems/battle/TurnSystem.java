package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerComponent;

import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ALLY;
import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ENEMY;

/**
 * Created by BB on 21/10/2017.
 */

public class TurnSystem extends EntitySystem {

    private ComponentMapper<TurnComponent> turnMapper;

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerComponent> playerMapper;

    private Array<Entity> currentTurnEntities = new Array<Entity>();

    private Entity currentEntity;

    private Array<Entity> enemyTurnEntities = new Array<Entity>();
    private Array<Entity> allyTurnEntities = new Array<Entity>();

    private TurnComponent currentTurnComponent;

    private boolean processingFlag = false;


    private enum STATE {
        WAITING, NEXT
    }

    private STATE state = STATE.NEXT;




    public enum TURN {
        ENEMY, ALLY
    }

    public TURN turn = ALLY;

    @SuppressWarnings("unchecked")
    public TurnSystem() {
        super(Aspect.all(TurnComponent.class));
    }


    @Override
    public void inserted(Entity e) {

        if(enemyMapper.has(e)){
            enemyTurnEntities.add(e);
           // if(turn == ENEMY) currentTurnEntities.add(e);

        } else if(playerMapper.has(e)){
            allyTurnEntities.add(e);
           // if(turn == ALLY) currentTurnEntities.add(e);
        }

    }

    @Override
    public void removed(Entity e) {
        enemyTurnEntities.removeValue(e, true);
        allyTurnEntities.removeValue(e, true);
        currentTurnEntities.removeValue(e, true);

        if(currentEntity.equals(e)){
            state = STATE.NEXT;
        }

    }

    public void setUp(TURN turn){

        this.turn = turn;

        System.out.println("Set up called turn is" + turn);

        if(turn == ENEMY) {
            currentTurnEntities.addAll(enemyTurnEntities);
        } else if(turn == ALLY){
            currentTurnEntities.addAll(allyTurnEntities);
        }

        state = STATE.NEXT;

        processingFlag = true;



    }


    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }

    @Override
    protected void processSystem() {


        switch (state) {

            case NEXT:
                switch (turn) {

                    case ENEMY:


                        if (currentTurnEntities.size <= 0) {
                            setUp(ALLY);
                            return;
                        }


                        break;

                    case ALLY:

                        return;

                       // break;

                }

                currentEntity = currentTurnEntities.pop();
                currentEntity.getComponent(TurnComponent.class).turnAction.performAction(world, currentEntity);

                state = STATE.WAITING;

                break;


            case WAITING:


               // System.out.println("waiting");

                if(currentEntity.getComponent(TurnComponent.class).turnOverCondition.condition(world, currentEntity)){
                    currentEntity.getComponent(TurnComponent.class).turnAction.cleanUpAction(world, currentEntity);
                    state = STATE.NEXT;
                }


                break;


        }

    }







}