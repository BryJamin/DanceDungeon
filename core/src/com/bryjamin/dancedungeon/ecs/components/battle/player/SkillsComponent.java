package com.bryjamin.dancedungeon.ecs.components.battle.player;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.basic.MeleeAttack;

/**
 * Created by BB on 18/11/2017.
 */

public class SkillsComponent extends Component {

    public Skill basicAttack = new MeleeAttack();

    public Array<Skill> skills = new Array<Skill>();

    public SkillsComponent(){};


    public SkillsComponent(Skill basicAttack, Skill... skills){
        this.basicAttack = basicAttack;
        this.skills.addAll(skills);
    }


    public void endTurn(){
        for(Skill skill : skills){
            skill.endTurnUpdate();
        }
    }


    public boolean canCast(World world, Entity entity){
        if(skills.size <= 0) return false;

        for(Skill s : skills){
            if(s.canCast(world, entity)){
                return true;
            }
        }

        return false;
    }



}
