package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.ecs.systems.FixedToCameraPanAndFlingSystem;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CameraMath;

/**
 * Created by BB on 02/03/2018.
 */

public class MapStageInitialisationSystem extends BaseSystem {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private FixedToCameraPanAndFlingSystem camSys;
    private MainGame game;
    private PartyDetails partyDetails;
    private Viewport gameport;
    private Skin uiSkin;

    private Table container;


    public MapStageInitialisationSystem(MainGame game, PartyDetails partyDetails, Viewport gameport){
        this.game = game;
        this.partyDetails = partyDetails;
        this.gameport = gameport;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }


    @Override
    protected void initialize() {

        Stage stage = stageUIRenderingSystem.stage;

        container = new Table(uiSkin);
        container.setDebug(true);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());
        container.align(Align.top);


        Label label = new Label("Select Your Next Destination", uiSkin);
        //label.setFillParent(true);
        container.add(label);
        container.row();


        float width = container.getWidth() / 3;

        Table infoTable = new Table(uiSkin);
        infoTable.align(Align.center);
        container.add(infoTable).height(Measure.units(5f));

        label = new Label("Money: $" + partyDetails.money, uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);

        label = new Label("Reputation: $" + partyDetails.money, uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);

        label = new Label("Something Else: $" + partyDetails.money, uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);


        stage.addActor(container);



    }

    @Override
    protected void processSystem() {
        container.setPosition(CameraMath.getBtmLftX(gameport), CameraMath.getBtmY(gameport.getCamera()));
    }

    //TODO messy?
    public void updateInformation() {
        container.remove();
        container.clear();
        initialize();
    }
}
