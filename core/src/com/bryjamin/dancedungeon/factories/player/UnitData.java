package com.bryjamin.dancedungeon.factories.player;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;

/**
 * Created by BB on 22/12/2017.
 *
 * Used to store the type of unit that can be created
 * As well as the stats and skills of the unit.
 *
 */

public class UnitData {

    public String id = UnitMap.UNIT_WARRIOR;

    public String icon = TextureStrings.CLASS_CYRONAUT;

    public String name = "Jeff";

    public StatComponent statComponent = new StatComponent();
    public SkillsComponent skillsComponent = new SkillsComponent();

    public UnitData(String id){
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
