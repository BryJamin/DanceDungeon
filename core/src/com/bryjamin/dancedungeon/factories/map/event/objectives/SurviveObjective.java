package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.World;

public class SurviveObjective extends AbstractObjective {

    private int rounds;
    private transient int trackedRounds;


    public SurviveObjective(){
        super();
    }

    public SurviveObjective(int rounds) {
        super(UpdateOn.END_TURN);
        this.rounds = rounds;
        this.trackedRounds = rounds;
    }

    @Override
    public String getDescription() {
        return "Survive for " + trackedRounds + " rounds";
    }

    @Override
    public boolean isComplete(World world) {
        return trackedRounds == 0;
    }

    @Override
    public void update(Object o) {
        trackedRounds--;
        super.update(o);
    }

    @Override
    public SurviveObjective clone() {
        return new SurviveObjective(rounds);
    }

}
