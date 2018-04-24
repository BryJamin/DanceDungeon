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
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.NinePatches;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
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
    private Table buyTable;

    private Table buyMenuSkillsTable;
    private Table buyMenuSkillDescriptionTable;



    private ButtonGroup<Button> skillButtons = new ButtonGroup<>();


    private MainGame game;
    private Viewport gameport;
    private Skin uiSkin;
    private ShopScreen shopScreen;
    private SkillLibrary skillLibrary;

    private Array<Skill> skillToBuyArray = new Array<>();
    private int sellingIndex = 0;



    private Array<Array<Skill>> skillsToSellArray = new Array<>();

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


        updateSkillsToSellArrays();


        createShopUI();


    }

    private void updateShopUI(){

        Stage stage = stageUIRenderingSystem.stage;

        container.clear();

        Label label = new Label("Welcome to the shop!", uiSkin);
        container.add(label).padTop(Measure.units(10f)).expandX();
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
                updateShopUI();
            }
        });

        TextButton switchToBuyButton = new TextButton("Buy", uiSkin);
        switchToBuyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                state = State.BUY;
                updateShopUI();
            }
        });

        Table tabTable = new Table(uiSkin);
        container.add(tabTable).width(stage.getWidth());

        tabTable.add(switchToBuyButton).width(Measure.units(15f)).expandX();
        tabTable.add(switchToSellButton).width(Measure.units(15f)).expandX();

        container.row();

        switch (state){
            case BUY:
                buyTable = new Table(uiSkin);
                buyTable.align(Align.bottom);
                buyTable.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas())));
                container.add(buyTable).width(stage.getWidth()).padRight(Padding.SMALL).padLeft(Padding.SMALL).height(Measure.units(32.5f)).expandY();
                updateBuyButtonTable(buyTable);

                break;
            case SELL:
                container.add(createSellScrollPane()).expandY();
                break;
        }

        container.row();
        container.add(leaveRestArea).width(stage.getWidth()).height(Measure.units(7.5f)).align(Align.bottom);





    }


    private void createShopUI(){

        Stage stage = stageUIRenderingSystem.stage;

        container = new Table();
        container.setDebug(StageUIRenderingSystem.DEBUG);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());

        container.align(Align.top);

        stage.addActor(container);

        updateShopUI();


    }


    public Table updateBuyMenuSkillTable(){

        buyMenuSkillsTable.clear();

        if(skillToBuyArray.size == 0){
            return buyMenuSkillsTable;
        }


        final float BUTTON_SIZE = Measure.units(5f);

        skillButtons.clear();

        for(final Skill s : skillToBuyArray){


            Stack itemButtonStack = new Stack();

            //Table for showing the icon and title of skill.
            Table inventoryInformation = new Table(uiSkin);
            inventoryInformation.setTouchable(Touchable.enabled);
            inventoryInformation.setDebug(StageUIRenderingSystem.DEBUG);
            inventoryInformation.setTouchable(Touchable.disabled);

            Image skillImage = new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(s.getIcon())));
            inventoryInformation.add(skillImage).height(BUTTON_SIZE).width(BUTTON_SIZE).padLeft(Padding.SMALL).padRight(Padding.SMALL);
            inventoryInformation.add(new Label(s.getName(), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT)).expandX();

            //Button for selecting which item is in focus
            final Button button = new Button(uiSkin, "inventory");
            button.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(button.isChecked()){
                        updateBuyMenuSkillDescriptionTable();
                    }
                }
            });

            skillButtons.add(button);

            itemButtonStack.add(button);
            itemButtonStack.add(inventoryInformation);

            buyMenuSkillsTable.add(itemButtonStack).width(Measure.units(22.5f)).height(BUTTON_SIZE + Measure.units(2f)).pad(0, 0, Measure.units(1.5f), Measure.units(1.5f)).expandX();


        }

        return buyMenuSkillsTable;

    }


    private Table updateBuyMenuSkillDescriptionTable(){
        buyMenuSkillDescriptionTable.clear();


        Skill s = skillToBuyArray.get(skillButtons.getCheckedIndex());

        Stack buyButtonStack = new Stack();

        BuyButton buyButton = new BuyButton("", uiSkin, s);
        buyButtonStack.add(buyButton);

        Table priceDisplay = new Table(uiSkin);
        priceDisplay.setTouchable(Touchable.disabled);
        priceDisplay.add(new Label("Buy ", uiSkin));
        priceDisplay.add(new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.ICON_MONEY)))).size(Measure.units(5f));
        priceDisplay.add(new Label("" + s.getStorePrice(), uiSkin));
        buyButtonStack.add(priceDisplay);

        Label description = new Label(s.getDescription(), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
        description.setWrap(true);
        description.setAlignment(Align.center);

        buyMenuSkillDescriptionTable.add(description).height(Measure.units(5f)).width(stageUIRenderingSystem.stage.getWidth()).align(Align.center);
        buyMenuSkillDescriptionTable.row();
        buyMenuSkillDescriptionTable.add(buyButtonStack).height(Measure.units(7.5f)).width(Measure.units(15f)).padBottom(Padding.MEDIUM);

        return buyMenuSkillDescriptionTable;
    }


    private void updateBuyButtonTable(Table buyTable){

        buyTable.clear();
        if(skillToBuyArray.size > 0){ //If there is something to sell.
            buyTable.add(updateBuyMenuSkillTable()).expandY();
            buyTable.row();
            buyTable.add(updateBuyMenuSkillDescriptionTable()).height(Measure.units(7.5f)).width(Measure.units(15f)).expandY();

        } else {
            buyTable.add(new Label("There is Nothing Left To Buy", uiSkin)).expandY();
        }

    }


    /**
     * Used to calculate which skills will be shown in the Sell menu.
     * As only 4 skills can be shown at a time, A an array of Skill arrays needs to be created
     * to monitor which menu is being shown.
     */
    private void updateSkillsToSellArrays(){

        skillsToSellArray.clear();

        Array<Skill> skillArray = new Array<>();
        skillArray.addAll(partyManagementSystem.getPartyDetails().getSkillInventory());
        skillArray.addAll(partyManagementSystem.getPartyDetails().getEquippedInventory());

        int count = 0;

        while(skillArray.size > 0){
            Array<Skill> skillArrayBlock = new Array<>();

            for(int i = 0; i < 4; i++){
                skillArrayBlock.add(skillArray.first());
                skillArray.removeIndex(0);
                if(skillArray.size == 0) break;
            }

            if(skillArrayBlock.size > 0) {
                skillsToSellArray.add(skillArrayBlock);
            }
        }

    }


    public Table createSellScrollPane(){


        Table sellTable = new Table(uiSkin);


        Button prev = new Button(uiSkin, "inventory");

        Button next = new Button(uiSkin, "inventory");

        prev.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                sellingIndex--;
                if(sellingIndex < 0){
                    sellingIndex = skillsToSellArray.size - 1;
                }

                refreshUI();
            }
        });

        next.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                sellingIndex++;

                if(sellingIndex > skillsToSellArray.size - 1){
                    sellingIndex = 0;
                }

                refreshUI();

            }

        });




        Table shopTable = new Table(uiSkin);


        //System.out.println("just before size " );
        if(skillsToSellArray.size == 0){
            Label youHaveNothingToSell = new Label("Your Inventory is Empty", uiSkin);
            sellTable.add(youHaveNothingToSell);
            return sellTable;
        } else if(skillsToSellArray.size - 1 < sellingIndex){//Checks which index needs to be shown in the sell screen
            sellingIndex = skillsToSellArray.size - 1;
        }



        sellTable.add(prev).width(Measure.units(5f)).height(Measure.units(5f)).padRight(Padding.SMALL);
        sellTable.add(shopTable);

        for(Skill s : skillsToSellArray.get(sellingIndex)){
            shopTable.row();
            addToShopTable(shopTable, s, false);
        }

        sellTable.add(next).width(Measure.units(5f)).height(Measure.units(5f)).padLeft(Padding.SMALL);;

        return sellTable;

    }


    private void addToShopTable(Table shopTable, Skill s, boolean isEquipped){


        shopTable.add(new Label("", uiSkin)).padBottom(Measure.units(5f));

        shopTable.add(new Image(renderingSystem.getAtlas().findRegion(s.getIcon()))).height(Measure.units(5f)).width(Measure.units(5f)).padRight(Measure.units(1.5f));
        shopTable.add(new Label(s.getName(), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT)).padRight(Measure.units(1.5f));

        String des = isEquipped ? s.getDescription() + " (Equipped)" : s.getDescription();


        Label skillDescription = new Label(des, uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
        skillDescription.setWrap(true);
        skillDescription.setAlignment(Align.center);

        shopTable.add(skillDescription).width(Measure.units(45f)).padRight(Measure.units(1.5f));




        Stack sellButtonStack = new Stack();

        int sell = s.getStorePrice() / 2;
        if(sell == 0) sell = 1;

        SellButton sellButton = new SellButton("", uiSkin, s, sell);
        sellButtonStack.add(sellButton);

        Table priceDisplay = new Table(uiSkin);
        priceDisplay.setTouchable(Touchable.disabled);
        priceDisplay.add(new Label("Sell ", uiSkin));
        priceDisplay.add(new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.ICON_MONEY)))).size(Measure.units(5f));

        priceDisplay.add(new Label("" + sell , uiSkin));
        sellButtonStack.add(priceDisplay);



        //SellButton sellButton = new SellButton("Sell ($" + s.getStorePrice() / 2 + ")", uiSkin, s, s.getStorePrice() / 2);
        shopTable.add(sellButtonStack).height(Measure.units(7.5f)).width(Measure.units(15f));


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
                    updateShopUI();
                }
            });

        }

        @Override
        public void act(float delta){

            if(partyManagementSystem.getPartyDetails().money < s.getStorePrice()){
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
                    updateSkillsToSellArrays();
                    refreshUI();
                }
            });

        }

    }

}
