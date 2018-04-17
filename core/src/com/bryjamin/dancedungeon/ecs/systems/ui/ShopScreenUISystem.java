package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.screens.strategy.ShopScreen;
import com.bryjamin.dancedungeon.utils.Measure;

public class ShopScreenUISystem extends BaseSystem {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;
    private PlayerPartyManagementSystem partyManagementSystem;


    private Table container;

    private MainGame game;
    private Viewport gameport;
    private Skin uiSkin;
    private ShopScreen shopScreen;
    private SkillLibrary skillLibrary;

    private Array<Skill> skillToBuyArray = new Array<Skill>();

    private enum State {
        BUY, SELL
    }

    private State state = State.BUY;

    public ShopScreenUISystem(MainGame game, Viewport gameport, ShopScreen shopScreen){
        this.game = game;
        this.gameport = gameport;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
        this.shopScreen = shopScreen;
        this.skillLibrary = new SkillLibrary();
    }

    @Override
    protected void initialize() {


        Array<Skill> allSkills = skillLibrary.getItems().values().toArray();

        for(int i = 0; i < 3; i++){
            allSkills.shuffle();
            skillToBuyArray.add(allSkills.pop());
        }

        createShopUI();


    }


    private void createShopUI(){

        Stage stage = stageUIRenderingSystem.stage;

        container = new Table();
        container.setDebug(StageUIRenderingSystem.DEBUG);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());

        container.align(Align.top);


        Label label = new Label("Welcome to the shop!", uiSkin);
        container.add(label).padTop(Measure.units(10f)).expandX();
        container.row();


        Label actionsLabel = new Label("Feel free to browse my wares", uiSkin);
        container.add(actionsLabel).expandX();
        container.row();

