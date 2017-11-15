package com.bryjamin.dancedungeon.factories.enemy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.ai.ActionCalculator;
import com.bryjamin.dancedungeon.ecs.ai.UtilityAiCalculator;
import com.bryjamin.dancedungeon.ecs.ai.actions.RangedAttackAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.RangedMoveToAction;
import com.bryjamin.dancedungeon.ecs.ai.calculations.IsInRangeCalculator;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MovementRangeComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.AttackAiComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.factories.AbstractFactory;
import com.bryjamin.dancedungeon.factories.player.spells.Fireball;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 14/11/2017.
 */

public class RangedDummyFactory extends AbstractFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);


    public static final DrawableDescription.DrawableDescriptionBuilder player = new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BIGGABLOBBA)
            .index(2)
            .size(height);

    public RangedDummyFactory(AssetManager assetManager) {
        super(assetManager);
    }


    public ComponentBag rangedDummy(float x, float y) {

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));
        bag.add(new HealthComponent(10));
        bag.add(new EnemyComponent());
        bag.add(new AbilityPointComponent());
        bag.add(new AttackAiComponent());
        bag.add(new TurnComponent());
        bag.add(new CoordinateComponent(new Coordinates(1, 0)));
        bag.add(new MoveToComponent(Measure.units(30f)));
        bag.add(new VelocityComponent(0, 0));
        bag.add(new BlinkOnHitComponent());
        bag.add(new BoundComponent(new Rectangle(x, y, width, height)));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, width, height))));

        int range = 3;

        bag.add(new UtilityAiComponent(
                new UtilityAiCalculator(
                        new ActionCalculator(new RangedMoveToAction(range), new IsInRangeCalculator(0, 100, range)),
                        new ActionCalculator(new RangedAttackAction(new Fireball()), new IsInRangeCalculator(150, -10, range)
                        )
                )));

        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.build()));
        bag.add(new MovementRangeComponent(2));

        return bag;


    }



}
