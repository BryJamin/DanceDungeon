package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.ecs.components.identifiers.StunnedComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;

/**
 * System used to track when a unit gets stunned.
 *
 * This System also removes any 'intent' attacks that the unit had.
 *
 * As due to being stunned the unit should not be able to attack
 */
public class StunnedSystem extends EntitySystem implements Observer{


    private DisplayEnemyIntentUISystem displayEnemyIntentUISystem;
    private TurnSystem turnSystem;

    ComponentMapper<UnitComponent> unitM;
    private ComponentMapper<StoredSkillComponent> storedSkillM;

    boolean processingFlag = false;

    public StunnedSystem() {
        super(Aspect.all(UnitComponent.class, StunnedComponent.class));
    }

    @Override
    protected void initialize() {
        turnSystem.addPlayerTurnObserver(this);
    }

    @Override
    public void inserted(Entity e) {
        processingFlag = true;

        if(storedSkillM.has(e)){
            e.edit().remove(StoredSkillComponent.class);
            displayEnemyIntentUISystem.updateIntent();
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
    public void update(Object o) {
        for(Entity e : this.getEntities()){

            UnitData unitData = unitM.get(e).getUnitData();

            unitData.stun--;

            if(unitData.stun <= 0){
                e.edit().remove(StunnedComponent.class);
            }

        }
    }

}
