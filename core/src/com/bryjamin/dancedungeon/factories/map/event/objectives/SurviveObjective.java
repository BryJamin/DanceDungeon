package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.World;

public class SurviveObjective extends AbstractObjective {

    private int rounds;

    public SurviveObjective(int rounds) {
        super(UpdateOn.END_TURN);
        this.rounds = rounds;
    }

    @Override
    public String getDescription() {
        return "Survive for " + rounds + " rounds";
    }

    @Override
    public boolean isComplete(World world) {
        return rounds == 0;
    }

    @Override
    public void update(Object o) {
        rounds--;
        super.update(o);
    }

}
