package com.bryjamin.dancedungeon.factories.enemy;

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
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.factories.spells.FireballSkill;
import com.bryjamin.dancedungeon.factories.spells.MovementDescription;
import com.bryjamin.dancedungeon.factories.spells.SkillDescription;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 14/11/2017.
 */

public class RangedDummyFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);

    private UnitFactory unitFactory = new UnitFactory();


    public final DrawableDescription.DrawableDescriptionBuilder player = new TextureDescription.Builder(TextureStrings.BIGGABLOBBA)
            .index(2)
            .size(height);


    public ComponentBag rangedDummy(float x, float y) {

        SkillDescription movement = new MovementDescription();
        SkillDescription fireball = new FireballSkill();

        int range = 4;

        StatComponent statComponent = new StatComponent.StatBuilder()
                .maxHealth(10)
                .magic(5)
                .power(2)
                .attackRange(4)
                .movementRange(4)
                .build();

        ComponentBag bag = unitFactory.baseEnemyUnitBag(statComponent);
        bag.add(new SkillsComponent(movement, fireball));
        bag.add(new CenteringBoundaryComponent(new Rectangle(x, y, width, height)));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, width, height))));

        bag.add(new UtilityAiComponent(
                new UtilityAiCalculator(
                        new ActionScoreCalculator(new EndTurnAction()),
                        new ActionScoreCalculator(new RangedMoveToAction(movement, range), new IsInRangeCalculator(-100, 100, range), new CanUseSkillCalculator(movement, 0f, null)),
                        new ActionScoreCalculator(new RangedAttackAction(fireball), new IsInRangeCalculator(150, -10, range), new CanUseSkillCalculator(fireball, 0f, null)
                        )
                )));

        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.build()));

        return bag;


    }



}
