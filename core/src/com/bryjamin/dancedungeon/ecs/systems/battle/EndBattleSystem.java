package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.systems.input.BattleEvent;
import com.bryjamin.dancedungeon.ecs.systems.input.GameMap;
import com.bryjamin.dancedungeon.ecs.systems.input.MapEvent;
import com.bryjamin.dancedungeon.factories.player.Unit;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.screens.battle.BattleDetails;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 28/11/2017.
 */

public class EndBattleSystem extends EntitySystem {

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerControlledComponent> pcMapper;


    private Bag<Entity> playerBag = new Bag<Entity>();
    private Bag<Entity> enemyBag = new Bag<Entity>();

    private MainGame game;

    private BattleDetails battleDetails;
    private GameMap gameMap;

    private boolean processingFlag = true;


    private MapEvent.EventType currentEventType;


    public EndBattleSystem(MainGame game, GameMap gameMap, BattleDetails battleDetails) {
        super(Aspect.one(EnemyComponent.class, PlayerControlledComponent.class));
        this.battleDetails = battleDetails;
        this.game = game;
        this.gameMap = gameMap;
        //battleDetails.getPlayerParty().


    }

    @Override
    protected void initialize() {
        //setupEvent(gameMap.getNextEvent());
        currentEventType = MapEvent.EventType.BATTLE;
        setUpEnemyLocations(world, ((BattleEvent) gameMap.getNextEvent()).convertEnemiesIntoComponentBags());
        setUpPlayerLocations(world, battleDetails);
    }



    @Override
    protected void processSystem() {


        switch (currentEventType){
            case BATTLE:
            case BOSS:

                //If all enemies have been defeated

                if(enemyBag.isEmpty() && gameMap.getMapEvents().size <= 0){
                    ((BattleScreen) game.getScreen()).victory();
                } else if(enemyBag.isEmpty()){ //Continue to the next battle arena
                    next();
                }

                break;
        }


        if(playerBag.isEmpty()){
            ((BattleScreen) game.getScreen()).defeat();
        }

    }


    /**
     * Should be called when the next event is ready to be called
     */
    public void next(){
        MapEvent currentMapEvent = gameMap.getNextEvent();
        setupEvent(currentMapEvent);
    };

    private void setupEvent(MapEvent mapEvent){

     //   mapEvent.setUpEvent(world);
     //   currentEventType = mapEvent.getEventType();


        if(mapEvent instanceof BattleEvent){

            world.getSystem(TurnSystem.class).setUp(TurnSystem.TURN.ALLY);
            world.getSystem(SelectedTargetSystem.class).reset();

            setUpEnemyLocations(world, ((BattleEvent) mapEvent).convertEnemiesIntoComponentBags());

            for(int i = 0; i < playerBag.size(); i++){
                setPlayerCoordinate(playerBag.get(i).getComponent(CoordinateComponent.class), i);
                setUpIntro(playerBag.get(i));
            }

            currentEventType = MapEvent.EventType.BATTLE;
            mapEvent.setUpEvent(world);
            currentEventType = mapEvent.getEventType();

        } else {
            next();
        }


    }


    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }





    @Override
    public void inserted(Entity e) {
        if(enemyMapper.has(e)) enemyBag.add(e);
        if(pcMapper.has(e)) playerBag.add(e);
    }

    @Override
    public void removed(Entity e) {
        if(enemyMapper.has(e)) enemyBag.remove(e);
        if(pcMapper.has(e)) playerBag.remove(e);
    }


    public void endBattle(){

    }



    private void setPlayerCoordinate(CoordinateComponent coordinateComponent, int partyPosition){

        Coordinates c = coordinateComponent.coordinates;
        coordinateComponent.freePlacement = true;

        switch (partyPosition) {
            case 0:
                c.set(2, 2);
                break;
            case 1:
                c.set(1, 3);
                break;
            case 2:
                c.set(1, 1);
                break;
            case 3:
                c.set(0, 2);
                break;
        }


    }


    private void setUpIntro(Entity entity){


        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Coordinates c = entity.getComponent(CoordinateComponent.class).coordinates;

        entity.getComponent(PositionComponent.class).position.set(
                tileSystem.getPositionUsingCoordinates(c.getX() - 4, c.getY(),
                        entity.getComponent(CenteringBoundaryComponent.class).bound)
        );

        entity.getComponent(MoveToComponent.class).movementPositions.add(tileSystem.getPositionUsingCoordinates(c,
                entity.getComponent(CenteringBoundaryComponent.class).bound));


    }


    private void setUpPlayerLocations(World world, BattleDetails battleDetails){

        UnitMap unitMap = new UnitMap();

        for(int i = 0; i < battleDetails.getPlayerParty().size; i++) {

            if (battleDetails.getPlayerParty().get(i) != null) {

                Unit unit = battleDetails.getPlayerParty().get(i);
                ComponentBag player = unitMap.getUnit(unit);
                Entity entity = BagToEntity.bagToEntity(world.createEntity(), player);

                setPlayerCoordinate(player.getComponent(CoordinateComponent.class), i);
                setUpIntro(entity);

            }

        }

    }



    private void setUpEnemyLocations(World world, Bag<ComponentBag> enemies){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for(int i = 0; i < enemies.size(); i++) {
            ComponentBag enemy = enemies.get(i);

            if (enemy != null) {
                Coordinates c = enemy.getComponent(CoordinateComponent.class).coordinates;
                enemy.getComponent(CoordinateComponent.class).freePlacement = true;


                switch (i) {
                    case 0:
                        c.set(tileSystem.getMaxX() - 2, 2);
                        break;
                    case 1:
                        c.set(tileSystem.getMaxX() - 1, 3);
                        break;
                    case 2:
                        c.set(tileSystem.getMaxX() - 1, 1);
                        break;
                    case 3:
                        c.set(tileSystem.getMaxX(), 2);
                        break;
                }



                enemy.getComponent(PositionComponent.class).position.set(
                        tileSystem.getPositionUsingCoordinates(c.getX() + 4, c.getY(),
                                enemy.getComponent(CenteringBoundaryComponent.class).bound)
                );

                enemy.getComponent(MoveToComponent.class).movementPositions.add(tileSystem.getPositionUsingCoordinates(c,
                        enemy.getComponent(CenteringBoundaryComponent.class).bound));

                BagToEntity.bagToEntity(world.createEntity(), enemy);

            }

        }


    }



}
