package com.bryjamin.dancedungeon.ecs.components.battle.player;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.player.spells.SkillDescription;

/**
 * Created by BB on 18/11/2017.
 */

public class SkillsComponent extends Component {

    public Array<SkillDescription> skillDescriptions = new Array<SkillDescription>();

    public SkillsComponent(){};

    public SkillsComponent(SkillDescription... skillDescriptions){
        this.skillDescriptions.addAll(skillDescriptions);
    }


    public void endTurn(){
        for(SkillDescription skillDescription : skillDescriptions){
            skillDescription.endTurnUpdate();
        }
    }



}
