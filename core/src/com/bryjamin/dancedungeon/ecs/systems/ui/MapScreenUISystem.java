package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

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
import com.bryjamin.dancedungeon.ecs.systems.MapCameraSystemFlingAndPan;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.input.MapInputSystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CameraMath;
import com.bryjamin.dancedungeon.utils.save.QuickSave;

import java.util.Locale;

/**
 * Created by BB on 02/03/2018.
 */

public class MapScreenUISystem extends BaseSystem {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private InformationBannerSystem informationBannerSystem;
    private RenderingSystem renderingSystem;
    private MapCameraSystemFlingAndPan camSys;
    private MainGame game;
    private GameMap gameMap;
    private PartyDetails partyDetails;
    private Viewport gameport;
    private Skin uiSkin;

    private Table container;
    private Table characterWindowContainer;
    private Table characterWindow;
    private Table viewInventoryAndQuickSaveTab;
    private Table selectCharacterButtonsTable;

    private ButtonGroup<Button> buttonButtonGroup;

    private DragAndDrop dragAndDrop;

    private static final float WINDOW_WIDTH = Measure.units(100f);
    private static final float WINDOW_HEIGHT = Measure.units(45f);


    public static final float INFORMATION_WINDOW_WIDTH = Measure.units(80f);

    private Table characterInformationTable;
    private Table equippedSkillsTable;

    private Table inventoryTable;
    private Table skillInformationTable;

    private UnitData selectedCharacter;


    private Array<Actor> equippedSkills = new Array<Actor>();


    public MapScreenUISystem(MainGame game, GameMap gameMap, PartyDetails partyDetails, Viewport gameport) {
        this.game = game;
        this.partyDetails = partyDetails;
        this.gameMap = gameMap;
        this.gameport = gameport;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }


    @Override
    protected void initialize() {

        Stage stage = stageUIRenderingSystem.stage;


        container = new Table(uiSkin);
        stage.addActor(container);

        //Initialize Container for View Inventory and Quick Save Buttons
        container.setDebug(StageUIRenderingSystem.DEBUG);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());
        container.align(Align.bottom);
        container.row();

        //VIEW INVENTORY AND BOTTOM BUTTONS
        viewInventoryAndQuickSaveTab = new Table(uiSkin);
        viewInventoryAndQuickSaveTab.setDebug(StageUIRenderingSystem.DEBUG);
        viewInventoryAndQuickSaveTab.align(Align.center);
        container.add(viewInventoryAndQuickSaveTab).height(Measure.units(7.5f)).width(stage.getWidth());


        TextButton quickSave = new TextButton("Quick Save", uiSkin);

        quickSave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if(characterWindow.isVisible()){
                    closeCharacterWindow();
                }