        TextButton leaveRestArea = new TextButton("Leave", uiSkin);
        leaveRestArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goToPreviousScreen(shopScreen.getPreviousScreen());
            }
        });

        TextButton switchToSellButton = new TextButton("Sell", uiSkin);
        switchToSellButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                state = State.SELL;
                refreshUI();
            }
        });

        TextButton switchToBuyButton = new TextButton("Buy", uiSkin);
        switchToBuyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                state = State.BUY;
                refreshUI();
            }
        });

        stage.addActor(container);

        Table tabTable = new Table(uiSkin);
        container.add(tabTable).width(stage.getWidth());

        tabTable.add(switchToBuyButton).width(Measure.units(15f)).expandX();
        tabTable.add(switchToSellButton).width(Measure.units(15f)).expandX();

        container.row();

        switch (state){
            case BUY:
                ScrollPane shopItemPane = createBuyScrollPane();
                container.add(shopItemPane).expandY();
                break;
            case SELL:
                container.add(createSellScrollPane()).expandY();
                break;
        }

        container.row();
        container.add(leaveRestArea).width(stage.getWidth()).height(Measure.units(7.5f)).align(Align.bottom);


    }




    public ScrollPane createBuyScrollPane(){

        Table shopTable = new Table(uiSkin);
        ScrollPane shopItemPane = new ScrollPane(shopTable, uiSkin, "default"); //Adds the Table to the ScrollPane

        if(skillToBuyArray.size == 0){
            Label youHaveNothingToSell = new Label("There is Nothing Left To Buy", uiSkin);
            shopTable.add(youHaveNothingToSell);
            return shopItemPane;
        }


        for(final Skill s : skillToBuyArray){
            shopTable.row();

            Table t = new Table(uiSkin);
            t.setDebug(StageUIRenderingSystem.DEBUG);
            t.align(Align.left);
            shopTable.add(t).width(stageUIRenderingSystem.stage.getWidth()).height(Measure.units(7.5f)).padBottom(Padding.MEDIUM);

            t.add(new Image(renderingSystem.getAtlas().findRegion(s.getIcon()))).height(Measure.units(5f)).width(Measure.units(5f)).padRight(Measure.units(1.5f));
            t.add(new Label(s.getName(), uiSkin)).expandX().padRight(Measure.units(1.5f));

            Label skillDescription = new Label(s.getDescription(), uiSkin, Fonts.SMALL_FONT_NAME);
            skillDescription.setWrap(true);
            skillDescription.setAlignment(Align.center);

            t.add(skillDescription).width(Measure.units(55f)).padRight(Measure.units(1.5f)).expandX();
            BuyButton buyButton = new BuyButton("Buy ($" + s.getStorePrice() + ")", uiSkin, s, s.getStorePrice());
            t.add(buyButton).expandX();
        }

        return shopItemPane;

    }


    public ScrollPane createSellScrollPane(){

        Table shopTable = new Table(uiSkin);
        ScrollPane shopItemPane = new ScrollPane(shopTable);

        if(partyManagementSystem.getPartyDetails().getSkillInventory().size == 0 && partyManagementSystem.getPartyDetails().getEquippedInventory().size == 0){
            Label youHaveNothingToSell = new Label("Your Inventory is Empty", uiSkin);
            shopTable.add(youHaveNothingToSell);
            return shopItemPane;
        }

        for(final Skill s : partyManagementSystem.getPartyDetails().getSkillInventory()){
            shopTable.row();
            shopTable.add(new Image(renderingSystem.getAtlas().findRegion(s.getIcon()))).height(Measure.units(5f)).width(Measure.units(5f)).padRight(Measure.units(1.5f));
            shopTable.add(new Label(s.getName(), uiSkin)).padRight(Measure.units(1.5f));

            Label skillDescription = new Label(s.getDescription(), uiSkin);
            skillDescription.setWrap(true);
            skillDescription.setAlignment(Align.center);

            shopTable.add(skillDescription).width(Measure.units(60f)).padRight(Measure.units(1.5f));
            SellButton sellButton = new SellButton("Sell ($" + s.getStorePrice() / 2 + ")", uiSkin, s, s.getStorePrice() / 2);
            shopTable.add(sellButton).expandX().fillY();
        }


        for(final Skill s : partyManagementSystem.getPartyDetails().getEquippedInventory()){
            shopTable.row();
            shopTable.add(new Image(renderingSystem.getAtlas().findRegion(s.getIcon()))).height(Measure.units(5f)).width(Measure.units(5f)).padRight(Measure.units(1.5f));
            shopTable.add(new Label(s.getName(), uiSkin)).padRight(Measure.units(1.5f));

            Label skillDescription = new Label(s.getDescription() + " (Equipped)", uiSkin);
            skillDescription.setWrap(true);
            skillDescription.setAlignment(Align.center);

            shopTable.add(skillDescription).width(Measure.units(60f)).padRight(Measure.units(1.5f));
            SellButton sellButton = new SellButton("Sell ($" + s.getStorePrice() / 2 + ")", uiSkin, s, s.getStorePrice() / 2);
            shopTable.add(sellButton).expandX().fillY();
        }

        return shopItemPane;

    }


    public void goToPreviousScreen(Screen previous){
        game.getScreen().dispose();
        game.setScreen(previous);
        ((MapScreen) previous).battleVictory();
    }

    @Override
    protected void processSystem() {

    }


    private void refreshUI(){
        container.clear();
        createShopUI();
    }


    private class BuyButton extends TextButton {

        private int price;

        public BuyButton(String text, Skin skin, final Skill s, final int price){
            super(text, skin);
            this.price = price;

            this.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    partyManagementSystem.getPartyDetails().addSkillToInventory(s);
                    partyManagementSystem.editMoney(-price);
                    skillToBuyArray.removeValue(s, true);
                    refreshUI();
                }
            });

        }

        @Override
        public void act(float delta){

            if(partyManagementSystem.getPartyDetails().money < price){
                this.setDisabled(true);
                this.setColor(Color.RED);
            } else {
                this.setDisabled(false);
            }

            super.act(delta);
        }

    }


    private class SellButton extends TextButton {

        private int price;

        public SellButton(String text, Skin skin, final Skill s, final int price){
            super(text, skin);
            this.price = price;

            this.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    partyManagementSystem.getPartyDetails().removeSkillFromInventoryOrParty(s);
                    partyManagementSystem.editMoney(price);
                    skillToBuyArray.removeValue(s, true);
                    refreshUI();
                }
            });

        }

    }

}
