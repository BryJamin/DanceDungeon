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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.screens.strategy.RestScreen;

/**
 * Created by BB on 31/03/2018.
 */

public class RestScreenUiSystem extends BaseSystem {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;
    private PlayerPartyManagementSystem partyManagementSystem;


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

        container.align(Align.top);

        Label label = new Label("You've Reached a Rest Site", uiSkin);
        container.add(label).expandX();
        container.row();


        Label actionsLabel = new Label("Please Select An Action to Take", uiSkin);
        container.add(actionsLabel).expandX();
        container.row();

        //Table actionsTable = new Table(uiSkin);

        //container.add(actionsTable);

        final TextButton restButton = new TextButton("Rest (Restore 1 hp to all party members)", uiSkin);
        container.add(restButton).width(stage.getWidth()).expandX().expandY();

        restButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                for(UnitData unitData : partyManagementSystem.getPartyDetails().getParty()){

                    unitData.getStatComponent().changeHealth(1);
                }

                Screen menu = restScreen.getPreviousScreen();
                game.getScreen().dispose();
                game.setScreen(menu);
                ((MapScreen) menu).battleVictory();


                restButton.setDisabled(true);
            }
        });

        container.row();

        TextButton boostMoraleButton = new TextButton("Boost Morale (Restore 1 to your party's morale)", uiSkin);

        boostMoraleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                partyManagementSystem.getPartyDetails().changeMorale(1);
                Screen menu = restScreen.getPreviousScreen();
                game.getScreen().dispose();
                game.setScreen(menu);
                ((MapScreen) menu).battleVictory();
            }
        });


        container.add(boostMoraleButton).width(stage.getWidth()).expandX().expandY();

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

        container.add(leaveRestArea).width(stage.getWidth()).expandY();


        stage.addActor(container);



    }

    @Override
    protected void processSystem() {

    }
}
