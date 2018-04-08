package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
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
public class BattleDeploymentSystem extends BaseSystem {

    private TileSystem tileSystem;
    private BattleEvent battleEvent;

    private Array<Coordinates> enemySpawning;

    private UnitFactory unitFactory = new UnitFactory();


    public BattleDeploymentSystem(BattleEvent battleEvent) {
        this.battleEvent = battleEvent;
    }

    @Override
    protected void initialize() {


        EnemyFactory enemyFactory = new EnemyFactory();

        Array<Coordinates> spawningLocations = new Array<Coordinates>(tileSystem.getEnemySpawningLocations());

        for(int i = 0; i < 3; i++){
            Entity e = BagToEntity.bagToEntity(world.createEntity(), enemyFactory.get(battleEvent.getEnemies().random()));

            Coordinates selected = tileSystem.getEnemySpawningLocations().random();
            spawningLocations.removeValue(selected, true);
            e.getComponent(CoordinateComponent.class).coordinates.set(selected);
        }



    }

    @Override
    protected void processSystem() {

    }
}
