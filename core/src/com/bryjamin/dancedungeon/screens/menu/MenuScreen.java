package com.bryjamin.dancedungeon.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.save.QuickSave;

/**
 * Created by BB on 08/10/2017.
 */

public class MenuScreen extends AbstractScreen {

    private Stage stage;
    private Table table;

    public MenuScreen(final MainGame game) {
        super(game);
        this.game = game;

        Skin uiSkin = Skins.DEFAULT_SKIN(assetManager);

        stage = new Stage(gameport, game.batch);
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);

        System.out.println();
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
        }


        TextButton textBtn1 = new TextButton("New Game", uiSkin);
        textBtn1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreen().dispose();
                game.setScreen(new ExpeditionScreen(game));
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

        table.setDebug(true); // This is optional, but enables debug lines for tables.
        table.add(textBtn1).width(Measure.units(20f)).padBottom(Measure.units(2.5f));
        table.row();
        table.add(textBtn2).width(Measure.units(20f));

        stage.addActor(table);

        // Add widgets to the table here.

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    public void resize (int width, int height) {
        gameport.update(width, height);
        stage.getViewport().update(width, height);
    }


}
