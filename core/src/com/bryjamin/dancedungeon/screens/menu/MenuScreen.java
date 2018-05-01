package com.bryjamin.dancedungeon.screens.menu;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.music.MusicFiles;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdateBoundPositionsSystem;
import com.bryjamin.dancedungeon.ecs.systems.input.BasicInputSystemWithStage;
import com.bryjamin.dancedungeon.ecs.systems.music.MusicSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.MenuScreenUISystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.utils.GameDelta;

/**
 * Created by BB on 08/10/2017.
 */

public class MenuScreen extends AbstractScreen {

    private World world;

    public MenuScreen(MainGame game) {
        super(game);
        createWorld();
    }


    private void createWorld() {


        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        new MenuScreenUISystem(game, gameport),
                        new BasicInputSystemWithStage(gameport),
                        new MusicSystem(game, MusicFiles.BG_MAIN_MENU),
                        new MovementSystem(),
                        new UpdateBoundPositionsSystem(),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ConditionalActionSystem(),
                        new ParentChildSystem(),
                        new ExpireSystem(),
                        new DeathSystem()
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new ActionOnTapSystem(),
                        new FadeSystem(),
                        new RenderingSystem(game, gameport),
                        new StageUIRenderingSystem( new Stage(gameport, game.batch)),
                        new BoundsDrawingSystem(batch))
                .build();

        world = new World(config);

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GameDelta.delta(world, delta);
    }

/*
    public MenuScreen(final MainGame game) {
        super(game);
        this.game = game;

        Skin uiSkin = Skins.DEFAULT_SKIN(assetManager);

        stage = new Stage(gameport, game.batch);
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.setBackground(new TextureRegionDrawable(game.assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class).findRegion(TextureStrings.WORLD_MAP)));

        if(QuickSave.isThereAValidQuickSave()){
            TextButton textBtn1 = new TextButton("Continue", uiSkin);
            textBtn1.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.getScreen().dispose();
                    QuickSave.SavedData savedData = QuickSave.savedData;
                    GameMap gameMap = savedData.getGameMap();
                    gameMap.setUpLoadedMap();
                    PartyDetails partyDetails = savedData.getPartyDetails();
                    game.setScreen(new MapScreen(game, gameMap, partyDetails));
                }
            });

            table.add(textBtn1).width(Measure.units(20f)).padBottom(Padding.SMALL);
            table.row();
        }


        TextButton textBtn1 = new TextButton("New Game", uiSkin);
        textBtn1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreen().dispose();
                game.setScreen(new CharacterSelectionScreen(game));
            }
        });

        TextButton textBtn2 = new TextButton("Quit Game", uiSkin);
        textBtn2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                System.exit(0);
            }
        });

        table.setDebug(StageUIRenderingSystem.DEBUG); // This is optional, but enables debug lines for tables.
        table.add(textBtn1).width(Measure.units(20f)).padBottom(Measure.units(2.5f));
        table.row();
        table.add(textBtn2).width(Measure.units(20f));

        stage.addActor(table);

        // Add widgets to the table here.

    }*/


}
