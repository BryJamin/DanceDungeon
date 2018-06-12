package com.bryjamin.dancedungeon.factories.map.event;

import com.artemis.World;
import com.bryjamin.dancedungeon.assets.MapData;
import com.bryjamin.dancedungeon.ecs.systems.ui.TutorialSystem;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.DefeatAllEnemiesObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.SurviveObjective;

public class TutorialEvent extends BattleEvent{

    public TutorialEvent(){

        BattleEvent battleEvent = new BattleEvent();

        //TODO Tutorial is dead
    }


    private static class DefeatAllEnemiesObjectiveTutorial extends DefeatAllEnemiesObjective {

        public DefeatAllEnemiesObjectiveTutorial(Reward reward) {
            super(reward);
        }

        @Override
        public boolean isComplete(World world) {
            return super.isComplete(world) && world.getSystem(TutorialSystem.class).getTutorialState() == TutorialSystem.TutorialState.END;
        }
    }

}
