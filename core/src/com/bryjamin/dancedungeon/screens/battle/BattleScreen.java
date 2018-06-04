package com.bryjamin.dancedungeon.screens.battle;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.music.MusicFiles;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.audio.SoundSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleDeploymentSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DisplayEnemyIntentUISystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.StunnedSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileEffectSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.UndoMoveSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.UtilityAiSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.ArchingTextureSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.BattleScreenInputSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BlinkOnHitSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.EndBattleSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.GenerateTargetsSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.HealthSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.NoMoreActionsGreyScaleSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ReselectTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.AnimationSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FollowPositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.HealthBarSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.ScaleTransformationSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdateBoundPositionsSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.TutorialSystem;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.utils.GameDelta;


/**
 * Created by BB on 11/10/2017.
 */

public class BattleScreen extends AbstractScreen {

    private PartyDetails partyDetails;
    private BattleEvent battleEvent;

    private Screen previousScreen;
    private World world;
    private boolean isTutorial;

    public BattleScreen(MainGame game, Screen previousScreen, BattleEvent battleEvent, PartyDetails partyDetails) {
        this(game, previousScreen, battleEvent, partyDetails, false);
    }

    public BattleScreen(MainGame game, Screen previousScreen, BattleEvent battleEvent, PartyDetails partyDetails, boolean isTutorial) {
        super(game);
        this.previousScreen = previousScreen;
        this.partyDetails = partyDetails;
        this.battleEvent = battleEvent;
        this.isTutorial = isTutorial;
        createWorld();
        game.musicSystem.changeMix(MusicFiles.BATTLE_MUSIC);
    }

    private void createWorld(){

        Stage UIStage = new Stage(gameport, game.batch);

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        //Initialize Tiles
                        new TileSystem(battleEvent),
                        new TutorialSystem(isTutorial),
                        new BattleDeploymentSystem(battleEvent, isTutorial),
                        new UndoMoveSystem(),

                        game.musicSystem,
                        new SoundSystem(assetManager),

                        new BattleScreenInputSystem(gameport),

                        //new InformationBannerSystem(game, gameport, InformationBannerSystem.State.BATTLE_SCREEN),
                        new BattleScreenUISystem(UIStage, game),

                        new PlayerPartyManagementSystem(partyDetails),

                        new MovementSystem(),
                        new FollowPositionSystem(),
                        new UpdateBoundPositionsSystem(),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,

                        new TileEffectSystem(),

                        new ConditionalActionSystem(),
                        new TurnSystem(),
                        new HealthSystem(),
                        new BlinkOnHitSystem(),
                        new ExpireSystem(),
                        new EndBattleSystem(game, battleEvent, partyDetails)
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new ActionOnTapSystem(),
                        new ActionQueueSystem(),

                        //Rendering Effects
                        new FadeSystem(),
                        new ScaleTransformationSystem(),
                        new ArchingTextureSystem(),

                        new NoMoreActionsGreyScaleSystem(),
                        new DisplayEnemyIntentUISystem(),
                        new UtilityAiSystem(),
                        new StunnedSystem(),
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

    public Screen getPreviousScreen() {
        return previousScreen;
    }
}
