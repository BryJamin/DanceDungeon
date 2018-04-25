package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.World;

import java.util.Locale;

public class CompleteWithinObjective extends AbstractObjective {

    private int rounds;



    public CompleteWithinObjective(int rounds) {
        super(AbstractObjective.UpdateOn.END_TURN);
        this.rounds = rounds;
    }

    public CompleteWithinObjective(Reward reward, int rounds) {
        super(AbstractObjective.UpdateOn.END_TURN);
        this.rounds = rounds;
        this.reward = reward;
    }

    @Override
    public String getDescription() {

        if(rounds == 1){
            return  String.format(Locale.ENGLISH, "Complete within %d turn", rounds);
        } else {
            return String.format(Locale.ENGLISH, "Complete within %d turns", rounds);
        }
    }

    @Override
    public boolean isComplete(World world) {
        return false;
    }


    @Override
    public void update(Object o) {
        rounds--;
        if(rounds < 0) rounds = 0;
        super.update(o);
    }

    @Override
    public boolean isFailed(World world) {
        return rounds <= 0;
    }
}
