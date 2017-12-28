package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.factories.spells.animations.BasicProjectile;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 18/11/2017.
 */

public class FireballSkill extends CooldownSpellDescription {

    @Override
    public Array<Entity> createTargeting(World world, final Entity player) {
        Array<Entity> entityArray = new com.bryjamin.dancedungeon.factories.spells.TargetingFactory().createTargetTiles(world, player, this, 3);
        return entityArray;
    }

    @Override
    public String getIcon() {
        return "skills/Fire";
    }


    @Override
    public void cast(World world, Entity entity, Coordinates target) {

        ready = false;

        float width = Measure.units(5f);
        float height = Measure.units(5f);

        new BasicProjectile.BasicProjectileBuilder()
                .drawableComponent(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                        new TextureDescription.Builder(TextureStrings.BLOCK)
                                .color(new Color(Colors.BOMB_ORANGE))
                                .width(width)
                                .height(height)
                                .build()))
                .width(width)
                .speed(Measure.units(85f))
                .damage(entity.getComponent(StatComponent.class).magic)
                .build()
                .cast(world, entity, target);

    }
}
