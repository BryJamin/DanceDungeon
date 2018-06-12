package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.World;

import java.util.Locale;

public class CompleteWithinObjective extends AbstractObjective {

    private int rounds = 3;
    private int trackedRounds = 3;

    public CompleteWithinObjective() {
        super(UpdateOn.END_TURN);
    }


    public CompleteWithinObjective(int rounds) {
        super(AbstractObjective.UpdateOn.END_TURN);
        this.rounds = rounds;
        this.trackedRounds = rounds;
    }

    @Override
    public String getDescription() {

        if(rounds == 1){
            return  String.format(Locale.ENGLISH, "Complete within %d turn", trackedRounds);
        } else {
            return String.format(Locale.ENGLISH, "Complete within %d turns", trackedRounds);
        }
    }

    @Override
    public boolean isComplete(World world) {
        return false;
    }


    @Override
    public void update(Object o) {
        trackedRounds--;
        if(trackedRounds < 0) trackedRounds = 0;
        super.update(o);
    }

    @Override
    public boolean isFailed(World world) {
        return trackedRounds <= 0;
    }

    @Override
    public CompleteWithinObjective clone() {
        return new CompleteWithinObjective(rounds);
    }
}
