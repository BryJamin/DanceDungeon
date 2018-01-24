package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.factories.spells.animations.SkillAnimation;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Highlight;

/**
 * Created by BB on 18/11/2017.
 */

public abstract class Skill {

    protected SkillAnimation skillAnimation;

    public abstract Array<Entity> createTargeting(World world, Entity player);

    public abstract boolean canCast(World world, Entity entity);

    public abstract void cast(World world, Entity entity, Coordinates target);

    public SkillAnimation getSkillAnimation(){
        return skillAnimation;
    }

    public abstract void endTurnUpdate();

    public String getIcon(){
        return TextureStrings.BLOCK;
    };

    public String getName(){
        return "ERROR: NAME NOT SET";
    }

    public String getDescription(World world, Entity entity){
        return "ERROR: DESCRIPTION NOT SET";
    }

    public Highlight getHighlight(){
        return null;
    }



    //public







}





