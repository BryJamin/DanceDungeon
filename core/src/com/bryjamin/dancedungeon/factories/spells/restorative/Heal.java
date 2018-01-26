package com.bryjamin.dancedungeon.factories.spells.restorative;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.texture.HighlightedText;

/**
 * Created by BB on 04/01/2018.
 */

public class Heal extends Skill {


    public boolean ready = false;

    public Heal() {
        super(new Builder()
                .name("Heal")
                .attack(Attack.Ranged)
                .spellAnimation(SpellAnimation.Glitter)
                .spellType(SpellType.Heal)
                .icon("skills/Medicine")
                .targeting(Targeting.Ally));
    }


    private int getHealValue(Entity e){
        return e.getComponent(StatComponent.class).magic;
    }

    public void endTurnUpdate(){
        ready = true;
    };

    @Override
    public String getDescription(World world, Entity entity) {
        return "Test Heals Allies for " + getHealValue(entity) + " damage";
    }

    @Override
    public HighlightedText getHighlight(World world, Entity entity) {
        return new HighlightedText()
                .add("Heals", new Color(Color.GREEN))
                .add(" Allies for ")
                .add(Integer.toString(getHealValue(entity)), new Color(Color.GREEN))
                .add(" damage");
    }

}
