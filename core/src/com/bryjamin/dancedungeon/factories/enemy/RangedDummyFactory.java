package com.bryjamin.dancedungeon.factories.enemy;

import com.artemis.Aspect;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.ai.ActionScoreCalculator;
import com.bryjamin.dancedungeon.ecs.ai.UtilityAiCalculator;
import com.bryjamin.dancedungeon.ecs.ai.actions.EndTurnAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.RangedAttackAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.RangedMoveToAction;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanUseSkillCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.IsInRangeCalculator;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MovementRangeComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.factories.player.spells.FireballSkill;
import com.bryjamin.dancedungeon.factories.player.spells.MovementDescription;
import com.bryjamin.dancedungeon.factories.player.spells.SkillDescription;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 14/11/2017.
 */

public class RangedDummyFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);


    public final DrawableDescription.DrawableDescriptionBuilder player = new TextureDescription.Builder(TextureStrings.BIGGABLOBBA)
            .index(2)
            .size(height);


    public ComponentBag rangedDummy(float x, float y) {

        SkillDescription movement = new MovementDescription();
        SkillDescription fireball = new FireballSkill();



        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));
        bag.add(new SkillsComponent(movement, fireball));
        bag.add(new HealthComponent(10));
        bag.add(new EnemyComponent());
        bag.add(new TurnComponent());
        bag.add(new CoordinateComponent(new Coordinates(1, 0)));
        bag.add(new MoveToComponent(Measure.units(60f)));
        bag.add(new VelocityComponent(0, 0));
        bag.add(new BlinkOnHitComponent());
        bag.add(new CenteringBoundaryComponent(new Rectangle(x, y, width, height)));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, width, height))));

        bag.add(new TargetComponent(Aspect.all(PlayerControlledComponent.class, CoordinateComponent.class)));

        int range = 3;

        bag.add(new UtilityAiComponent(
                new UtilityAiCalculator(
                        new ActionScoreCalculator(new EndTurnAction()),
                        new ActionScoreCalculator(new RangedMoveToAction(movement, range), new IsInRangeCalculator(-100, 100, range), new CanUseSkillCalculator(movement, 0, -1000)),
                        new ActionScoreCalculator(new RangedAttackAction(fireball), new IsInRangeCalculator(150, -10, range), new CanUseSkillCalculator(fireball, 0, -1000)
                        )
                )));

        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.build()));
        bag.add(new MovementRangeComponent(2));

        return bag;


    }



}
