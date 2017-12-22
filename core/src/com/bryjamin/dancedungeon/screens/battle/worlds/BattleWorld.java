package com.bryjamin.dancedungeon.screens.battle.worlds;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BlinkOnHitSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BulletSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DispelSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.EndBattleSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ExplosionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.HealthSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FollowPositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.HealthBarSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.PlayerGraphicalTargetingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UIRenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.factories.decor.FloorFactory;
import com.bryjamin.dancedungeon.factories.player.Unit;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.factories.spells.SpellFactory;
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.screens.battle.BattleDetails;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 28/11/2017.
 */

public class BattleWorld extends WorldContainer {

    float originX = Measure.units(10f);
    float originY = Measure.units(10f);
    float width = Measure.units(80f);
    float height = Measure.units(45f);

    int rows = 5;
    int columns = 10;

    private VictoryAdapter victoryAdapter = new VictoryAdapter();

    private BattleDetails battleDetails;

    public BattleWorld(MainGame game, final Viewport gameport, BattleDetails battleDetails) {
        super(game, gameport);
        this.battleDetails = battleDetails;
        createWorld();
    }

    public BattleDetails getBattleDetails() {
        return battleDetails;
    }

    public void createWorld(){

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,
                        new MovementSystem(),
                        new FollowPositionSystem(),
                        new UpdatePositionSystem(),
                        new TileSystem(originX, originY, width, height, rows, columns),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ConditionalActionSystem(),
                        new ExplosionSystem(),
                        new BulletSystem(),
                        new DispelSystem(),
                        new TurnSystem(),
                        new HealthSystem(),
                        new ParentChildSystem(),
                        new BlinkOnHitSystem(),
                        new DeathSystem(),
                        new ExpireSystem(),
                        new EndBattleSystem(game)
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new ActionOnTapSystem(gameport),
                        new ActionCameraSystem(),
                        new FadeSystem(),
                        new PlayerGraphicalTargetingSystem(),
                        new BattleMessageSystem(gameport),
                        new RenderingSystem(game, gameport),
                        new HealthBarSystem(game, gameport),
                        new UIRenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch),

                        new SelectedTargetSystem()
                )
                .build();

        world = new World(config);

        setUpPlayerLocations(world, battleDetails);
        setUpEnemyLocations(world, battleDetails);

        BagToEntity.bagToEntity(world.createEntity(), new FloorFactory(game.assetManager).createFloor(originX, originY, width, height,
                rows, columns));


        BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().endTurnButton(0, 0));

/*
        BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().defaultButton(Measure.units(0), Measure.units(50f), new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                game.setScreen(new BattleScreen(game));
            }
        }));
*/

    }


    private void setUpPlayerLocations(World world, BattleDetails battleDetails){

        UnitFactory unitFactory = new UnitFactory();

        for(int i = 0; i < battleDetails.getPlayerParty().size; i++) {

            if (battleDetails.getPlayerParty().get(i) != null) {

                Unit unit = battleDetails.getPlayerParty().get(i);
                ComponentBag player = unitFactory.getUnit(unit);

                Coordinates c = player.getComponent(CoordinateComponent.class).coordinates;
                player.getComponent(CoordinateComponent.class).freePlacement = true;


                switch (i) {
                    case 0:
                        c.set(2, 2);
                        break;
                    case 1:
                        c.set(1, 3);
                        break;
                    case 2:
                        c.set(1, 1);
                        break;
                    case 3:
                        c.set(0, 2);
                        break;
                }

                TileSystem tileSystem = world.getSystem(TileSystem.class);

                player.getComponent(PositionComponent.class).position.set(
                        tileSystem.getPositionUsingCoordinates(c.getX() - 4, c.getY(),
                                player.getComponent(CenteringBoundaryComponent.class).bound)
                );

                player.getComponent(MoveToComponent.class).movementPositions.add(tileSystem.getPositionUsingCoordinates(c,
                        player.getComponent(CenteringBoundaryComponent.class).bound));

                BagToEntity.bagToEntity(world.createEntity(), player);

            }

        }

    }



    private void setUpEnemyLocations(World world, BattleDetails battleDetails){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for(int i = 0; i < battleDetails.getEnemyParty().size; i++) {
            ComponentBag enemy = battleDetails.getEnemyParty().get(i);

            if (enemy != null) {
                Coordinates c = enemy.getComponent(CoordinateComponent.class).coordinates;
                enemy.getComponent(CoordinateComponent.class).freePlacement = true;


                switch (i) {
                    case 0:
                        c.set(tileSystem.getMaxX() - 2, 2);
                        break;
                    case 1:
                        c.set(tileSystem.getMaxX() - 1, 3);
                        break;
                    case 2:
                        c.set(tileSystem.getMaxX() - 1, 1);
                        break;
                    case 3:
                        c.set(tileSystem.getMaxX(), 2);
                        break;
                }



                enemy.getComponent(PositionComponent.class).position.set(
                        tileSystem.getPositionUsingCoordinates(c.getX() + 4, c.getY(),
                                enemy.getComponent(CenteringBoundaryComponent.class).bound)
                );

                enemy.getComponent(MoveToComponent.class).movementPositions.add(tileSystem.getPositionUsingCoordinates(c,
                        enemy.getComponent(CenteringBoundaryComponent.class).bound));

                BagToEntity.bagToEntity(world.createEntity(), enemy);

            }

        }


    }




    public void pauseWorld() {
        for (BaseSystem s : world.getSystems()) {
            if (!(s instanceof RenderingSystem || s instanceof HealthBarSystem || s instanceof UIRenderingSystem)) {
                s.setEnabled(false);
            }
        }
    }

    public void unPauseWorld() {
        for (BaseSystem s : world.getSystems()) {
            s.setEnabled(true);
        }
    }



    @Override
    public void handleInput(InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(victoryAdapter);
    }



    private class VictoryAdapter extends InputAdapter {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 input = gameport.unproject(new Vector3(screenX, screenY , 0));

            if(world.getSystem(TurnSystem.class).turn == TurnSystem.TURN.ALLY) {

                if(world.getSystem(SelectedTargetSystem.class).selectCharacter(input.x, input.y)) return true;

                if(world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)){
                    return  true;
                };
            }
            return false;
        }
    }




}

