package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.QueuedAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.QueuedInstantAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.AvailableActionsCompnent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UsedByTutorialComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DisplayEnemyIntentUISystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.factories.unit.UnitLibrary;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
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
    private DisplayEnemyIntentUISystem displayEnemyIntentUISystem;
    private TileSystem tileSystem;
    private TurnSystem turnSystem;


    private ComponentMapper<CoordinateComponent> coordsM;
    private ComponentMapper<PositionComponent> posM;

    public boolean processing;


    private Entity rangedEnemy;
    private Entity meleeEnemy;
    private Entity tutorialEnemy3;


    private Entity meleePlayer;
    private Entity playerUnitWithRangedSkill;
    private Entity playerUnitWithTHROWNSkill;


    public static boolean isTutorial = false;


    public TutorialSystem(boolean processing){
        super(Aspect.all(UsedByTutorialComponent.class));
        this.processing = processing;
        TutorialSystem.isTutorial = processing;
    }


    public enum TutorialState {

        BEGINNING,
        BANNER,
        ALLIED_STRUCTURE,
        ENEMY_FIRST_MOVE,
        HERO_EXPLANATION_AND_PLAYER_FIRST_MOVE,
        MOVE_HERE_PROMPT,

        SKILLS,

        PUSHING,

        RANGED_PLAYER_ARRIVES,
        THROWN_PLAYER_ARRIVES,

        OBJECTIVES,

        END,


    }

    private TutorialState tutorialState = TutorialState.HERO_EXPLANATION_AND_PLAYER_FIRST_MOVE;


    private Coordinates PLAYER_FIRST_MOVE_FINAL_COORDINATES = new Coordinates(3, 4);
    private Coordinates MELEE_PLAYER_START_COORDINATES = new Coordinates(0, 4);
    private Coordinates THROWN_PLAYER_START_COORDINATES = new Coordinates(0, 2);


    private Coordinates RANGED_PLAYER_COORDINATES = new Coordinates(1, 2);

    private Coordinates MELEE_ENEMY_COORDINATES = new Coordinates(6, 2);
    private Coordinates MELEE_ENEMY_FIRST_MOVE_COORDINATES = new Coordinates(4, 1);
    private Coordinates MELEE_ENEMY_SECOND_MOVE_COORDINATES = new Coordinates(3, 2);

    private Coordinates THIRD_ENEMY_FIRST_MOVE_COORDINATES = new Coordinates(5, 2);

    private Coordinates RANGED_ENEMY_PLACEMENT = new Coordinates(6, 4);
    private Coordinates RANGED_ENEMY_PLACEMENT_AFTER_FIRST_ATTACK = new Coordinates(5, 4);

    private Coordinates RANGED_ENEMY_AFTER_NEXT_TURN = new Coordinates(4, 4);

    private Coordinates MIDDLE_ALLIED_STRUCTURE_COORDINATES = new Coordinates(4, 2);

    private Coordinates TUTORIAL_END_SPAWN_ONE = new Coordinates(6, 5);
    private Coordinates TUTORIAL_END_SPAWN_TWO = new Coordinates(7, 2);
    private Coordinates TUTORIAL_END_SPAWN_THREE = new Coordinates(7, 2);

    @Override
    protected void initialize() {

        if(!processing) return;

        actionQueueSystem.observable.addObserver(this);
        turnSystem.addPlayerTurnObserver(this);

        //PART ONE - RANGED ENEMY IS PLACED AND TIPS DESCRIBING THE BATTLEFIELD ARE DISPLAYED ///

        rangedEnemy = createTutorialEntity(new Coordinates(RANGED_ENEMY_PLACEMENT), UnitLibrary.RANGED_BLASTER);

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

        turnSystem.setUp(TurnSystem.TURN.ENEMY);

            //OPEN HERO UNIT TUTORIAL
        createTipWorldAction(meleePlayer.getComponent(CenteringBoundComponent.class).bound, TutorialState.HERO_EXPLANATION_AND_PLAYER_FIRST_MOVE);

    }


    @Override
    public void update(Object o) {

        if(o instanceof ActionQueueSystem){

            AvailableActionsCompnent tc;
            Coordinates current;

            switch (tutorialState){

                case HERO_EXPLANATION_AND_PLAYER_FIRST_MOVE: //Checks Whether The Player Has Moved To The Current Sqaure


                    //PART TWO - PLAYER MOVES HERO TOWARDS RANGED ENEMY ///

                    tc = meleePlayer.getComponent(AvailableActionsCompnent.class);
                    current = coordsM.get(meleePlayer).coordinates;

                    if(!current.equals(PLAYER_FIRST_MOVE_FINAL_COORDINATES) && !current.equals(MELEE_PLAYER_START_COORDINATES)){
                        meleePlayer.getComponent(CoordinateComponent.class).coordinates.set(MELEE_PLAYER_START_COORDINATES);
                        Vector3 v3 = tileSystem.getPositionUsingCoordinates(MELEE_PLAYER_START_COORDINATES, meleePlayer.getComponent(CenteringBoundComponent.class).bound);
                        posM.get(meleePlayer).position.set(v3.x, v3.y, v3.z);
                        tc.reset();
                        battleScreenUISystem.createTutorialWindow(tileSystem.createRectangleUsingCoordinates(PLAYER_FIRST_MOVE_FINAL_COORDINATES), TutorialState.MOVE_HERE_PROMPT);


                    } else if(!tc.attackActionAvailable){//If player uses Attack Action early, rest attack action
                        tc.reset();
                    } else if(current.equals(PLAYER_FIRST_MOVE_FINAL_COORDINATES)){
                        createTipWorldAction(new Rectangle(), TutorialState.SKILLS);
                        tutorialState = TutorialState.SKILLS;
                    };

                    break;

                case SKILLS:

                    //PART THREE - PLAYER IS DISPLAYED INFORMATION ON SKILL AND THEN NEEDS TO ATTACK THE ENEMY ///
                    //WHEN THE ENEMY IS PUSHED INFORMATION ABOUT PUSHING IS SHOWN ///

                    tc = meleePlayer.getComponent(AvailableActionsCompnent.class);
                    Coordinates enemyCurrent = coordsM.get(rangedEnemy).coordinates;

                    if(!enemyCurrent.equals(RANGED_ENEMY_PLACEMENT_AFTER_FIRST_ATTACK)){
                        tc.attackActionAvailable = true;
                    } else if(enemyCurrent.equals(RANGED_ENEMY_PLACEMENT_AFTER_FIRST_ATTACK)){

                        tutorialState = TutorialState.RANGED_PLAYER_ARRIVES;
                        createTipWorldAction(rangedEnemy.getComponent(CenteringBoundComponent.class).bound, TutorialState.PUSHING);
                        createTutorialStateTransferAction(TutorialState.RANGED_PLAYER_ARRIVES);

                    }
            }
        } else if(o instanceof TurnSystem){



            switch (tutorialState){


                case RANGED_PLAYER_ARRIVES:

                    //PART THREE - PLAYER UNIT WITH RANGED SKILL IS PLACED. USER NEEDS TO USE RANGED ATTACK ///

                    //Add Ranged Enemy,
                    //Tutorial Hack To Skip Over The 'Intent' Turn.
                    //Better to Create an 'Enemy Turn' Listener within the TurnSystem.
                    turnSystem.setUp(TurnSystem.TURN.ENEMY);

                    for(Entity e : this.getEntities()){
                        displayEnemyIntentUISystem.releaseAttack(e);
                    }

                        //MOVE RANGED UNIT TO RE-ATTACK SAME STRUCTURE
                    createMovementActionForEnemyEntity(rangedEnemy, RANGED_ENEMY_AFTER_NEXT_TURN);
                    createAttackActionForEnemyEntity(rangedEnemy, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_BLAST), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));


                        //ADD MELEE ENEMY THAT MOVES BELOW THE TARGET ALLIED STRUCTURE
                    meleeEnemy = createTutorialEntity(new Coordinates(MELEE_ENEMY_COORDINATES), UnitLibrary.MELEE_BLOB);
                    createMovementActionForEnemyEntity(meleeEnemy, MELEE_ENEMY_FIRST_MOVE_COORDINATES);
                    createAttackActionForEnemyEntity(meleeEnemy, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_SWIPE), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));

                        //ADD RANGED PLAYER UNIT
                    playerUnitWithRangedSkill = UnitLibrary.getPlayerUnit(world, UnitLibrary.CHARACTERS_BOLAS);
                    playerUnitWithRangedSkill.getComponent(CoordinateComponent.class).coordinates.set(RANGED_PLAYER_COORDINATES);
                    createTipWorldAction(playerUnitWithRangedSkill.getComponent(CenteringBoundComponent.class).bound, TutorialState.RANGED_PLAYER_ARRIVES);

                    tutorialState = TutorialState.THROWN_PLAYER_ARRIVES;


                    addAIToTutorialEntity(rangedEnemy);


                    break;

                case THROWN_PLAYER_ARRIVES:

                    turnSystem.setUp(TurnSystem.TURN.ENEMY);

                    for(Entity e : this.getEntities()){
                        displayEnemyIntentUISystem.releaseAttack(e);
                    }

                    Entity tutorialEnemyNumber3 = createTutorialEntity(new Coordinates(MELEE_ENEMY_COORDINATES), UnitLibrary.MELEE_BLOB);
                    createMovementActionForEnemyEntity(tutorialEnemyNumber3, new Coordinates(THIRD_ENEMY_FIRST_MOVE_COORDINATES));
                    createAttackActionForEnemyEntity(tutorialEnemyNumber3, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_SWIPE), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));


                    //Default Enemy Moves Back to Underneath the allied structure
                    createMovementActionForEnemyEntity(meleeEnemy, new Coordinates(MELEE_ENEMY_FIRST_MOVE_COORDINATES));
                    //Retargets the Structure
                    createAttackActionForEnemyEntity(meleeEnemy, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_SWIPE), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));


                    Entity blasterForThrownPlayer = createTutorialEntity(new Coordinates(7, 2), UnitLibrary.RANGED_BLASTER);
                    createMovementActionForEnemyEntity(blasterForThrownPlayer, new Coordinates(5, 1));
                    //createAttackActionForEnemyEntity(tutorialEnemy3, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_BLAST), new Coordinates(2, 5));

                    //createAttackActionForEnemyEntity(meleeEnemy, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_SWIPE), new Coordinates(MIDDLE_ALLIED_STRUCTURE_COORDINATES));


                    playerUnitWithTHROWNSkill = UnitLibrary.getPlayerUnit(world, UnitLibrary.CHARACTERS_FIRAS);
                    playerUnitWithTHROWNSkill.getComponent(CoordinateComponent.class).coordinates.set(new Coordinates(THROWN_PLAYER_START_COORDINATES));
                    createTipWorldAction(playerUnitWithTHROWNSkill.getComponent(CenteringBoundComponent.class).bound, TutorialState.THROWN_PLAYER_ARRIVES);

                    tutorialState = TutorialState.OBJECTIVES;
                    createTutorialStateTransferAction(TutorialState.OBJECTIVES);

                    break;

                case OBJECTIVES:

                    for(Entity e : this.getEntities()){
                        e.edit().add(new UtilityAiComponent());
                    }

                    if(this.getEntities().size() <= 0) {

                        UnitLibrary.getEnemyUnit(world, UnitLibrary.MELEE_BLOB)
                                .getComponent(CoordinateComponent.class).coordinates = new Coordinates(TUTORIAL_END_SPAWN_ONE);
                        UnitLibrary.getEnemyUnit(world, UnitLibrary.RANGED_BLASTER)
                                .getComponent(CoordinateComponent.class).coordinates = new Coordinates(TUTORIAL_END_SPAWN_TWO);

                    } else if(this.getEntities().size() == 1){

                        UnitLibrary.getEnemyUnit(world, UnitLibrary.RANGED_BLASTER)
                                .getComponent(CoordinateComponent.class).coordinates = new Coordinates(TUTORIAL_END_SPAWN_TWO);

                    }

                    createTipWorldAction(new Rectangle(), TutorialState.OBJECTIVES);
                    tutorialState = TutorialState.END;

            }




        }
    }


    @Override
    protected void processSystem() {
    }



    private void addAIToTutorialEntity(Entity e){
        e.edit().remove(UsedByTutorialComponent.class);
        e.edit().add(new UtilityAiComponent());
    }

    private Entity createTutorialEntity(Coordinates c, String unitID){

        Entity e = UnitLibrary.getEnemyUnit(world, unitID);
        e.edit().remove(UtilityAiComponent.class);
        e.edit().add(new UsedByTutorialComponent());
        e.getComponent(CoordinateComponent.class).coordinates.set(c);

        return e;
    }


    private void createMovementActionForEnemyEntity(final Entity e, final Coordinates destination){

        actionQueueSystem.pushLastAction(e, new QueuedAction() {
            @Override
            public void act() {
                Queue<Coordinates> coordinatesQueue = new Queue<>();
                tileSystem.findShortestPath(e, coordinatesQueue, destination, 1000);


                for (Coordinates c : coordinatesQueue) {
                    e.getComponent(MoveToComponent.class).movementPositions.add(
                            world.getSystem(TileSystem.class).getPositionUsingCoordinates(
                                    c, e.getComponent(CenteringBoundComponent.class).bound));
                }
            }

            @Override
            public boolean isComplete() {
                return e.getComponent(MoveToComponent.class).isEmpty();
            }

        });

    }


    private void createAttackActionForEnemyEntity(final Entity e, final Skill s, final Coordinates target){

        //MOVE ENEMY INTO PLACE
        actionQueueSystem.pushLastAction(e, new QueuedInstantAction() {
            @Override
            public void act() {
                e.edit().add(new StoredSkillComponent(e.getComponent(CoordinateComponent.class).coordinates,
                        target, s));

                world.getSystem(DisplayEnemyIntentUISystem.class).updateIntent();
            }
        });


    }


    private void createTipWorldAction(final Rectangle r, final TutorialState tutorialState){


        actionQueueSystem.pushLastAction(world.createEntity(), new QueuedAction() {

            Table t;

            @Override
            public void act() {
                t = battleScreenUISystem.createTutorialWindow(r, tutorialState);
            }

            @Override
            public boolean isComplete() {
                return !t.isVisible();
            }

        });

    }


    private void createTutorialStateTransferAction(final TutorialState tutorialStateChange){


        actionQueueSystem.pushLastAction(rangedEnemy, new QueuedInstantAction() {

            @Override
            public void act() {
                tutorialState = tutorialStateChange;
            }

        });

    }


    public TutorialState getTutorialState() {
        return tutorialState;
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
