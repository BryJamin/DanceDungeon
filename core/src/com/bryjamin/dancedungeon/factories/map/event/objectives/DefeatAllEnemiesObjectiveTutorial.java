package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.systems.ui.TutorialSystem;

public class DefeatAllEnemiesObjectiveTutorial extends DefeatAllEnemiesObjective {

    @Override
    public boolean isComplete(World world) {
        return super.isComplete(world) && world.getSystem(TutorialSystem.class).getTutorialState() == TutorialSystem.TutorialState.END;
    }
}
