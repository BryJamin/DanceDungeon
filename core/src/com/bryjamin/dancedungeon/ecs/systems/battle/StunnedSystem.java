package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.bryjamin.dancedungeon.Observer;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StunnedComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyIntentComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;

/**
 * System used to track when a unit gets stunned.
 *
 * This System also removes any 'intent' attacks that the unit had.
 *
 * As due to being stunned the unit should not be able to attack
 */
public class StunnedSystem extends EntitySystem implements Observer{


    private EnemyIntentSystem enemyIntentSystem;
    private TurnSystem turnSystem;

    ComponentMapper<StatComponent> statM;
    private ComponentMapper<StoredSkillComponent> storedSkillM;

    boolean processingFlag = false;

    public StunnedSystem() {
        super(Aspect.all(StatComponent.class, StunnedComponent.class));
    }

    @Override
    protected void initialize() {
        turnSystem.addNextTurnObserver(this);
    }

    @Override
    public void inserted(Entity e) {
        processingFlag = true;

        if(storedSkillM.has(e)){
            e.edit().remove(StoredSkillComponent.class);
            enemyIntentSystem.updateIntent();
        }

    }

    @Override
    public void removed(Entity e) {

    }

    @Override
    protected void processSystem() {

    }


    @Override
    protected boolean checkProcessing() {
        return super.checkProcessing();
    }

    @Override
    public void onNotify() {

/*
        if(this.getEntities().size() == 0) {
            processingFlag = false;
            return;
        }
*/

        for(Entity e : this.getEntities()){
            StatComponent statComponent = e.getComponent(StatComponent.class);
            statComponent.stun--;

            System.out.println("STUN: " + statComponent.stun);

            if(statComponent.stun <= 0){
                e.edit().remove(StunnedComponent.class);
            }

        }



    }
}
