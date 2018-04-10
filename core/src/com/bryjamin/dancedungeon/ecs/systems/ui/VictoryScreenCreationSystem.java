package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 02/02/2018.
 */

public class VictoryScreenCreationSystem extends BaseSystem {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;
    private Skin uiSkin = new Skin();

    private Screen nextScreen;
    private Viewport gameport;
    private MainGame game;
    private PartyDetails partyDetails;
    private BattleEvent battleEvent;


    public VictoryScreenCreationSystem(MainGame game, Viewport gameport, Screen nextScreen, BattleEvent battleEvent, PartyDetails partyDetails){
        this.nextScreen = nextScreen;
        this.gameport = gameport;
        this.game = game;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
        this.partyDetails = partyDetails;
    }





    @Override
    protected void processSystem() {




    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    public void createVictoryRewards(){
        Stage stage = stageUIRenderingSystem.stage;

        Table container = stageUIRenderingSystem.createContainerTable();
        container.setBackground(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.BLOCK)).tint(new Color(0,0,0,0.6f)));
        stage.addActor(container);
        Table victoryTable = new Table(uiSkin);
        victoryTable.align(Align.top);

        container.add(victoryTable).width(Measure.units(40f)).height(Measure.units(40f));


        Label victory = new Label("Victory", uiSkin);
        victoryTable.add(victory).height(Measure.units(5f));
        victoryTable.row();
        Table rewardTable = createRewardTable();
        victoryTable.add(rewardTable).height(Measure.units(30f));
        victoryTable.row();


        TextButton textButton = new TextButton("Continue", uiSkin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Screen menu = ((BattleScreen) nextScreen).getPreviousScreen();
                game.getScreen().dispose();
                game.setScreen(menu);
                ((MapScreen) menu).battleVictory();
            }
        });

        victoryTable.add(textButton).height(Measure.units(5f)).expandX();
    }


    public Table createRewardTable(){
        Table rewardTable = new Table(uiSkin);


        Label reputation = new Label("Reputation", uiSkin);
        rewardTable.add(reputation);

        partyDetails.reputation = partyDetails.reputation + 5;
        Label reputationIncrease = new Label(" +5", uiSkin);
        rewardTable.add(reputationIncrease);

        rewardTable.row();

        Label gold = new Label("Gold", uiSkin);
        rewardTable.add(gold);

        partyDetails.money = partyDetails.money + 5;
        Label goldIncrease = new Label(" +5", uiSkin);
        rewardTable.add(goldIncrease);

        return rewardTable;

    }

}
