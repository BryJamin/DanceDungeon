package com.bryjamin.dancedungeon.factories.map.event;

import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.assets.MapData;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.CompleteWithinObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.DefeatAllEnemiesObjective;

/**
 * Created by BB on 07/01/2018.
 */

public class BattleEvent extends MapEvent {


    public static final String RANDOM_POOLED_UNIT = "random_pooled_unit";

    //Id of the Event
    private String id = "unidentified";

    //Map used for the event (Maybe array for multiple map selections?)
    private String mapLocation = MapData.MAP_1;


    private AbstractObjective primaryObjective = new DefeatAllEnemiesObjective();
    private AbstractObjective[] bonusObjectives = new AbstractObjective[]{new CompleteWithinObjective(7)};

    //Enemies that can be spawned inside of the event
    private Array<String> fixedEnemyPool = new Array<String>();

    //TODO Variable for determining which random enemies can be drawn from? A difficulty variable?

    //The Fixed number of the waves that can be inside of the event
    private Queue<Array<String>> waves = new Queue<>();

    //Assuming the rest of the waves are random and only one wave is fixed. This is the maximum number of waves that will be spawned
    private int numberOfWaves = 3;

    public BattleEvent(){ }

    public BattleEvent(String... fixedEnemyPool){
        this.fixedEnemyPool.addAll(fixedEnemyPool);
    }


    private enum Objective {
        SURVIVE(5), BATTLE, BOMBS;
        private int value;

        Objective(){}

        Objective(int value){
            this.value = value;
        }

    }


    public BattleEvent(BattleEvent be){

        this.mapLocation = be.mapLocation;
        this.primaryObjective = be.primaryObjective.clone();
        this.bonusObjectives = be.bonusObjectives.clone();

        //TODO create a 'reset' inside of the Objectives? Not sure.
        this.fixedEnemyPool.addAll(be.fixedEnemyPool);
    }

    private Objective objective = Objective.SURVIVE;


    public Array<String> getFixedEnemyPool() {
        return fixedEnemyPool;
    }

    public Queue<Array<String>> getWaves() {
        return waves;
    }

    public AbstractObjective getPrimaryObjective() {
        return primaryObjective;
    }

    public AbstractObjective[] getBonusObjectives() {
        return bonusObjectives;
    }

    @Override
    public EventType getEventType() {
        return EventType.BATTLE;
    }

    @Override
    public boolean isComplete(World world) {
        return (primaryObjective.isComplete(world));
    }

    public String getId() {
        return id;
    }

    public String getMapLocation() {
        return mapLocation;
    }



    public int getNumberOfWaves() {
        return numberOfWaves;
    }

    public void decreaseNumberOfWaves(){
        numberOfWaves--;
    }


}
