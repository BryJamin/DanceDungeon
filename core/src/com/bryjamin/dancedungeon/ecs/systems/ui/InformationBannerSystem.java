package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CameraMath;

import java.util.Locale;

public class InformationBannerSystem extends BaseSystem implements Observer {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;
    private PlayerPartyManagementSystem partyManagementSystem;

    private Table container;

    private MainGame game;
    private Viewport gameport;
    private Skin uiSkin;

    public static float BANNER_HEIGHT;

    public enum State {
        BATTLE_SCREEN, DEFAULT;
    }


    private State state = State.DEFAULT;

    public InformationBannerSystem(MainGame game, Viewport gameport){
        this.game = game;
        this.gameport = gameport;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }

    public InformationBannerSystem(MainGame game, Viewport gameport, State state){
        this.game = game;
        this.gameport = gameport;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
        this.state = state;
    }



    @Override
    protected void initialize() {
        partyManagementSystem.addObserver(this);
        createBanner();
        updateBanner();
    }


    public void updateBanner(){

        if(container.hasChildren()){
            container.clear();
        }

        PartyDetails partyDetails = partyManagementSystem.getPartyDetails();
        float width = container.getWidth() / 3;

        Table infoTable = new Table(uiSkin);
        infoTable.align(Align.center);
        container.add(infoTable).height(Measure.units(5f));


        Table tableForMoneyIconAndText = new Table(uiSkin);
        tableForMoneyIconAndText.add(new Image(renderingSystem.getRegion(TextureStrings.ICON_MONEY))).size(Measure.units(5f));
        Label label = new Label("" + partyDetails.money, uiSkin);
        label.setAlignment(Align.center);
        tableForMoneyIconAndText.add(label);

        infoTable.add(tableForMoneyIconAndText).width(width);

        label = new Label(String.format(Locale.ENGLISH,"Morale: %d/%d", partyDetails.morale, PartyDetails.MAX_MORALE), uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);

        label = new Label("Reputation: " + partyDetails.skillPoints, uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);


        container.row();


        if(state != State.BATTLE_SCREEN) {

            Table table = new Table(uiSkin);
            container.add(table).width(stageUIRenderingSystem.stage.getWidth());

            for (UnitData unitData : partyDetails.getParty()) {

                Table characterTable = new Table(uiSkin);
                characterTable.add(new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(unitData.icon))))
                        .width(Measure.units(3.5f))
                        .height(Measure.units(3.5f))
                        .padRight(Measure.units(1.5f));

                int current = unitData.getStatComponent().health;
                int max = unitData.getStatComponent().maxHealth;

                characterTable.add(new Label(String.format(Locale.ENGLISH, "HP %s/%s", current, max), uiSkin));

                table.add(characterTable).expandX();

            }

        }

    }

    public void createBanner(){

        Stage stage = stageUIRenderingSystem.stage;

        container = new Table(uiSkin);
        container.setDebug(StageUIRenderingSystem.DEBUG);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());
        container.align(Align.top);
        stage.addActor(container);

    }

    public void updateInformation(){
        updateBanner();
    }

    public Table getContainer() {
        return container;
    }

    @Override
    protected void processSystem() {
        container.setPosition(CameraMath.getBtmLftX(gameport), CameraMath.getBtmY(gameport.getCamera()));
    }

    @Override
    public void update(Object o) {
        updateInformation();
    }
}
