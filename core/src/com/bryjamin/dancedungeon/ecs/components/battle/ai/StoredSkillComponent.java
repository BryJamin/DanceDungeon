package com.bryjamin.dancedungeon.ecs.components.battle.ai;

import com.artemis.Component;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 06/03/2018.
 */

public class StoredSkillComponent extends Component {

    public Coordinates storedCoordinates;
    public Coordinates storedTargetCoordinates;

    public Skill skill;


    public StoredSkillComponent(){
        storedCoordinates = new Coordinates();
        storedTargetCoordinates = new Coordinates();
        skill = SkillLibrary.getSkill(SkillLibrary.SKILL_HEAVY_STRIKE);
    }


    public StoredSkillComponent(Coordinates storedCoordinates, Coordinates storedTargetCoordinates, Skill skill){
        this.storedCoordinates = storedCoordinates;
        this.storedTargetCoordinates = storedTargetCoordinates;
        this.skill = skill;
    }


}
