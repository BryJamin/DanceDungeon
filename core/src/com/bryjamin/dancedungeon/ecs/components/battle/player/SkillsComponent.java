package com.bryjamin.dancedungeon.ecs.components.battle.player;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.basic.MeleeAttack;

/**
 * Created by BB on 18/11/2017.
 */

public class SkillsComponent extends Component {

    public Skill basicAttack = new MeleeAttack();

    private int maxSkills;

    public Array<Skill> skills = new Array<Skill>();

    public SkillsComponent(){};


    public SkillsComponent(Skill... skills){
        this.skills.addAll(skills);
    }


    public void endTurn(){
        basicAttack.endTurnUpdate();
        for(Skill skill : skills){
            skill.endTurnUpdate();
        }
    }


}
