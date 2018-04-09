package com.bryjamin.dancedungeon.factories.map.event;

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
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleDeploymentSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.EndBattleSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.DefeatAllEnemiesObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.SurviveObjective;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 07/01/2018.
 */

public class BattleEvent extends com.bryjamin.dancedungeon.factories.map.event.MapEvent {


    private AbstractObjective primaryObjective = new SurviveObjective(5);
    private AbstractObjective bonusObjective = new DefeatAllEnemiesObjective();



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

    public AbstractObjective getPrimaryObjective() {
        return primaryObjective;
    }

    public AbstractObjective getBonusObjective() {
        return bonusObjective;
    }

    @Override
    public EventType getEventType() {
        return EventType.BATTLE;
    }

    @Override
    public void setUpEvent(World world) {
        //world.getSystem(TurnSystem.class).setUp(TurnSystem.TURN.INTENT); //Run intent first since enemies do not exist yet. //TODO look into this
        world.getSystem(BattleScreenUISystem.class).reset();
        //setUpEnemyLocations(world, convertEnemiesIntoComponentBags());
    }


    @Override
    public boolean isComplete(World world) {
        return (primaryObjective.isComplete(world));
    }

    @Override
    public void cleanUpEvent(World world) {

    }

    @Override
    public boolean cleanUpComplete(World world) {
        return true;
    }

















}
