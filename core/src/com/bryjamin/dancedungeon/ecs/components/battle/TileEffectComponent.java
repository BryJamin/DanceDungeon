package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;

/**
 * Componenet used by tiles to apply damage to any entities that reside on the co-ordinates
 */
public class TileEffectComponent extends Component {

    public enum Effect {
        NONE,
        DEATH,
    }

    public Effect[] effects = new Effect[]{};

    public TileEffectComponent(){

    }

    public TileEffectComponent(Effect... effects){
        this.effects = effects;
    }



}
