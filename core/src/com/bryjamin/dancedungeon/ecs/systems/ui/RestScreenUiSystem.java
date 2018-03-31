package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.screens.strategy.RestScreen;

/**
 * Created by BB on 31/03/2018.
 */

public class RestScreenUiSystem extends BaseSystem {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;


    private Table container;

    private MainGame game;
    private Viewport gameport;
    private Skin uiSkin;
    private RestScreen restScreen;

    public RestScreenUiSystem(MainGame game, Viewport gameport, RestScreen restScreen){
        this.game = game;
        this.gameport = gameport;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
        this.restScreen = restScreen;
    }

    @Override
    protected void initialize() {

        Stage stage = stageUIRenderingSystem.stage;

        container = new Table();
        container.setDebug(true);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());

        Label label = new Label("You've Reached a Rest Site", uiSkin);
        container.add(label).fillX();
        container.row();


        Label actionsLabel = new Label("Please Select An Action to Take", uiSkin);
        container.add(actionsLabel).fillX();
        container.row();

        Table actionsTable = new Table(uiSkin);

        container.add(actionsTable);

        TextButton restButton = new TextButton("Rest", uiSkin);
        actionsTable.add(restButton);
        TextButton boostMoraleButton = new TextButton("Boost Morale", uiSkin);
        actionsTable.add(boostMoraleButton);

        container.row();
        TextButton leaveRestArea = new TextButton("Leave", uiSkin);
        leaveRestArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Screen menu = restScreen.getPreviousScreen();
                game.getScreen().dispose();
                game.setScreen(menu);
                ((MapScreen) menu).battleVictory();
            }
        });

        container.add(leaveRestArea);


        stage.addActor(container);



    }

    @Override
    protected void processSystem() {

    }
}
