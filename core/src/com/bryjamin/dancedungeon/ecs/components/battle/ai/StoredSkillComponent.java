package com.bryjamin.dancedungeon.ecs.components.battle.ai;

import com.artemis.Component;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.basic.Foresight;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 06/03/2018.
 */

public class StoredSkillComponent extends Component {

    public Coordinates current;
    public Coordinates target;

    public Skill skill;


    public StoredSkillComponent(){
        current = new Coordinates();
        target = new Coordinates();
        skill = new Foresight();
    }


    public StoredSkillComponent(Coordinates current, Coordinates target, Skill skill){
        this.current = current;
        this.target = target;
        this.skill = skill;
    }


}
