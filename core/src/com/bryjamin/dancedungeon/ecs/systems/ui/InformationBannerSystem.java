package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.Observer;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.strategy.ShopScreen;
import com.bryjamin.dancedungeon.utils.Measure;

import java.util.Locale;

public class InformationBannerSystem extends BaseSystem implements Observer {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;
    private PlayerPartyManagementSystem partyManagementSystem;

    private Table container;

    private MainGame game;
    private Viewport gameport;
    private Skin uiSkin;

    public InformationBannerSystem(MainGame game, Viewport gameport){
        this.game = game;
        this.gameport = gameport;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }


    @Override
    protected void initialize() {
        partyManagementSystem.addObserver(this);
        createBanner();
    }


    public void createBanner(){

        Stage stage = stageUIRenderingSystem.stage;

        container = new Table(uiSkin);
        container.setDebug(true);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());
        container.align(Align.top);

        stage.addActor(container);

        PartyDetails partyDetails = partyManagementSystem.getPartyDetails();
        float width = container.getWidth() / 3;

        Table infoTable = new Table(uiSkin);
        infoTable.align(Align.center);
        container.add(infoTable).height(Measure.units(5f));

        Label label = new Label("Money: $" + partyDetails.money, uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);

        label = new Label(String.format(Locale.ENGLISH,"Morale: %d/%d", partyDetails.morale, PartyDetails.MAX_MORALE), uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);

        label = new Label("Something Else: $" + partyDetails.money, uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);
    }

    public void refreshUI(){
        container.clear();
        container.remove();
        createBanner();
    }

    @Override
    protected void processSystem() {

    }

    @Override
    public void onNotify() {
        refreshUI();
    }
}
