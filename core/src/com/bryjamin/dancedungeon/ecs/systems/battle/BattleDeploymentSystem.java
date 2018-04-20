package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.Observer;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.DeploymentComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.InformationBannerSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;


/**
 * This System is to used to setup the initial deployment of player characters
 *
 * It scans for deployment zones and cycles through all characters until every characrter has been deployed
 *
 * It also deploys enemy characters initially so plays can see where they may want to best deploy their units
 *
 * //TODO this system will either rely on the TileSystem selected map, Or the map inserted into the constructor
 * //TODO this means this system MUST be placed beneath the TileSystem when used,
 *
 */
public class BattleDeploymentSystem extends EntitySystem {

    private TileSystem tileSystem;
    private TurnSystem turnSystem;
    private PlayerPartyManagementSystem playerPartyManagementSystem;
    private BattleScreenUISystem battleScreenUISystem;
    private StageUIRenderingSystem stageUIRenderingSystem;
    private InformationBannerSystem informationBannerSystem;
    private RenderingSystem renderingSystem;

    private int count;
    private boolean[] deployedArray = new boolean[PartyDetails.PARTY_SIZE];

    private BattleEvent battleEvent;

    private Array<Coordinates> enemySpawning;
    private Array<Coordinates> deploymentLocations;


    private Table deploymentTable;

    private UnitFactory unitFactory = new UnitFactory();

    public Array<Observer> observers = new Array<Observer>();

    private boolean processingFlag = true;

    private Skin uiSkin;

    public BattleDeploymentSystem(MainGame game, BattleEvent battleEvent) {
        super(Aspect.all(DeploymentComponent.class));
        this.battleEvent = battleEvent;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }

    @Override
    protected void initialize() {


        EnemyFactory enemyFactory = new EnemyFactory();

        Array<Coordinates> spawningLocations = new Array<Coordinates>(tileSystem.getEnemySpawningLocations());

        //TODO Need to be built upon

        //TODO Currently enemeis are place randomly based on whther they are featured within the spawning pool of the event

        //TODO Should events determine whetehr they spawn something or should a system be a deicider?

        for(int i = 0; i < 3; i++){

            if(battleEvent.getEnemies().size == 0) continue; //TODO this could be cleaner, if I fail to include enemies, an error should be printed.

            Entity e = BagToEntity.bagToEntity(world.createEntity(), enemyFactory.get(battleEvent.getEnemies().random()));
            Coordinates selected = tileSystem.getEnemySpawningLocations().random();
            spawningLocations.removeValue(selected, true);
            e.getComponent(CoordinateComponent.class).coordinates.set(selected);
        }

        deploymentLocations = new Array<>(tileSystem.getAllySpawningLocations());

        deploymentTable = new Table(uiSkin);

        stageUIRenderingSystem.stage.addActor(deploymentTable);

        createDeploymentLocations();


    }


    public void createDeploymentLocations(){


        for(int i = 0; i < deployedArray.length; i++) {
            if(!deployedArray[i]) {
                updateDeploymentTable(playerPartyManagementSystem.getPartyDetails().getParty()[i]);
                break;
            }
        }

        for(final Coordinates c : deploymentLocations){
            Entity e = unitFactory.baseDeploymentZone(world, tileSystem.createRectangleUsingCoordinates(c), c);

            e.edit().add(new ActionOnTapComponent(new WorldAction() {
                @Override
                public void performAction(World world, Entity entity) {

                    for(int i = 0; i < deployedArray.length; i++){

                        count = i + 1;

                        if(!deployedArray[i]){
                            UnitMap unitMap = new UnitMap();
                            PartyDetails partyDetails = playerPartyManagementSystem.getPartyDetails();


                            if (partyDetails.getParty()[i] != null) {
                                deployedArray[i] = true;
                                UnitData unitData = partyDetails.getParty()[i];

                                if(unitData.getStatComponent().health <= 0) continue;

                                ComponentBag player = unitMap.getUnit(unitData);
                                Entity e = BagToEntity.bagToEntity(world.createEntity(), player);
                                e.getComponent(CoordinateComponent.class).coordinates.set(c);
                                deploymentLocations.removeValue(c, false);

                                if(!deployedArray[deployedArray.length - 1]) {
                                    createDeploymentLocations();
                                } else {
                                    processingFlag = false;
                                    turnSystem.setProcessingFlag(true);
                                    deploymentTable.remove();
                                }
                                clear();

                                break;
                            }

                        }

                    }


                }
            }));
        }


    }



    public void updateDeploymentTable(UnitData unitData){

        if(deploymentTable.hasChildren()){
            deploymentTable.clear();
        }

       // deploymentTable.setDebug(true);
        deploymentTable.setWidth(stageUIRenderingSystem.stage.getWidth());
        deploymentTable.setHeight(Measure.units(15f));

        NinePatch patch = new NinePatch(renderingSystem.getAtlas().findRegion(TextureStrings.BORDER), 4, 4, 4, 4);
        deploymentTable.setBackground(new NinePatchDrawable(patch));

        deploymentTable.setPosition(0, 0);

        Label deployingLabel = new Label("Please Select Where To Deploy: ", uiSkin);

        deploymentTable.add(deployingLabel).pad(Padding.SMALL);

        deploymentTable.add(new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(unitData.icon)))).size(Measure.units(7.5f), Measure.units(7.5f));

    }


    private void clear(){
        for(Entity e : this.getEntities()){
            e.deleteFromWorld();
        }
    }

    @Override
    protected void processSystem() {

    }

    public boolean isProcessing(){
        return processingFlag;
    }

    public UnitData getDeployingUnit(){
        return playerPartyManagementSystem.getPartyDetails().getParty()[count];
    }

}
