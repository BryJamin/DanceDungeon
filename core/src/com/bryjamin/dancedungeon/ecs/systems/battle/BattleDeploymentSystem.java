package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeploymentComponent;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.observer.Observable;


/**
 * This System is to used to setup the initial deployment of player characters
 * <p>
 * It scans for deployment zones and cycles through all characters until every characrter has been deployed
 * <p>
 * It also deploys enemy characters initially so plays can see where they may want to best deploy their units
 * <p>
 * //TODO this system will either rely on the TileSystem selected map, Or the map inserted into the constructor
 * //TODO this means this system MUST be placed beneath the TileSystem when used,
 */
public class BattleDeploymentSystem extends EntitySystem {

    private TileSystem tileSystem;
    private PlayerPartyManagementSystem playerPartyManagementSystem;

    private int count;
    private boolean[] deployedArray = new boolean[PartyDetails.PARTY_SIZE];

    private BattleEvent battleEvent;
    private Array<Coordinates> deploymentLocations = new Array<>();

    private UnitFactory unitFactory = new UnitFactory();
    private UnitData unitToBeDeployed;

    public Observable observable = new Observable();

    private boolean processingFlag = true;

    private boolean isTutorial = false;

    public BattleDeploymentSystem(BattleEvent battleEvent, boolean isTutorial) {
        super(Aspect.all(DeploymentComponent.class));
        this.battleEvent = battleEvent;
        this.isTutorial = isTutorial;
    }




    @Override
    protected void initialize() {

        Array<Coordinates> spawningLocations = new Array<Coordinates>(tileSystem.getEnemySpawningLocations());

        for (int i = 0; i < 3; i++) {

            if (battleEvent.getEnemies().size == 0)
                continue; //TODO this could be cleaner, if I fail to include enemies, an error should be printed. Or featured in a test?

            Entity e = UnitLibrary.getEnemyUnit(world, battleEvent.getEnemies().random());

            Coordinates selected = tileSystem.getEnemySpawningLocations().random();
            spawningLocations.removeValue(selected, true);
            e.getComponent(CoordinateComponent.class).coordinates.set(selected);
        }

        deploymentLocations = new Array<>(tileSystem.getAllySpawningLocations());


        calculateUnitToBeDeployed();
        createDeploymentLocations();


    }


    /**
     * Calculates the next Unit to deployed. If a unit's health is zero it's place will be skipped
     * in the queue
     */
    private void calculateUnitToBeDeployed(){

        PartyDetails partyDetails = playerPartyManagementSystem.getPartyDetails();

        for (int i = 0; i < deployedArray.length; i++) {
            //Checks if the unit has already been deployed, or has the health to be deployed.
            if (!deployedArray[i]) {
                if (partyDetails.getParty()[i].getHealth() <= 0) {
                    deployedArray[i] = true;
                    continue;
                }

                unitToBeDeployed = partyDetails.getParty()[i];
                count = i;
                break;
            }
        }

    }


    /**
     * Creates UI entities in the Tiles where units can be deployed.
     */
    private void createDeploymentLocations() {

        for (final Coordinates c : deploymentLocations) {
            Entity e = unitFactory.baseDeploymentZone(world, tileSystem.createRectangleUsingCoordinates(c), c);

            e.edit().add(new ActionOnTapComponent(new WorldAction() {
                @Override
                public void performAction(World world, Entity entity) {
                    deployUnit(c);
                }
            }));
        }


    }


    /**
     * Converts the UnitData given into an Entity that is placed on the Map. ]
     *
     * Once a unit is placed, the next unit to be placed is calculated.
     * Observers are then updated.
     *
     * If no more units can be deployed this system stops processing.
     *
     */
    private void deployUnit(Coordinates c){
        if (unitToBeDeployed != null) {
            deployedArray[count] = true;

            Entity e = UnitLibrary.convertUnitDataIntoPlayerEntity(world, unitToBeDeployed);
            e.getComponent(CoordinateComponent.class).coordinates.set(c);
            deploymentLocations.removeValue(c, false);

            if (!deployedArray[deployedArray.length - 1]) { //If all units have been deployed, exit this system
                calculateUnitToBeDeployed(); //Setup next before notifying
                createDeploymentLocations();
                observable.notifyObservers(this);
            } else {
                processingFlag = false;
                observable.notifyObservers(this);
            }
            clear();
        }
    }


    /**
     * Clears DeploymentUI Entities from the Game World
     */
    private void clear() {
        for (Entity e : this.getEntities()) {
            e.deleteFromWorld();
        }
    }

    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }

    @Override
    protected void processSystem() {

        if(isTutorial) {
            processingFlag = false;
            observable.notifyObservers(this);
            clear();
        }

    }

    public Observable getObservers() {
        return observable;
    }

    public boolean isProcessing() {
        return processingFlag;
    }

    public boolean deploymentComplete(){
        return !processingFlag;
    }

    public UnitData getDeployingUnit() {
        return playerPartyManagementSystem.getPartyDetails().getParty()[count];
    }

}
