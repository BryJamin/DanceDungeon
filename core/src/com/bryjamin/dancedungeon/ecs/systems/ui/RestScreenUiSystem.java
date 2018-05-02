package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.Styles;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.screens.strategy.RestScreen;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 31/03/2018.
 */

public class RestScreenUiSystem extends BaseSystem {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;
    private PlayerPartyManagementSystem partyManagementSystem;

    private static final float BUTTON_WIDTH = Measure.units(75f);
    private static final float BUTTON_HEIGHT = Measure.units(7.5f);

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
        container.setDebug(StageUIRenderingSystem.DEBUG);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());

        container.align(Align.top);

        Label label = new Label(TextResource.REST_SCREEN_WECLOME, uiSkin);
        container.add(label).padTop(Measure.units(12.5f)).expandX();
        container.row();

        Stack rest = createRestStack(TextResource.REST_SCREEN_REST, TextResource.REST_SCREEN_REST_DESCRIPTION, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for(UnitData unitData : partyManagementSystem.getPartyDetails().getParty()){
                    unitData.changeHealth(1);
                }
                returnToMapScreen();

            }
        });


        Stack morale = createRestStack(TextResource.REST_SCREEN_MORALE, TextResource.REST_SCREEN_MORALE_DESCRIPTION, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                partyManagementSystem.editMorale(1);
                returnToMapScreen();
            }
        });



        container.add(rest).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).expandY();
        container.row();

        container.add(morale).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).expandY();
        container.row();

        TextButton leaveRestArea = new TextButton(TextResource.REST_SCREEN_LEAVE, uiSkin);
        leaveRestArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                returnToMapScreen();
            }
        });

        container.add(leaveRestArea).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).expandY();

        stage.addActor(container);

    }

    private void returnToMapScreen(){
        Screen menu = restScreen.getPreviousScreen();
        game.getScreen().dispose();
        game.setScreen(menu);
        ((MapScreen) menu).battleVictory();
    }


    private Stack createRestStack(String title, String description, ClickListener buttonListener){

        Button b = new Button(uiSkin, Styles.BUTTON_STYLE_TOGGLE);
        b.addListener(buttonListener);

        Table wrapper = new Table(uiSkin);
        wrapper.setTouchable(Touchable.disabled);
        Label titleLabel = new Label(title, uiSkin);
        titleLabel.setAlignment(Align.center);
        wrapper.add(titleLabel);
        wrapper.row();

        Label desLabel = new Label(description, uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
        desLabel.setAlignment(Align.center);
        wrapper.add(desLabel);

        Stack stack = new Stack();
        stack.add(b);
        stack.add(wrapper);

        return stack;

    }


    @Override
    protected void processSystem() {

    }
}
