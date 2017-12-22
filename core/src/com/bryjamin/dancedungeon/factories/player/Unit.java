package com.bryjamin.dancedungeon.factories.player;

import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;

/**
 * Created by BB on 22/12/2017.
 */

public class Unit {

    public String id = UnitFactory.UNIT_WARRIOR;
    public StatComponent statComponent = new StatComponent();
    public SkillsComponent skillsComponent = new SkillsComponent();

    public Unit(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatComponent getStatComponent() {
        return statComponent;
    }

    public void setStatComponent(StatComponent statComponent) {
        this.statComponent = statComponent;
    }

    public SkillsComponent getSkillsComponent() {
        return skillsComponent;
    }

    public void setSkillsComponent(SkillsComponent skillsComponent) {
        this.skillsComponent = skillsComponent;
    }

}
