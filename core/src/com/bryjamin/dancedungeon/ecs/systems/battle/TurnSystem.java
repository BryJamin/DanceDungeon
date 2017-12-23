package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
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


    private ComponentMapper<UtilityAiComponent> utilityAiMapper;

    private ComponentMapper<CoordinateComponent> coordinateMapper;

    private Array<Entity> currentTurnEntities = new Array<Entity>();

    private Entity currentEntity;

    private Array<Entity> enemyTurnEntities = new Array<Entity>();
    private Array<Entity> allyTurnEntities = new Array<Entity>();

    private TurnComponent currentTurnComponent;

    private boolean processingFlag = true;


    private enum STATE {
        WAITING, NEXT
    }

    private STATE state = STATE.NEXT;


    public enum TURN {
        ENEMY, ALLY
    }

    public TURN turn = ENEMY;

    @SuppressWarnings("unchecked")
    public TurnSystem() {
        super(Aspect.all(TurnComponent.class, SkillsComponent.class).one(UtilityAiComponent.class, PlayerControlledComponent.class));
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
                state = STATE.NEXT;
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

            for (Entity e : allyTurnEntities) {
                skillMapper.get(e).endTurn();
                turnMapper.get(e).reset();
            }

        } else if (turn == ALLY) {
            currentTurnEntities.addAll(allyTurnEntities);
            world.getSystem(SelectedTargetSystem.class).reselectEntityAfterActionComplete();

        }

        state = STATE.NEXT;

        processingFlag = true;


    }


    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }

    //TODO organise, as it is quite messy
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

                        if (currentTurnEntities.size <= 0) {
                            setUp(ENEMY);
                            return;
                        }


                        // break;

                }


                currentEntity = currentTurnEntities.pop();
                if (!playerMapper.has(currentEntity)) {
                    currentEntity.getComponent(TurnComponent.class).state = TurnComponent.State.DECIDING;

                    if (skillMapper.has(currentEntity)) {
                        skillMapper.get(currentEntity).endTurn();
                    }

                    if(turnMapper.has(currentEntity)){
                        turnMapper.get(currentEntity).reset();
                    }


                }

                state = STATE.WAITING;


                break;


            case WAITING:

                TurnComponent turnComponent = currentEntity.getComponent(TurnComponent.class);

                if (!playerMapper.has(currentEntity)) {

                    switch (turnComponent.state) {

                        case DECIDING:

                            if (!turnComponent.hasActions()) {
                                turnComponent.state = TurnComponent.State.END;
                            } else {

                                turnComponent.state = TurnComponent.State.WAITING;

                                if (utilityAiMapper.has(currentEntity)) {
                                    utilityAiMapper.get(currentEntity).utilityAiCalculator.performAction(world, currentEntity);
                                }

                            }

                            break;

                        case WAITING:

                            if (!world.getSystem(ActionCameraSystem.class).isProcessing()) {
                                turnComponent.state = TurnComponent.State.DECIDING;
                            }

                            break;

                        case END:

                            state = STATE.NEXT;
                            break;
                    }

                }

                break;


        }

    }


}

