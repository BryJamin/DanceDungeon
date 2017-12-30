package com.bryjamin.dancedungeon.ecs.components.battle.player;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.spells.basic.MeleeAttack;
import com.bryjamin.dancedungeon.factories.spells.SkillDescription;

/**
 * Created by BB on 18/11/2017.
 */

public class SkillsComponent extends Component {

    public SkillDescription basicAttack = new MeleeAttack();

    public Array<SkillDescription> skillDescriptions = new Array<SkillDescription>();

    public SkillsComponent(){};


    public SkillsComponent(SkillDescription basicAttack, SkillDescription... skillDescriptions){
        this.basicAttack = basicAttack;
        this.skillDescriptions.addAll(skillDescriptions);
    }


    public void endTurn(){
        for(SkillDescription skillDescription : skillDescriptions){
            skillDescription.endTurnUpdate();
        }
    }


    public boolean canCast(World world, Entity entity){
        if(skillDescriptions.size <= 0) return false;

        for(SkillDescription s : skillDescriptions){
            if(s.canCast(world, entity)){
                return true;
            }
        }

        return false;
    }



}