                QuickSave.quickSave(gameMap, partyDetails);
            }
        });

        final TextButton viewInventory = new TextButton("View Inventory", uiSkin);

        viewInventory.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (characterWindowContainer.isVisible()) {
                    closeCharacterWindow();
                    world.getSystem(MapInputSystem.class).closedMenu();
                } else {

                    System.out.println(selectedCharacter == null);

                    if(selectedCharacter == null) { //Default the menu opens to the first character in the party.
                        openCharacterWindow(partyDetails.getParty()[0]);
                    } else {
                        openCharacterWindow(selectedCharacter);
                    }
                    world.getSystem(MapInputSystem.class).openMenu();
                }
            }
        });

        viewInventory.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                viewInventory.setText(characterWindowContainer.isVisible() ? "Close Inventory" : "View Inventory");
                return false;
            }
        });

        viewInventoryAndQuickSaveTab.add(quickSave).expandX().width(Measure.units(35f)).height(Measure.units(6.5f));
        viewInventoryAndQuickSaveTab.add(viewInventory).expandX().width(Measure.units(35f)).height(Measure.units(6.5f));




        characterWindowContainer = new Table();
        characterWindowContainer.setVisible(false);
        characterWindowContainer.setWidth(stage.getWidth());
        characterWindowContainer.setHeight(stage.getHeight());
        characterWindowContainer.align(Align.top);

        stage.addActor(characterWindowContainer);

        characterWindow = new Table(uiSkin);
        characterWindowContainer.add(characterWindow).size(WINDOW_WIDTH, WINDOW_HEIGHT).pad(Padding.MEDIUM, Padding.SMALL, Padding.SMALL, Padding.SMALL);
        characterWindow.setDebug(StageUIRenderingSystem.DEBUG);
        characterWindow.align(Align.top);



        Table tempTable = new Table(uiSkin);
        tempTable.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas())).tint(new Color(1, 1, 1, 0.95f)));
        characterWindow.add(tempTable).width(WINDOW_WIDTH);

        selectCharacterButtonsTable = new Table(uiSkin);
        tempTable.add(selectCharacterButtonsTable).width(Measure.units(15f)).height(characterWindow.getHeight());
        createCharacterSelectionForInventoryWindow(selectCharacterButtonsTable);

        characterInformationTable = new Table(uiSkin);
        tempTable.add(characterInformationTable).width(INFORMATION_WINDOW_WIDTH).expandX();

        skillInformationTable = new Table(uiSkin);
        //characterWindow.add(skillInformationTable).width(Measure.units(30f));

        characterWindow.row();


        inventoryTable = new Table(uiSkin);
        inventoryTable.align(Align.top);
        inventoryTable.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas())).tint(new Color(1, 1, 1, 0.95f)));
        characterWindow.add(inventoryTable).width(WINDOW_WIDTH).height(Measure.units(10f)).colspan(4).padTop(Measure.units(2.5f));


    }

    @Override
    protected void processSystem() {

        container.setPosition(CameraMath.getBtmLftX(gameport), CameraMath.getBtmY(gameport.getCamera()));

        if (characterWindowContainer.isVisible()) {
            characterWindowContainer.setPosition(CameraMath.getBtmLftX(gameport), CameraMath.getBtmY(gameport.getCamera()));
        }
    }

    //TODO messy?
    public void updateInformation() {
        container.remove();
        container.clear();
        initialize();
    }



    private Table createCharacterSelectionForInventoryWindow(Table selectCharacterButtonsTable){

        //SELECT CHARACTER INVENTORY TABLE

        selectCharacterButtonsTable.align(Align.center);

        buttonButtonGroup = new ButtonGroup<>();
        buttonButtonGroup.setMinCheckCount(1);
        buttonButtonGroup.setMaxCheckCount(1);
        buttonButtonGroup.setUncheckLast(true);

        for(final UnitData unit : partyDetails.getParty()){

            Stack stack = new Stack();
            Table t = new Table();
            t.setTouchable(Touchable.disabled);
            t.add(new Image(renderingSystem.getAtlas().findRegion(unit.icon))).size(Measure.units(5f));

            final Button button = new Button(uiSkin, "inventory");

            stack.add(button);
            stack.add(t);

            selectCharacterButtonsTable.add(stack).size(Measure.units(8f)).padBottom(Padding.SMALL);

            button.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    updateSelectedCharacterTable(characterInformationTable, unit);
                    updateInventoryTable(inventoryTable);
                    updateSkillInformationTable(skillInformationTable, null);
                    selectedCharacter = unit;
                }

            });

            if(selectedCharacter == unit){ //When re-opening the menu it opens the previously Selected Character
                button.setChecked(true);
            }

            selectCharacterButtonsTable.row();

            buttonButtonGroup.add(button);
        }


        return selectCharacterButtonsTable;

    }


    /**
     * Opens the character window and displays a the given characters skills and health
     *
     * As well as buttons that allow you to switch to other characters
     *
     * @param unitData
     */
    private void openCharacterWindow(final UnitData unitData) {

        this.selectedCharacter = unitData;

        final Stage stage = stageUIRenderingSystem.stage;

        dragAndDrop = new DragAndDrop();
        characterWindowContainer.setVisible(true);
        //CHARACTER WINDOW TABLE
        //LEFT SIDE
        //CHARACTER PANE
        updateSelectedCharacterTable(characterInformationTable, unitData);
        //CHARACTER PANE END
        //INVENTORY PANE
        updateInventoryTable(inventoryTable);
        //--------------------- INVENTORY PANE END ----------------------------//
        //RIGHT SIDE - SKILL INFORMATION, WHEN PLAYER CLICKS A SKILL ICON.
        updateSkillInformationTable(skillInformationTable, null);

        //Adds A listenerer to the container to monitor if the player taps outside the window, in order to close it
        stage.addListener(new InputListener() { //Tracks taps outside of the character Window in order to close window


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                closeCharacterWindow();
                stage.removeListener(this);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                //Checks if the touch is outside the Character Window
       /*         if (x < characterWindow.getX() || x > characterWindow.getX() + characterWindow.getWidth()
                        || y < characterWindow.getY() || y > characterWindow.getY() + characterWindow.getHeight()) {

                    if(in)

                    return true;
                }
*/
                return false;
            }
        });


    }


    private void updateSelectedCharacterTable(Table selectedCharacterTable, UnitData unitData){

        if(selectedCharacterTable.hasChildren()){
            selectedCharacterTable.clear();
        }

        Label label = new Label(unitData.name, uiSkin);
        selectedCharacterTable.add(label).expandX().colspan(5);
        selectedCharacterTable.row();

        Image portrait = new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(unitData.icon)));
        selectedCharacterTable.add(portrait).size(Measure.units(7.5f)).expandX();

        int max = unitData.getStatComponent().maxHealth;
        int current = unitData.getStatComponent().health;

        //selectedCharacterTable.row();
        selectedCharacterTable.add(new Label(String.format(Locale.ENGLISH, "HP %s/%s", current, max), uiSkin)).expandX();
        selectedCharacterTable.add(new Label(String.format(Locale.ENGLISH, "Spd %s", unitData.getStatComponent().movementRange), uiSkin)).expandX();

        //Skills Table Row / Upgrades
        //selectedCharacterTable.row();
        //selectedCharacterTable.add(new Label("Skills", uiSkin)).colspan(5).expandX();
        selectedCharacterTable.row();

        equippedSkillsTable = new Table(uiSkin);
        selectedCharacterTable.add(equippedSkillsTable).colspan(5).width(selectedCharacterTable.getWidth()).fillX().height(Measure.units(20f));
        equippedSkillsTable.setDebug(StageUIRenderingSystem.DEBUG);
        updateEquippedSkillTable(equippedSkillsTable, unitData);

    }


    /**
     * Clears and updates the inventory Table.
     *
     * This is usually triggers when a skill is dragged into either the inventory table or an inventory slot.
     *
     * @param inventoryTable
     */
    private void updateInventoryTable(Table inventoryTable){

        if(inventoryTable == null) return;

        if(inventoryTable.hasChildren()){
            inventoryTable.clear();
        }

        final float BUTTON_SIZE = Measure.units(5f);

        Label title = new Label("Inventory (Drag and Drop to Equip)", uiSkin);

        inventoryTable.add(title).expandX().padBottom(Padding.SMALL).colspan(4);
        inventoryTable.row();

        for(int i = 0; i < PartyDetails.MAX_INVENTORY; i++){

            //inventoryTable.row();

            Table inventoryInformation = new Table(uiSkin);
            inventoryInformation.setTouchable(Touchable.enabled);
            inventoryInformation.setDebug(StageUIRenderingSystem.DEBUG);
            inventoryInformation.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas())));
            inventoryTable.add(inventoryInformation).width(Measure.units(22.5f)).height(BUTTON_SIZE + Measure.units(2f)).expandX();

            try {

                Skill s = partyDetails.getSkillInventory().get(i);

                final Button skillButton = new Button(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(s.getIcon())));
                inventoryInformation.add(skillButton).height(BUTTON_SIZE).width(BUTTON_SIZE).padLeft(Padding.SMALL).padRight(Padding.SMALL);
                inventoryInformation.add(new Label(s.getName(), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT)).expandX();
                skillButton.addListener(new SelectedActorListener(s));
                dragAndDrop.addSource(new InventorySource(inventoryInformation, s, i));
                dragAndDrop.addTarget(new InventoryTarget(inventoryInformation, s, i));

            } catch (IndexOutOfBoundsException e){
                inventoryInformation.add(new Label("Empty", uiSkin, Fonts.LABEL_STYLE_SMALL_FONT)).expandX();
                dragAndDrop.addTarget(new InventoryTarget(inventoryInformation, null, i));
            }
        }


    }


    /**
     * Used for updating the Table that contains all of a characters equipped skills.
     * @param equippedSkillsTable
     * @param unitData
     */
    private void updateEquippedSkillTable(Table equippedSkillsTable, UnitData unitData){

        float BUTTON_SIZE = Measure.units(7.5f);

        if(equippedSkillsTable.hasChildren()){
            equippedSkillsTable.clear();
        }

        for(int i = 0; i < UnitData.MAXIMUM_SKILLS; i++){

            equippedSkillsTable.row();

            try {
                final Skill s = unitData.getSkillsComponent().skills.get(i);

                if (s != null) {

                    Table skillTable = new Table(uiSkin);
                    skillTable.align(Align.left);
                    skillTable.setTouchable(Touchable.enabled);
                    skillTable.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas())));

                    equippedSkillsTable.add(skillTable).width(INFORMATION_WINDOW_WIDTH).height(BUTTON_SIZE).padBottom(Padding.SMALL);

                    final Button skillButton = new Button(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(s.getIcon())));
                    skillTable.add(skillButton).size(BUTTON_SIZE - Measure.units(1f)).padRight(Padding.MEDIUM);

                    Label title = new Label(s.getName(), uiSkin);
                    skillTable.add(title).padRight(Padding.MEDIUM).expandX();

                    Label description = new Label(s.getDescription(), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
                    description.setWrap(true);
                    skillTable.add(description).width(Measure.units(45));

                    equippedSkills.add(skillButton);

                    dragAndDrop.addSource(new SkillSource(skillTable, s, i));
                    dragAndDrop.addTarget(new EquippedTarget(skillTable, s, i));
                }

            } catch (IndexOutOfBoundsException e){

                Table skillNotSetTable = new Table(uiSkin);
                skillNotSetTable.setTouchable(Touchable.enabled);
                skillNotSetTable.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas())));

                Label skillNotSet = new Label("Equipment Slot is Empty", uiSkin);
                skillNotSet.setAlignment(Align.center);
                skillNotSetTable.add(skillNotSet);

                equippedSkillsTable.add(skillNotSetTable).height(BUTTON_SIZE).padBottom(Padding.SMALL).expandX().fillX();
                dragAndDrop.addTarget(new EquippedTarget(skillNotSetTable, null, i));
            }


        }

    }



    /**
     * Sets up the skill information table with the new skill supplied to it.
     *
     * This table displays both the skill'skill title and it'skill description.
     *
     * @param skillInformationTable
     * @param s
     * @return
     */
    private Table updateSkillInformationTable(Table skillInformationTable, Skill s){

        if(skillInformationTable.hasChildren()){
            skillInformationTable.clear();
        }

        if(s == null){

            Label message = new Label("You can view skill information here by tapping on the icons", uiSkin);
            message.setWrap(true);
            message.setAlignment(Align.center);


            skillInformationTable.add(message).expandY().expandX().fill();


        } else {

            Label title = new Label(s.getName(), uiSkin);
            skillInformationTable.add(title).padBottom(Padding.SMALL);
            skillInformationTable.row();

            Label decription = new Label(s.getDescription(), uiSkin);
            decription.setWrap(true);
            decription.setAlignment(Align.center);

            skillInformationTable.add(decription).expandY().expandX().fill();

        }

        return skillInformationTable;


    }

    private void closeCharacterWindow() {
        if (characterWindowContainer == null) return;
        //characterWindowContainer.clear();
        characterWindowContainer.setVisible(false);
        world.getSystem(MapInputSystem.class).closedMenu();
    }


    /**
     * Listener for when a skill is tapped.
     *
     * It triggers and updates the table used for displaying skill descriptions
     *
     */
    private class SelectedActorListener extends ChangeListener {

        private Skill s;

        private SelectedActorListener(Skill s){
            this.s = s;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            updateSkillInformationTable(skillInformationTable, s);
        }
    }




    private class SkillSource extends DragAndDrop.Source {

        private Skill skill;
        private int index;
        private Actor actor;
        public boolean isEquipped = true;

        public SkillSource(Actor actor, Skill skill, int index) {
            super(actor);
            this.skill = skill;
            this.actor = actor;
            this.index = index;
        }

        DragAndDrop.Payload payload = new DragAndDrop.Payload();

        @Override
        public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {

            Image i = new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(skill.getIcon())));
            i.setHeight(Measure.units(17.5f));
            i.setWidth(Measure.units(17.5f));

            payload.setDragActor(i);
            dragAndDrop.setDragActorPosition(i.getWidth() / 2, -i.getHeight() / 2);
            //dragAndDrop.setDragActorPosition(-(i.getWidth()/2), i.getHeight()/2);
            payload.setObject(this);

            return payload;
        }


        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
            super.dragStop(event, x, y, pointer, payload, target);
        }
    }

    private class InventorySource extends SkillSource {

        public InventorySource(Actor actor, Skill s, int index) {
            super(actor, s, index);
            this.isEquipped = false;
        }
    }




    private class EquippedTarget extends DragAndDrop.Target {

        private Skill skill;
        private int index;

        public EquippedTarget(Actor actor, Skill s, int index) {
            super(actor);

            this.skill = s;
            this.index = index;

        }

        @Override
        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
            return true;
        }

        @Override
        public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

            SkillSource es =  (SkillSource) source;

            if(es.isEquipped){

            } else {

                Skill inventorySkill = es.skill;
                ; //selectedCharacter.getSkillsComponent().skills.get(es.index);

                if(skill != null) { //If the Equipped slot is Empty, add the New Skill into the Slot.

                    selectedCharacter.getSkillsComponent().skills.set(index, inventorySkill);
                    partyDetails.getSkillInventory().set(es.index, skill);
                } else { //If Equipped slot is null add into the player'skill equipped.
                    selectedCharacter.getSkillsComponent().skills.add(inventorySkill);
                    partyDetails.getSkillInventory().removeValue(inventorySkill, true);
                }

                updateInventoryTable(inventoryTable);
                updateEquippedSkillTable(equippedSkillsTable, selectedCharacter);

            }


        }
    }




    /**
     * Used for Drag And Drop. This class is for when an Inventory item is the target.
     */
    private class InventoryTarget extends DragAndDrop.Target {


        private Skill skill;
        private int index;

        public InventoryTarget(Actor actor, Skill s, int index) {
            super(actor);
            this.skill = s;
            this.index = index;
        }

        @Override
        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
            return true;
        }

        @Override
        public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

            SkillSource es =  (SkillSource) source;

            if(es.isEquipped){

                Skill old = es.skill; //selectedCharacter.getSkillsComponent().skills.get(es.index);


                if(skill != null) {
                    selectedCharacter.getSkillsComponent().skills.set(es.index, skill);
                    partyDetails.getSkillInventory().set(index, old);
                } else {
                    selectedCharacter.getSkillsComponent().skills.removeIndex(es.index);
                    partyDetails.getSkillInventory().add(old);
                }

                updateInventoryTable(inventoryTable);
                updateEquippedSkillTable(equippedSkillsTable, selectedCharacter);

            } else {




            }
        }
    }


}