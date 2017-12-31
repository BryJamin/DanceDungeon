package com.bryjamin.dancedungeon.factories.player;

import com.artemis.Aspect;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 14/10/2017.
 */

public class PlayerFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);

    private static final float health = 20;

    private UnitFactory unitFactory = new UnitFactory();

    private static final int BODY_DRAWABLE = 24;

    private DrawableDescription.DrawableDescriptionBuilder createPlayerTexture(String id){

        return new TextureDescription.Builder(id)
                .identifier(BODY_DRAWABLE)
                .size(height);

    }

    public ComponentBag player(Unit unit){

        ComponentBag bag = unitFactory.basePlayerUnitBag(unit.getStatComponent());

        bag.add(unit.getSkillsComponent());
        bag.add(new TargetComponent(Aspect.all(EnemyComponent.class, CoordinateComponent.class)));
        bag.add(new CenteringBoundaryComponent(new Rectangle(0, 0, width, height)));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, createPlayerTexture(TextureStrings.WARRIOR).build()));

        return bag;

    }

    public ComponentBag mage(Unit unit){

        //System.out.println(unit.getStatComponent().maxHealth);

        ComponentBag bag = unitFactory.basePlayerUnitBag(unit.getStatComponent());

        bag.add(unit.getSkillsComponent());
        bag.add(new TargetComponent(Aspect.all(EnemyComponent.class, CoordinateComponent.class)));
        bag.add(new CenteringBoundaryComponent(new Rectangle(0, 0, width, height)));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, createPlayerTexture(TextureStrings.PLAYER).build()));

        int STANDING_ANIMATION = 23;

        bag.add(new AnimationStateComponent().put(BODY_DRAWABLE, STANDING_ANIMATION));
        bag.add(new AnimationMapComponent()
                .put(STANDING_ANIMATION, TextureStrings.PLAYER, 0.4f, Animation.PlayMode.LOOP));


        return bag;

    }



}
