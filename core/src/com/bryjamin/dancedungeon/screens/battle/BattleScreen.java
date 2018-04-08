package com.bryjamin.dancedungeon.screens.battle;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleDeploymentSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.BattleWorldInputHandlerSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BlinkOnHitSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BuffSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BulletSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.EndBattleSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.EnemyIntentSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ExplosionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.GenerateTargetsSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.HealthSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.NoMoreActionsSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.PlayerControlledSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ReselectTargetSystem;
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
import com.bryjamin.dancedungeon.ecs.systems.graphical.ScaleTransformationSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.InformationBannerSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.screens.VictoryScreen;
import com.bryjamin.dancedungeon.screens.menu.DefeatScreen;
import com.bryjamin.dancedungeon.utils.GameDelta;


/**
 * Created by BB on 11/10/2017.
 */

public class BattleScreen extends AbstractScreen {

    private PartyDetails partyDetails;
    private GameMap gameMap;

    private Screen previousScreen;
    private World world;

    public BattleScreen(MainGame game, Screen previousScreen, GameMap gameMap, PartyDetails partyDetails) {
        super(game);
        this.previousScreen = previousScreen;
        this.partyDetails = partyDetails;
        this.gameMap = gameMap;
        createWorld();
    }

    private void createWorld(){

        Stage UIStage = new Stage(gameport, game.batch);

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        new BattleWorldInputHandlerSystem(gameport),
                        new BattleScreenUISystem(UIStage, game),

                        new PlayerPartyManagementSystem(partyDetails),
                        new InformationBannerSystem(game, gameport),

                        new MovementSystem(),
                        new FollowPositionSystem(),
                        new UpdatePositionSystem(),

                        new BuffSystem(),

                        //Initialize Tiles
                        new TileSystem(),
                        new BattleDeploymentSystem((BattleEvent) gameMap.getCurrentMapNode().getMapEvent()),

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
                        new ActionOnTapSystem(),
                        new ActionCameraSystem(),

                        //Rendering     Effects
                        new FadeSystem(),
                        new ScaleTransformationSystem(),

                        new NoMoreActionsSystem(),
                        new PlayerGraphicalTargetingSystem(),
                        new BattleMessageSystem(gameport),
                        new EnemyIntentSystem(),
                        new AnimationSystem(game),
                        new RenderingSystem(game, gameport),
                        new HealthBarSystem(game, gameport),
                        new StageUIRenderingSystem(UIStage),
                        new BoundsDrawingSystem(batch),
                        new GenerateTargetsSystem(),
                        new SelectedTargetSystem(),
                        new ReselectTargetSystem(),
                        new DeathSystem()
                )
                .build();

        world = new World(config);

       // BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().endTurnButton(0, 0));
    }

    @Override
    public void resize(int width, int height) {
        gameport.update(width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GameDelta.delta(world, delta);
    }

    public void victory(PartyDetails partyDetails){
        game.setScreen(new VictoryScreen(game, this, partyDetails));
    }


    public void defeat(){
        game.setScreen(new DefeatScreen(game, this));
    }


    public Screen getPreviousScreen() {
        return previousScreen;
    }
}
