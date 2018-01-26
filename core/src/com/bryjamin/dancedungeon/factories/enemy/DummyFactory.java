package com.bryjamin.dancedungeon.factories.enemy;

import com.badlogic.gdx.graphics.Color;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.ai.ActionScoreCalculator;
import com.bryjamin.dancedungeon.ecs.ai.UtilityAiCalculator;
import com.bryjamin.dancedungeon.ecs.ai.actions.BasicAttackAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.EndTurnAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.MeleeMoveToAction;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanMoveCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanUseSkillCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.IsNextToCalculator;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SlashDescription;
import com.bryjamin.dancedungeon.factories.spells.basic.MeleeAttack;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 15/10/2017.
 */

public class DummyFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);

    private UnitFactory unitFactory = new UnitFactory();


    public final DrawableDescription.DrawableDescriptionBuilder blob = new TextureDescription.Builder(TextureStrings.BLOB)
            .index(2)
            .size(height)
            .color(Colors.BLOB_RED);

    public DummyFactory() {
        super();
    }


    private ComponentBag targetDummy(StatComponent statComponent) {

        Skill slash = new SlashDescription();

        ComponentBag bag = unitFactory.baseEnemyUnitBag(statComponent); //new ComponentBag();

        bag.add(new MoveToComponent(Measure.units(80f)));
        bag.add(new CenteringBoundaryComponent(width, height));
        bag.add(new HitBoxComponent(new HitBox(width, height)));
        bag.add(new SkillsComponent(new MeleeAttack()));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, blob.color(Color.WHITE).build()));
        bag.add(new UtilityAiComponent(dummyAi(slash)));

        return bag;

    }


    public ComponentBag targetDummyWalker() {
        ComponentBag bag = targetDummy(new StatComponent.StatBuilder().movementRange(3)
                .build());
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, blob.color(Color.CYAN).build()));
        bag.add(new StatComponent.StatBuilder().movementRange(3)
                .build());

        return bag;

    }


    //TODO fix AI

    public UtilityAiCalculator dummyAi(Skill slash) {
        return new UtilityAiCalculator(
                new ActionScoreCalculator(new EndTurnAction()),
                new ActionScoreCalculator(new MeleeMoveToAction(), new IsNextToCalculator(null, 100f),
                        new CanMoveCalculator(100f, null)),
                new ActionScoreCalculator(new BasicAttackAction(), new IsNextToCalculator(150f, null), new CanUseSkillCalculator(slash, 100f, null)
                ));
    }


    public ComponentBag targetDummySprinter() {

        ComponentBag bag = targetDummy(new StatComponent.StatBuilder().movementRange(6)
                .build());
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, blob.color(Color.WHITE).build()));
        bag.add(new StatComponent.StatBuilder().movementRange(6)
                .build());
        return bag;

    }

}

