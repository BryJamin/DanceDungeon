package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.GreyScaleComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.TutorialAIComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.EnemyIntentUISystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.observer.Observable;
import com.bryjamin.dancedungeon.utils.observer.Observer;

/**
 * System Used To Conduct The Tutorial.
 *
 * Follows a Strict Script
 *
 *
 */
public class TutorialSystem extends EntitySystem implements Observer{

    private ActionQueueSystem actionQueueSystem;
    private BattleScreenUISystem battleScreenUISystem;
    private EnemyIntentUISystem enemyIntentUISystem;
    private TileSystem tileSystem;
    private TurnSystem turnSystem;


    private ComponentMapper<CoordinateComponent> coordsM;
    private ComponentMapper<PositionComponent> posM;

    public boolean processing;


    private Entity rangedEnemy;
    private Entity meleeEnemy;
    private Entity tutorialEnemy3;


    private Entity meleePlayer;
    private Entity rangedPlayer;
    private Entity thrownPlayer;


    public TutorialSystem(boolean processing){
        super(Aspect.all(TutorialAIComponent.class));
        this.processing = processing;
        System.out.println(processing);
    }


    public enum TutorialState {

        BEGINNING,
        BANNER,
        ALLIED_STRUCTURE,
        ENEMY_FIRST_MOVE,
        PLAYER_FIRST_MOVE,
        MOVE_HERE_PROMPT,

        SKILLS,

        PUSHING,

        RANGED_PLAYER_ARRIVES,
        THROWN_PLAYER_ARRIVES,

        OBJECTIVES,

        END,


    }

    private TutorialState tutorialState = TutorialState.PLAYER_FIRST_MOVE;


    private Coordinates PLAYER_FIRST_MOVE_FINAL_COORDINATES = new Coordinates(3, 4);
    private Coordinates MELEE_PLAYER_START_COORDINATES = new Coordinates(0, 4);


    private Coordinates RANGED_PLAYER_COORDINATES = new Coordinates(3, 4);

    private Coordinates MELEE_ENEMY_COORDINATES = new Coordinates(6, 2);
    private Coordinates MELEE_ENEMY_FIRST_MOVE_COORDINATES = new Coordinates(4, 1);

    private Coordinates THIRD_ENEMY_FIRST_MOVE_COORDINATES = new Coordinates(5, 2);

    private Coordinates RANGED_ENEMY_PLACEMENT = new Coordinates(6, 4);
    private Coordinates RANGED_ENEMY_PLACEMENT_AFTER_FIRST_ATTACK = new Coordinates(5, 4);

    private Coordinates RANGED_ENEMY_AFTER_NEXT_TURN = new Coordinates(4, 4);

    private Coordinates MIDDLE_ALLIED_STRUCTURE_COORDINATES = new Coordinates(4, 2);



    private Coordinates TUTORIAL_END_SPAWN_ONE = new Coordinates(6, 5);
    private Coordinates TUTORIAL_END_SPAWN_TWO = new Coordinates(7, 4);
    private Coordinates TUTORIAL_END_SPAWN_THREE = new Coordinates(7, 2);

