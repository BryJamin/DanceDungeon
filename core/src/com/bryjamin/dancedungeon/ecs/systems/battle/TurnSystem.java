package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldCondition;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MovementRangeComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.AttackAiComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;

import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ALLY;
import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ENEMY;

/**
 * Created by BB on 21/10/2017.
 */

public class TurnSystem extends EntitySystem {

    private ComponentMapper<TurnComponent> turnMapper;

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerComponent> playerMapper;

    private ComponentMapper<AttackAiComponent> attackAiComponentMapper;
    private ComponentMapper<MovementRangeComponent> movementRangeMapper;


    private ComponentMapper<AbilityPointComponent> abilityPointMapper;

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

        try {
            if (currentEntity.equals(e)) {
                state = STATE.NEXT;
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }

    }

    public void setUp(TURN turn){

        this.turn = turn;
        currentTurnEntities.clear();

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

                        if (currentTurnEntities.size <= 0) {
                            setUp(ENEMY);
                            return;
                        }


                       // break;

                }


                currentEntity = currentTurnEntities.pop();
                if(!playerMapper.has(currentEntity)) {
                    currentEntity.getComponent(TurnComponent.class).state = TurnComponent.State.DECIDING;

                    AbilityPointComponent abilityPointComponent = abilityPointMapper.get(currentEntity);

                    abilityPointComponent.abilityPoints = abilityPointComponent.abilityPointsPerTurn;
                }

                state = STATE.WAITING;


                break;


            case WAITING:

                TurnComponent turnComponent = currentEntity.getComponent(TurnComponent.class);


                if(!playerMapper.has(currentEntity)) {


                    switch (turnComponent.state) {

                        case DECIDING:

                            AttackAiComponent attackAiComponent = attackAiComponentMapper.get(currentEntity);

                            AbilityPointComponent abilityPointComponent = abilityPointMapper.get(currentEntity);

                            CoordinateComponent playerCoordinateComponent = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class);

                            MovementRangeComponent movementRangeComponent = movementRangeMapper.get(currentEntity);
                            CoordinateComponent coordinateComponent = coordinateMapper.get(currentEntity);
                            MoveToComponent moveToComponent = currentEntity.getComponent(MoveToComponent.class);



                            if(!CoordinateMath.isWithinRange(coordinateComponent.coordinates, playerCoordinateComponent.coordinates, attackAiComponent.range)
                                    && abilityPointComponent.abilityPoints > 0
                                    ) {


                                world.getSystem(MovementAiSystem.class).calculatePath(coordinateComponent, movementRangeComponent, moveToComponent, currentEntity.getComponent(BoundComponent.class));

                                turnComponent.turnOverCondition = new WorldCondition() {
                                    @Override
                                    public boolean condition(World world, Entity entity) {
                                        return entity.getComponent(MoveToComponent.class).isEmpty();
                                    }
                                };

                                abilityPointComponent.abilityPoints -= 1;

                                turnComponent.state = TurnComponent.State.WAITING;
                            } else if(abilityPointComponent.abilityPoints > 0){

                                for(Entity meleeRangeEntity : world.getSystem(TileSystem.class).getCoordinateMap().get(playerCoordinateComponent.coordinates)){
                                    if( world.getMapper(PlayerComponent.class).has(meleeRangeEntity)){
                                        meleeRangeEntity.getComponent(HealthComponent.class).applyDamage(2.0f);
                                        System.out.println(meleeRangeEntity.getComponent(HealthComponent.class).health);
                                    }
                                }

                                abilityPointComponent.abilityPoints = 0;


                            } else {
                                turnComponent.state = TurnComponent.State.END;
                            }

                            break;

                        case WAITING:

                            if (turnComponent.turnOverCondition.condition(world, currentEntity)) turnComponent.state = TurnComponent.State.DECIDING;

                                break;

                        case END:

                            //System.out.println("End?");

                            state = STATE.NEXT;
                            break;
                    }

                }

                break;


        }

    }







}