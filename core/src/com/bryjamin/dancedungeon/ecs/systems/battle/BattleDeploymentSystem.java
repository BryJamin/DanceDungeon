package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.DeploymentComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
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

    private boolean[] deployedArray = new boolean[PartyDetails.PARTY_SIZE];

    private BattleEvent battleEvent;

    private Array<Coordinates> enemySpawning;
    private Array<Coordinates> deploymentLocations;

    private UnitFactory unitFactory = new UnitFactory();


    private boolean processingFlag = true;


    public BattleDeploymentSystem(BattleEvent battleEvent) {
        super(Aspect.all(DeploymentComponent.class));
        this.battleEvent = battleEvent;
    }

    @Override
    protected void initialize() {


        EnemyFactory enemyFactory = new EnemyFactory();

        Array<Coordinates> spawningLocations = new Array<Coordinates>(tileSystem.getEnemySpawningLocations());

        //TODO Need to be built upon

        //TODO Currently enemeis are place randomly based on whther they are featured within the spawning pool of the event

        //TODO Should events determine whetehr they spawn something or should a system be a deicider?

        for(int i = 0; i < 3; i++){
            Entity e = BagToEntity.bagToEntity(world.createEntity(), enemyFactory.get(battleEvent.getEnemies().random()));
            Coordinates selected = tileSystem.getEnemySpawningLocations().random();
            spawningLocations.removeValue(selected, true);
            e.getComponent(CoordinateComponent.class).coordinates.set(selected);
        }

        deploymentLocations = new Array<Coordinates>(tileSystem.getAllySpawningLocations());

        createDeploymentLocations();

    }


    public void createDeploymentLocations(){

        for(final Coordinates c : deploymentLocations){
            Entity e = unitFactory.baseDeploymentZone(world, tileSystem.createRectangleUsingCoordinates(c), c);

            e.edit().add(new ActionOnTapComponent(new WorldAction() {
                @Override
                public void performAction(World world, Entity entity) {

                    for(int i = 0; i < deployedArray.length; i++){

                        if(!deployedArray[i]){
                            UnitMap unitMap = new UnitMap();
                            PartyDetails partyDetails = playerPartyManagementSystem.getPartyDetails();

                            if (partyDetails.getParty()[i] != null) {
                                deployedArray[i] = true;
                                UnitData unitData = partyDetails.getParty()[i];
                                ComponentBag player = unitMap.getUnit(unitData);
                                Entity e = BagToEntity.bagToEntity(world.createEntity(), player);
                                e.getComponent(CoordinateComponent.class).coordinates.set(c);
                                deploymentLocations.removeValue(c, false);

                                if(!deployedArray[deployedArray.length - 1]) {
                                    createDeploymentLocations();
                                }
                                clear();
                                break;
                            }

                        }

                    }

                    if(deployedArray[deployedArray.length - 1]){
                        processingFlag = false;
                        turnSystem.setProcessingFlag(true);
                    }



                }
            }));
        }


    }

    private void clear(){
        System.out.println("hmm");
        for(Entity e : this.getEntities()){
            e.edit().add(new DeadComponent());
            System.out.println("Inside");
        }
    }

    @Override
    protected void processSystem() {

    }

    public boolean isProcessing(){
        return processingFlag;
    }
}