    @Override
    protected void initialize() {

        if(!processing) return;

        actionQueueSystem.observable.addObserver(this);
        turnSystem.addNextTurnObserver(this);



        rangedEnemy = UnitLibrary.getEnemyUnit(world, UnitLibrary.RANGED_BLASTER);
        rangedEnemy.edit().remove(UtilityAiComponent.class);
        rangedEnemy.edit().add(new TutorialAIComponent());
        rangedEnemy.getComponent(CoordinateComponent.class).coordinates.set(RANGED_ENEMY_PLACEMENT);

        //ACTIONS FOR MOVING ENTITY INTO POSITION
        createMovementActionForEnemyEntity(rangedEnemy, new Coordinates(4, 4));
        createAttackActionForEnemyEntity(rangedEnemy, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_BLAST), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));


        //OPEN MORALE TUTORIAL
        createTipWorldAction(new Rectangle(), TutorialState.BANNER);

        //OPEN ALLIED TUTORIAL
        createTipWorldAction(tileSystem.createRectangleUsingCoordinates(new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES)),
                TutorialState.ALLIED_STRUCTURE);


        //OPEN ENEMY TUTORIAL

        createTipWorldAction(rangedEnemy.getComponent(CenteringBoundComponent.class).bound, TutorialState.ENEMY_FIRST_MOVE);



        meleePlayer = UnitLibrary.getPlayerUnit(world, UnitLibrary.CHARACTERS_SGT_SWORD);
        meleePlayer.getComponent(CoordinateComponent.class).coordinates.set(MELEE_PLAYER_START_COORDINATES);
        meleePlayer.edit().add(new TutorialAIComponent());
        turnSystem.setUp(TurnSystem.TURN.ENEMY);


        //OPEN HERO UNIT TUTORIAL
        actionQueueSystem.pushLastAction(rangedEnemy, new WorldConditionalAction() {

            Table t;

            @Override
            public boolean condition(World world, Entity entity) {
                return !t.isVisible();
            }

            @Override
            public void performAction(World world, Entity entity) {
                tutorialState = TutorialState.PLAYER_FIRST_MOVE;

                t = battleScreenUISystem.createTutorialWindow(meleePlayer.getComponent(CenteringBoundComponent.class).bound, TutorialState.PLAYER_FIRST_MOVE);
            }
        });


    }


    @Override
    public void update(Object o) {

        if(o instanceof ActionQueueSystem){

            TurnComponent tc;
            Coordinates current;

            switch (tutorialState){

                case PLAYER_FIRST_MOVE: //Checks Whether The Player Has Moved To The Current Sqaure
                    tc = meleePlayer.getComponent(TurnComponent.class);
                    current = coordsM.get(meleePlayer).coordinates;

                    if(!current.equals(PLAYER_FIRST_MOVE_FINAL_COORDINATES) && !current.equals(MELEE_PLAYER_START_COORDINATES)){
                        meleePlayer.getComponent(CoordinateComponent.class).coordinates.set(MELEE_PLAYER_START_COORDINATES);

                        Vector3 v3 = tileSystem.getPositionUsingCoordinates(MELEE_PLAYER_START_COORDINATES, meleePlayer.getComponent(CenteringBoundComponent.class).bound);
                        posM.get(meleePlayer).setX(v3.x);
                        posM.get(meleePlayer).setY(v3.y);
                        meleePlayer.getComponent(TurnComponent.class).movementActionAvailable = true;
                        battleScreenUISystem.createTutorialWindow(tileSystem.createRectangleUsingCoordinates(PLAYER_FIRST_MOVE_FINAL_COORDINATES), TutorialState.MOVE_HERE_PROMPT);


                    } else if(!tc.attackActionAvailable){
                        tc.attackActionAvailable = true;
                        tc.movementActionAvailable = true;
                    } else if(current.equals(PLAYER_FIRST_MOVE_FINAL_COORDINATES)){


                        actionQueueSystem.pushLastAction(meleePlayer, new WorldConditionalAction() {

                            Table t;

                            @Override
                            public boolean condition(World world, Entity entity) {
                                return !t.isVisible();
                            }

                            @Override
                            public void performAction(World world, Entity entity) {
                                tutorialState = TutorialState.SKILLS;
                                t = battleScreenUISystem.createTutorialWindow(new Rectangle(), TutorialState.SKILLS);
                            }
                        });

                    };

                    break;

                case SKILLS:

                    tc = meleePlayer.getComponent(TurnComponent.class);
                    Coordinates enemyCurrent = coordsM.get(rangedEnemy).coordinates;

                    if(!enemyCurrent.equals(RANGED_ENEMY_PLACEMENT_AFTER_FIRST_ATTACK)){
                        tc.attackActionAvailable = true;
                    } else if(enemyCurrent.equals(RANGED_ENEMY_PLACEMENT_AFTER_FIRST_ATTACK)){

                        tutorialState = TutorialState.RANGED_PLAYER_ARRIVES;

                        actionQueueSystem.pushLastAction(rangedEnemy, new WorldConditionalAction() {

                            Table t;

                            @Override
                            public boolean condition(World world, Entity entity) {
                                return !t.isVisible();
                            }

                            @Override
                            public void performAction(World world, Entity entity) {
                                tutorialState = TutorialState.RANGED_PLAYER_ARRIVES;
                                t = battleScreenUISystem.createTutorialWindow(rangedEnemy.getComponent(CenteringBoundComponent.class).bound, TutorialState.PUSHING);
                            }

                        });

                    }
            }
        } else if(o instanceof TurnSystem){



            switch (tutorialState){


                case RANGED_PLAYER_ARRIVES:

                    //Add Ranged Enemy,
                    //TODO Tutorial Hack To Skip Over The 'Intent' Turn.
                    //TODO Better to Create an 'Enemy Turn' Listener within the TurnSystem.
                    turnSystem.setUp(TurnSystem.TURN.ENEMY);

                    for(Entity e : this.getEntities()){
                        enemyIntentUISystem.releaseAttack(e);
                    }

                    createMovementActionForEnemyEntity(rangedEnemy, RANGED_ENEMY_AFTER_NEXT_TURN);
                    createAttackActionForEnemyEntity(rangedEnemy, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_BLAST), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));

                    meleeEnemy = UnitLibrary.getEnemyUnit(world, UnitLibrary.MELEE_BLOB);
                    meleeEnemy.edit().remove(UtilityAiComponent.class);
                    meleeEnemy.getComponent(CoordinateComponent.class).coordinates.set(MELEE_ENEMY_COORDINATES);
                    createMovementActionForEnemyEntity(meleeEnemy, MELEE_ENEMY_FIRST_MOVE_COORDINATES);
                    createAttackActionForEnemyEntity(meleeEnemy, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_SWIPE), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));

                    rangedPlayer = UnitLibrary.getPlayerUnit(world, UnitLibrary.CHARACTERS_BOLAS);
                    createTipWorldAction(rangedPlayer.getComponent(CenteringBoundComponent.class).bound, TutorialState.RANGED_PLAYER_ARRIVES);

                    tutorialState = TutorialState.THROWN_PLAYER_ARRIVES;

                    rangedEnemy.edit().add(new UtilityAiComponent());

                    break;

                case THROWN_PLAYER_ARRIVES:

                    turnSystem.setUp(TurnSystem.TURN.ENEMY);

                    for(Entity e : this.getEntities()){
                        enemyIntentUISystem.releaseAttack(e);
                    }

                    tutorialEnemy3 = UnitLibrary.getEnemyUnit(world, UnitLibrary.MELEE_BLOB);
                    tutorialEnemy3.edit().remove(UtilityAiComponent.class);
                    tutorialEnemy3.getComponent(CoordinateComponent.class).coordinates.set(MELEE_ENEMY_COORDINATES);

                    createMovementActionForEnemyEntity(tutorialEnemy3, THIRD_ENEMY_FIRST_MOVE_COORDINATES);
                    createAttackActionForEnemyEntity(tutorialEnemy3, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_SWIPE), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));


                    createMovementActionForEnemyEntity(meleeEnemy, MELEE_ENEMY_FIRST_MOVE_COORDINATES);
                    createAttackActionForEnemyEntity(meleeEnemy, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_SWIPE), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));

                    thrownPlayer = UnitLibrary.getPlayerUnit(world, UnitLibrary.CHARACTERS_FIRAS);
                    createTipWorldAction(thrownPlayer.getComponent(CenteringBoundComponent.class).bound, TutorialState.THROWN_PLAYER_ARRIVES);

                    tutorialState = TutorialState.OBJECTIVES;

                    break;

                case OBJECTIVES:


                    Entity e = UnitLibrary.getEnemyUnit(world, UnitLibrary.MELEE_BLOB);
                    e.getComponent(CoordinateComponent.class).coordinates = TUTORIAL_END_SPAWN_ONE;
                    e = UnitLibrary.getEnemyUnit(world, UnitLibrary.RANGED_BLASTER);
                    e.getComponent(CoordinateComponent.class).coordinates = TUTORIAL_END_SPAWN_TWO;
                    e = UnitLibrary.getEnemyUnit(world, UnitLibrary.RANGED_BLASTER);
                    e.getComponent(CoordinateComponent.class).coordinates = TUTORIAL_END_SPAWN_THREE;



                    createTipWorldAction(new Rectangle(), TutorialState.OBJECTIVES);

                    tutorialState = TutorialState.END;



            }




        }
    }


    @Override
    protected void processSystem() {
    }




    private void createMovementActionForEnemyEntity(Entity e, Coordinates destination){
        Queue<Coordinates> coordinatesQueue = new Queue<>();
        tileSystem.findShortestPath(e, coordinatesQueue, destination, 1000);
        actionQueueSystem.createMovementAction(e, coordinatesQueue);
    }


    private void createAttackActionForEnemyEntity(Entity e, final Skill s, final Coordinates target){

        //MOVE ENEMY INTO PLACE
        actionQueueSystem.pushLastAction(e, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return true;
            }

            @Override
            public void performAction(World world, Entity entity) {
                entity.edit().add(new StoredSkillComponent(entity.getComponent(CoordinateComponent.class).coordinates,
                        target, s));

                world.getSystem(EnemyIntentUISystem.class).updateIntent();
            }
        });


    }


    private void createTipWorldAction(final Rectangle r, final TutorialState tutorialState){


        actionQueueSystem.pushLastAction(rangedEnemy, new WorldConditionalAction() {

            Table t;

            @Override
            public boolean condition(World world, Entity entity) {
                return !t.isVisible();
            }

            @Override
            public void performAction(World world, Entity entity) {
                t = battleScreenUISystem.createTutorialWindow(r, tutorialState);
            }

        });

    }

    private WorldConditionalAction endTurnButton(){

        return new WorldConditionalAction() {

            Table t;

            @Override
            public boolean condition(World world, Entity entity) {
                return !t.isVisible();
            }

            @Override
            public void performAction(World world, Entity entity) {
                tutorialState = TutorialState.SKILLS;
                t = battleScreenUISystem.createTutorialWindow(new Rectangle(), TutorialState.PUSHING);
            }
        };


    }


}
