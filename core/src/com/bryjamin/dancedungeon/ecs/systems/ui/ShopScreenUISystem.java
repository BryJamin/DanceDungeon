package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
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
    private Table shopTable;
    private ScrollPane skillScrollPane;

    private Table buyTable;

    private Table buyMenuSkillsTable;
    private Table buyMenuSkillDescriptionTable;



    private ButtonGroup<Button> skillButtons = new ButtonGroup<>();

    private float TABLE_WIDTH = Measure.units(95f);


    private MainGame game;
    private Viewport gameport;
    private Skin uiSkin;
    private ShopScreen shopScreen;
    private SkillLibrary skillLibrary;

    private Array<Skill> skillToBuyArray = new Array<>();
    private int sellingIndex = 0;

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

        //Create and shuffle items
        for(int i = 0; i < 3; i++){
            allSkills.shuffle();
            skillToBuyArray.add(allSkills.pop());
        }

        skillButtons = new ButtonGroup<>();
        skillButtons.setMaxCheckCount(1);
        skillButtons.setMinCheckCount(1);
        skillButtons.setUncheckLast(true);

        buyMenuSkillsTable = new Table(uiSkin);
        buyMenuSkillDescriptionTable = new Table(uiSkin);

        createShopUI();


    }


    private void createShopUI(){

        Stage stage = stageUIRenderingSystem.stage;

        container = new Table();
        container.setDebug(StageUIRenderingSystem.DEBUG);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());

        container.align(Align.top);

        stage.addActor(container);

        populateShopUI();


    }

    private void populateShopUI(){

        Stage stage = stageUIRenderingSystem.stage;

        container.clear();

        Label label = new Label(TextResource.SHOP_WELCOME, uiSkin);
        container.add(label).padTop(Measure.units(10f)).expandX(); //Avoids info banner
        container.row();

        TextButton leaveRestArea = new TextButton(TextResource.SHOP_LEAVE, uiSkin);
        leaveRestArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goToPreviousScreen(shopScreen.getPreviousScreen());
            }
        });

        final TextButton switchToSellButton = new TextButton(TextResource.SHOP_SELL, uiSkin);
        switchToSellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                state = State.SELL;
                populateShopTable();
            }
        });

        final TextButton switchToBuyButton = new TextButton(TextResource.SHOP_BUY, uiSkin);
        switchToBuyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                state = State.BUY;
                populateShopTable();
            }
        });

        Table tabTable = new Table(uiSkin);
        container.add(tabTable).width(TABLE_WIDTH).height(Measure.units(7.5f));
        tabTable.add(switchToBuyButton).expandX().fill();
        tabTable.add(switchToSellButton).expandX().fill();

        ButtonGroup<TextButton> buttonGroup = new ButtonGroup<>();
        buttonGroup.setUncheckLast(true);
        buttonGroup.add(switchToBuyButton);
        buttonGroup.add(switchToSellButton);
        container.row();

        shopTable = new Table(uiSkin);
        skillScrollPane = new ScrollPane(shopTable, uiSkin);
        skillScrollPane.setFadeScrollBars(false);
        skillScrollPane.setOverscroll(false, false);
        container.add(skillScrollPane).width(TABLE_WIDTH).maxHeight(Measure.units(27.5f)).minHeight(Measure.units(27.5f)).expandY();
        populateShopTable();
        container.row();
        container.add(leaveRestArea).width(stage.getWidth()).maxWidth(Measure.units(25f)).height(Measure.units(7.5f)).align(Align.bottom).padBottom(Padding.SMALL);
    }


    public void populateShopTable(){

        shopTable.clear();

        switch (state){

            case BUY:

                if(skillToBuyArray.size <= 0){ //If there is something to sell.
                    Label youHaveNothingToSell = new Label(TextResource.SHOP_NOTHING_TO_BUY, uiSkin);
                    shopTable.add(youHaveNothingToSell);
                } else {

                    for(Skill s : skillToBuyArray){
                        shopTable.row();
                        addToShopTable(shopTable, s, false);

                    }
                }

                break;

            case SELL:

                if(partyManagementSystem.getPartyDetails().getSkillInventory().size == 0 &&
                        partyManagementSystem.getPartyDetails().getEquippedInventory().size == 0){
                    Label youHaveNothingToSell = new Label("Your Inventory is Empty", uiSkin);
                    shopTable.add(youHaveNothingToSell);
                    break;
                }

                for(Skill s : partyManagementSystem.getPartyDetails().getSkillInventory()){
                    shopTable.row();
                    addToShopTable(shopTable, s, false);
                }

                for(Skill s : partyManagementSystem.getPartyDetails().getEquippedInventory()){
                    shopTable.row();
                    addToShopTable(shopTable, s, true);
                }

                break;
        }

    }



    private void addToShopTable(Table shopTable, Skill s, boolean isEquipped){


        String skillDes;
        int price;
        String priceTagText;

        switch (state){
            default:
            case BUY:

                skillDes = s.getDescription();
                price = s.getStorePrice();
                priceTagText = TextResource.SHOP_BUY;

                break;


            case SELL:

                skillDes = isEquipped ? "(Equipped) " + s.getDescription() : s.getDescription();
                price = s.getStorePrice() / 2;
                if(price == 0) price = 1;
                priceTagText = TextResource.SHOP_SELL;

                break;
        }

        shopTable.add(new Label("", uiSkin)).padBottom(Measure.units(5f)).padLeft(Padding.SMALL);
        shopTable.add(new Image(renderingSystem.getAtlas().findRegion(s.getIcon()))).height(Measure.units(5f)).width(Measure.units(5f)).padRight(Measure.units(1.5f));
        shopTable.add(new Label(s.getName(), uiSkin)).padRight(Measure.units(1.5f)).expandX();

        Label skillDescription = new Label(skillDes, uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
        skillDescription.setWrap(true);
        skillDescription.setAlignment(Align.center);
        shopTable.add(skillDescription).width(Measure.units(50f)).padRight(Measure.units(1.5f));





        Stack sellButtonStack = new Stack();

        switch (state){
            case BUY:
                BuyButton buyButton = new BuyButton("", uiSkin, s);
                sellButtonStack.add(buyButton);
                break;
            case SELL:
                SellButton sellButton = new SellButton("", uiSkin, s, price);
                sellButtonStack.add(sellButton);
                break;

        }

        Table priceDisplay = new Table(uiSkin);
        priceDisplay.setTouchable(Touchable.disabled);
        //priceDisplay.add(new Label(priceTagText, uiSkin));
        priceDisplay.add(new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.ICON_MONEY)))).size(Measure.units(5f)).expandX();
        priceDisplay.add(new Label("" + price, uiSkin)).expandX();

        sellButtonStack.add(priceDisplay);

        shopTable.add(sellButtonStack).height(Measure.units(7.5f)).width(Measure.units(8.5f)).padRight(Padding.SMALL);

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
        private Skill s;

        public BuyButton(String text, Skin skin, final Skill s){
            super(text, skin);
            this.price = price;
            this.s = s;

            this.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    partyManagementSystem.getPartyDetails().addSkillToInventory(s);
                    partyManagementSystem.editMoney(-s.getStorePrice());
                    skillToBuyArray.removeValue(s, true);
                    populateShopTable();
                }
            });

        }

        @Override
        public void act(float delta){

            if(partyManagementSystem.getPartyDetails().getMoney() < s.getStorePrice()){
                this.setDisabled(true);
                this.setColor(Color.RED);
            } else {
                this.setDisabled(false);
            }

            super.act(delta);
        }

    }


    private class SellButton extends TextButton {

        public SellButton(String text, Skin skin, final Skill s, final int price){
            super(text, skin);

            this.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    partyManagementSystem.getPartyDetails().removeSkillFromInventoryOrParty(s);
                    partyManagementSystem.editMoney(price);
                    skillToBuyArray.removeValue(s, true);
                    populateShopTable();
                }
            });

        }

    }

}
