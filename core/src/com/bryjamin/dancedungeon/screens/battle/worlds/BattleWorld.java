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
import com.bryjamin.dancedungeon.ecs.systems.battle.EndBattleSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ExplosionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.GenerateTargetsSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.HealthSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.NoMoreActionsSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.PlayerControlledSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.AnimationSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FollowPositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.HealthBarSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.PlayerGraphicalTargetingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UIRenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.decor.FloorFactory;
import com.bryjamin.dancedungeon.factories.spells.SpellFactory;
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;

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

    private PartyDetails partyDetails;
    private GameMap gameMap;

    public BattleWorld(MainGame game, final Viewport gameport, GameMap gameMap, PartyDetails partyDetails) {
        super(game, gameport);
        this.partyDetails = partyDetails;
        this.gameMap = gameMap;
        createWorld();
    }

    public PartyDetails getPartyDetails() {
        return partyDetails;
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
                        new TurnSystem(),
                        new HealthSystem(),
                        new ParentChildSystem(),
                        new BlinkOnHitSystem(),
                        new ExpireSystem(),
                        new PlayerControlledSystem(game),
                        new EndBattleSystem(game, gameMap, partyDetails)
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new ActionOnTapSystem(gameport),
                        new ActionCameraSystem(),
                        new FadeSystem(),
                        new NoMoreActionsSystem(),
                        new PlayerGraphicalTargetingSystem(),
                        new BattleMessageSystem(gameport),
                        new AnimationSystem(game),
                        new RenderingSystem(game, gameport),
                        new HealthBarSystem(game, gameport),
                        new UIRenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch),
                        new GenerateTargetsSystem(),
                        new SelectedTargetSystem(),
                        new DeathSystem()
                )
                .build();

        world = new World(config);
/*
        setUpPlayerLocations(world, partyDetails);
        setUpEnemyLocations(world, partyDetails);*/

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

            if(world.getSystem(TurnSystem.class).getTurn() == TurnSystem.TURN.ALLY) {

                if(world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)){
                    return  true;
                };
                if(world.getSystem(SelectedTargetSystem.class).selectCharacter(input.x, input.y)) return true;

            }
            return false;
        }
    }




}

