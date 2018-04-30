package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.EnemyIntentUISystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;
import com.bryjamin.dancedungeon.factories.player.UnitData;
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
public class TutorialSystem extends BaseSystem implements Observer{

    private ActionQueueSystem actionQueueSystem;
    private BattleScreenUISystem battleScreenUISystem;
    private TileSystem tileSystem;
    private TurnSystem turnSystem;


    private ComponentMapper<CoordinateComponent> coordsM;
    private ComponentMapper<PositionComponent> posM;

    public boolean processing;


    private Entity meleePlayer;


    public TutorialSystem(boolean processing){
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


    }

    private TutorialState tutorialState = TutorialState.PLAYER_FIRST_MOVE;


    private Coordinates PLAYER_FIRST_MOVE_FINAL_COORDINATES = new Coordinates(3, 4);
    private Coordinates MELEE_PLAYER_START_COORDINATES = new Coordinates(0, 4);

    @Override
    protected void initialize() {

        if(!processing) return;

        actionQueueSystem.observable.addObserver(this);




        final Coordinates alliedStructureCoordinates = new Coordinates(4, 2);

        final Entity e = UnitLibrary.getEnemyUnit(world, UnitLibrary.RANGED_BLASTER);
        e.edit().remove(UtilityAiComponent.class);
        e.getComponent(CoordinateComponent.class).coordinates.set(6, 4);

        Array<Coordinates> path = new Array<>();
        path.addAll(new Coordinates(5, 4), new Coordinates(4, 4));

        actionQueueSystem.createMovementAction(e, path);

        //MOVE ENEMY INTO PLACE
        actionQueueSystem.pushLastAction(e, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return true;
            }

            @Override
            public void performAction(World world, Entity entity) {
                entity.edit().add(new StoredSkillComponent(entity.getComponent(CoordinateComponent.class).coordinates,
                        alliedStructureCoordinates, SkillLibrary.getSkill(SkillLibrary.ENEMY_SKILL_BLAST)));

                world.getSystem(EnemyIntentUISystem.class).updateIntent();
            }
        });

        //OPEN MORALE TUTORIAL
        actionQueueSystem.pushLastAction(e, new WorldConditionalAction() {

            Table t;

            @Override
            public boolean condition(World world, Entity entity) {
                return !t.isVisible();
            }

            @Override
            public void performAction(World world, Entity entity) {
                t = battleScreenUISystem.createTutorialWindow(new Rectangle(), TutorialState.BANNER);
            }
        });


        //OPEN ALLIED TUTORIAL
        actionQueueSystem.pushLastAction(e, new WorldConditionalAction() {

            Table t;

            @Override
            public boolean condition(World world, Entity entity) {
                return !t.isVisible();
            }

            @Override
            public void performAction(World world, Entity entity) {
                t = battleScreenUISystem.createTutorialWindow(tileSystem.createRectangleUsingCoordinates(alliedStructureCoordinates), TutorialState.ALLIED_STRUCTURE);
            }
        });



        //OPEN ENEMY TUTORIAL
        actionQueueSystem.pushLastAction(e, new WorldConditionalAction() {

            Table t;

            @Override
            public boolean condition(World world, Entity entity) {
                return !t.isVisible();
            }

            @Override
            public void performAction(World world, Entity entity) {
                t = battleScreenUISystem.createTutorialWindow(e.getComponent(CenteringBoundComponent.class).bound, TutorialState.ENEMY_FIRST_MOVE);
            }
        });


        meleePlayer = UnitLibrary.getPlayerUnit(world, UnitLibrary.CHARACTERS_SGT_SWORD);
        meleePlayer.getComponent(CoordinateComponent.class).coordinates.set(MELEE_PLAYER_START_COORDINATES);
        turnSystem.setUp(TurnSystem.TURN.ENEMY);


        //OPEN HERO UNIT TUTORIAL
        actionQueueSystem.pushLastAction(e, new WorldConditionalAction() {

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

            switch (tutorialState){

                case PLAYER_FIRST_MOVE:
                    Coordinates current = coordsM.get(meleePlayer).coordinates;
                    TurnComponent tc = meleePlayer.getComponent(TurnComponent.class);

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


            }

        }

    }


    @Override
    protected void processSystem() {
    }





}
