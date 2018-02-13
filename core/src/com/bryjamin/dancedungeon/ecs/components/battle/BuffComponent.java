package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.spells.Skill;


/**
 * Created by BB on 30/01/2018.
 */

public class BuffComponent extends Component {

    public BuffComponent() {
    }

    public Array<Skill.SpellEffect> spellEffectArray = new Array<Skill.SpellEffect>();

    public void add(Skill.SpellEffect s) {
        spellEffectArray.add(s);
    }


    public void endTurn() {
        for (int i = spellEffectArray.size - 1; i >= 0; i--) {
            if (spellEffectArray.get(i).duration <= 0) spellEffectArray.removeIndex(i);
            else spellEffectArray.get(i).duration--;
        }
    }


}
