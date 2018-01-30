package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.battle.BuffComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 30/01/2018.
 */
public class BuffSystem extends EntityProcessingSystem {

    private ComponentMapper<BuffComponent> buffM;
    private ComponentMapper<StatComponent> statM;

    public BuffSystem() {
        super(Aspect.all(BuffComponent.class, StatComponent.class));
    }

    @Override
    protected void process(Entity e) {

        BuffComponent bc = buffM.get(e);
        StatComponent sc = statM.get(e);

        //Reset buffed stats
        sc.buffedDodge = 0;

        for(Skill.SpellEffect se : bc.spellEffectArray){

            switch (se){
                case Dodge:
                    sc.buffedDodge += se.number;
                    break;
            }

        }


    }
}
