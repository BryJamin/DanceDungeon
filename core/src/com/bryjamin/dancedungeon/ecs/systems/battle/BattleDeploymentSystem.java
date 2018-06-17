package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.QueuedAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.QueuedInstantAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.SpawnerComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeploymentComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.factories.unit.UnitLibrary;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.unit.UnitData;
import com.bryjamin.dancedungeon.factories.unit.UnitFactory;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.observer.Observable;
import com.bryjamin.dancedungeon.utils.observer.Observer;


/**
 * This System is to used to setup the initial deployment of player characters
 * <p>
 * It scans for deployment zones and cycles through all characters until every characrter has been deployed
 * <p>
 * It also deploys enemy characters initially so players can see where they may want to best deploy their units
 * <p>
 */
public class BattleDeploymentSystem extends EntitySystem implements Observer{

    private TileSystem tileSystem;
    private ActionQueueSystem actionQueueSystem;
    private PlayerPartyManagementSystem playerPartyManagementSystem;

    private TurnSystem turnSystem;

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



    public Array<String> getNextEnemyWave(){

        Array<String> unitIdsToSpawn = new Array<>();

        if(battleEvent.getNumberOfWaves() > 0){

            if(battleEvent.getWaves().size > 0){
                for(String s : battleEvent.getWaves().removeFirst()){
                    unitIdsToSpawn.add(s.equals(BattleEvent.RANDOM_POOLED_UNIT) ? battleEvent.getFixedEnemyPool().random() : s);
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    unitIdsToSpawn.add(battleEvent.getFixedEnemyPool().size == 0 ? UnitLibrary.getRandomEnemyUnitID() : battleEvent.getFixedEnemyPool().random());
                }


            }
        }

        return unitIdsToSpawn;

    }


    @Override
    protected void initialize() {

        turnSystem.addPlayerTurnObserver(this);
        turnSystem.addEnemyTurnObserver(this);

        for(String s : getNextEnemyWave()){
            addEnemyUnit(new Array<>(tileSystem.getEnemySpawningLocations()), s);
        }

        deploymentLocations = new Array<>(tileSystem.getAllySpawningLocations());
        calculateUnitToBeDeployed();
        createDeploymentLocations();


    }

    private Entity addSpawnUnit(Array<Coordinates> spawningLocations, String unitId){

        Entity e = UnitFactory.baseSpawnBag(world, unitId);
        Coordinates selected = spawningLocations.random();
        spawningLocations.removeValue(selected, true);
        e.getComponent(CoordinateComponent.class).coordinates.set(selected);

        return e;

    }


    private Entity addEnemyUnit(Array<Coordinates> spawningLocations, String unitId){
        Entity e = UnitLibrary.getEnemyUnit(world, unitId);
        Coordinates selected = spawningLocations.random();
        spawningLocations.removeValue(selected, true);
        e.getComponent(CoordinateComponent.class).coordinates.set(selected);
        return e;
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

    @Override
    public void update(Object o) {
        if(!isTutorial) {

            switch (turnSystem.getTurn()){

                case INTENT:

                    IntBag toBeSpawned = world.getAspectSubscriptionManager().get(Aspect.all(CoordinateComponent.class, SpawnerComponent.class)).getEntities();



                    for(int i = 0; i < toBeSpawned.size(); i++){
                        final Entity e = world.getEntity(toBeSpawned.get(i));


                        actionQueueSystem.pushLastAction(new QueuedAction() {

                            Entity spawned;

                            @Override
                            public void act() {

                                //TODO is it possible for a null pointer?

                                spawned = UnitLibrary.getEnemyUnit(world, e.getComponent(SpawnerComponent.class).getUnitID());
                                spawned.getComponent(CoordinateComponent.class).coordinates.set(e.getComponent(CoordinateComponent.class).coordinates);
                                spawned.edit().add(new FadeComponent(new FadeComponent.FadeBuilder()
                                        .alpha(0)
                                        .fadeIn(true)
                                        .endless(false)
                                        .maximumDuration(0.2f)));

                                e.edit().add(new DeadComponent());
                            }

                            @Override
                            public boolean isComplete() {
                                return spawned.getComponent(DrawableComponent.class).drawables.getColor().a >= 1;
                            }
                        });
                    }

                    break;

                case PLAYER: //When switching to the player turn queue up the gltiches showing up.

                    for(final String s : getNextEnemyWave()) {
                        actionQueueSystem.pushLastAction(new QueuedAction() {

                            Entity e;

                            @Override
                            public void act() {
                                e = addSpawnUnit(new Array<>(tileSystem.getEnemySpawningLocations()), s);
                                e.edit().add(new FadeComponent(new FadeComponent.FadeBuilder()
                                        .alpha(0)
                                        .fadeIn(true)
                                        .endless(false)
                                        .maximumDuration(0.4f)));

                            }

                            @Override
                            public boolean isComplete() {
                                return e.getComponent(DrawableComponent.class).drawables.getColor().a >= 1;
                            }
                        });

                    }

                    break;



            }

        }
    }
}
