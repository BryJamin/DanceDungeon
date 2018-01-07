package com.bryjamin.dancedungeon.ecs.systems.input;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.EndBattleSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 07/01/2018.
 */

public class BattleEvent extends MapEvent {

    private Array<String> enemies = new Array<String>();

    public BattleEvent(String... enemies){
        this.enemies.addAll(enemies);
    }

    public Array<String> getEnemies() {
        return enemies;
    }

    public Bag<ComponentBag> convertEnemiesIntoComponentBags(){

        Bag<ComponentBag> enemyBags = new Bag<ComponentBag>();
        EnemyFactory enemyFactory = new EnemyFactory();

        for(String s : enemies){
            enemyBags.add(enemyFactory.get(s));
        }

        return enemyBags;


    }

    @Override
    public EventType getEventType() {
        return EventType.BATTLE;
    }

    @Override
    public void setUpEvent(World world) {

        world.getSystem(TurnSystem.class).setUp(TurnSystem.TURN.ALLY);
        world.getSystem(SelectedTargetSystem.class).reset();

        setUpPlayerLocations(world);
        setUpEnemyLocations(world, convertEnemiesIntoComponentBags());
    }


    @Override
    public boolean isComplete(World world) {
        return world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class)).getEntities().size() <= 0;
    }

    private void setPlayerCoordinate(CoordinateComponent coordinateComponent, int partyPosition) {

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


    private void setUpIntro(World world, Entity entity) {


        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Coordinates c = entity.getComponent(CoordinateComponent.class).coordinates;

        entity.getComponent(PositionComponent.class).position.set(
                tileSystem.getPositionUsingCoordinates(c.getX() - 4, c.getY(),
                        entity.getComponent(CenteringBoundaryComponent.class).bound)
        );

        entity.getComponent(MoveToComponent.class).movementPositions.add(tileSystem.getPositionUsingCoordinates(c,
                entity.getComponent(CenteringBoundaryComponent.class).bound));


    }


    private void setUpPlayerLocations(World world) {

        Bag<Entity> entityBag = world.getSystem(EndBattleSystem.class).getPlayerBag();

        for(int i = 0; i < entityBag.size(); i++){
            setPlayerCoordinate(entityBag.get(i).getComponent(CoordinateComponent.class), i);
            setUpIntro(world, entityBag.get(i));
        }
    }


    private void setUpEnemyLocations(World world, Bag<ComponentBag> enemies) {

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for (int i = 0; i < enemies.size(); i++) {
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
