package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.Aspect;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;

public class DefeatAllEnemiesObjective extends AbstractObjective {

    public DefeatAllEnemiesObjective() {
        super(UpdateOn.ENEMY_DEATH);
    }


    public DefeatAllEnemiesObjective(Reward reward) {
        super(UpdateOn.ENEMY_DEATH);
    }

    @Override
    public String getDescription() {
        return "Defeat All Enemies";
    }

    @Override
    public boolean isComplete(World world) {
        return world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class)).getEntities().size() == 0;
    }

    @Override
    public void update(Object o) {
        super.update(o);
    }

    @Override
    public DefeatAllEnemiesObjective clone() {
        return new DefeatAllEnemiesObjective();
    }

}
