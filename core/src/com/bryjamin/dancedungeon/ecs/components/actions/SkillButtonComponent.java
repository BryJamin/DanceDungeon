package com.bryjamin.dancedungeon.ecs.components.actions;


import com.artemis.Component;
import com.artemis.Entity;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 23/01/2018.
 */

public class SkillButtonComponent extends Component {

    private Entity entity;
    private Skill skill;

    public SkillButtonComponent(){};

    public SkillButtonComponent(Entity entity, Skill skill){
        this.skill = skill;
        this.entity = entity;
    }

    public Skill getSkill() {
        return skill;
    }

    public Entity getEntity() {
        return entity;
    }
}
