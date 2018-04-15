package com.bryjamin.dancedungeon.factories.spells.animations;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.KillOnAnimationEndComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

import java.util.UUID;

public class BasicSlashAnimation implements SpellAnimation {

    @Override
    public void cast(World world, Entity caster, Skill skill, Coordinates casterCoordinates, Coordinates target) {

        Rectangle rectangle = world.getSystem(TileSystem.class).createRectangleUsingCoordinates(target);

        final int SLASH_DRAWABLE_ID = 25;
        final int SLASH_ANIMATION = 0;

        Entity slash = world.createEntity();
        slash.edit().add(new PositionComponent(rectangle.x, rectangle.y))
                .add(new DrawableComponent(Layer.FOREGROUND_LAYER_FAR, new TextureDescription.Builder(TextureStrings.SKILLS_SLASH)
                        .identifier(SLASH_DRAWABLE_ID)
                        .width(rectangle.getWidth())
                        //.color(new Color(Colors.AMOEBA_FAST_PURPLE))
                        .height(rectangle.getHeight())
                        //.scaleX(-1)
                        .build()))
                .add(new AnimationStateComponent(SLASH_ANIMATION))
                .add(new AnimationMapComponent().put(SLASH_ANIMATION, TextureStrings.SKILLS_SLASH, 0.3f, Animation.PlayMode.NORMAL))
                .add(new KillOnAnimationEndComponent(SLASH_ANIMATION));

        String id = UUID.randomUUID().toString();

        world.getSystem(ActionCameraSystem.class).createDeathWaitAction(slash, skill.getSkillId());
        skill.castSpellOnTargetLocation(skill.getSkillId(), world, caster, casterCoordinates, target);


    }
}
