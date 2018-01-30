package com.bryjamin.dancedungeon.factories.enemy;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.ai.ActionScoreCalculator;
import com.bryjamin.dancedungeon.ecs.ai.UtilityAiCalculator;
import com.bryjamin.dancedungeon.ecs.ai.actions.BasicAttackAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.EndTurnAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.MovementAction;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanMoveCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanUseSkillCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.IsInRangeCalculator;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.basic.Fireball;
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

    private static final int basicAttackRange = 3;
    private static final int movementRange = 4;

    private UnitFactory unitFactory = new UnitFactory();


    public final DrawableDescription.DrawableDescriptionBuilder player = new TextureDescription.Builder(TextureStrings.BIGGABLOBBA)
            .index(2)
            .size(height);


    public ComponentBag rangedDummy() {

        Skill fireball = new Fireball();

        StatComponent statComponent = new StatComponent.StatBuilder()
                .healthAndMax(10)
                .attack(3)
                .attackRange(basicAttackRange)
                .movementRange(4)
                .build();

        ComponentBag bag = unitFactory.baseEnemyUnitBag(statComponent);
        bag.add(new SkillsComponent(fireball));
        bag.add(new CenteringBoundaryComponent(width, height));
        bag.add(new HitBoxComponent(new HitBox(width, height)));

        bag.add(new UtilityAiComponent(
                new UtilityAiCalculator(
                        new ActionScoreCalculator(new EndTurnAction()),
                        new ActionScoreCalculator(new MovementAction(),
                                new IsInRangeCalculator(-100, 100, basicAttackRange),
                                new CanMoveCalculator(100f, null)),
                        new ActionScoreCalculator(new BasicAttackAction(), new IsInRangeCalculator(150, -10, basicAttackRange),
                                new CanUseSkillCalculator(fireball, 0f, null)
                        )
                )));

        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.build()));

        return bag;


    }


}
