package com.bryjamin.dancedungeon.factories.enemy;

import com.artemis.Aspect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.ai.ActionScoreCalculator;
import com.bryjamin.dancedungeon.ecs.ai.UtilityAiCalculator;
import com.bryjamin.dancedungeon.ecs.ai.actions.EndTurnAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.MeleeAttackAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.MeleeMoveToAction;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanUseSkillCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.IsNextToCalculator;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.DispellableComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MovementRangeComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.factories.player.spells.MovementDescription;
import com.bryjamin.dancedungeon.factories.player.spells.SkillDescription;
import com.bryjamin.dancedungeon.factories.player.spells.SlashDescription;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 15/10/2017.
 */

public class DummyFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);


    public final DrawableDescription.DrawableDescriptionBuilder blob = new TextureDescription.Builder(TextureStrings.BLOB)
            .index(2)
            .size(height)
            .color(Colors.BLOB_RED);

    public DummyFactory() {
        super();
    }


    private ComponentBag targetDummy(float x, float y) {

        SkillDescription movement = new MovementDescription();
        SkillDescription slash = new SlashDescription();

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));
        bag.add(new HealthComponent(10));
        bag.add(new EnemyComponent());
        bag.add(new TurnComponent());
        bag.add(new CoordinateComponent(new Coordinates(1, 0)));
        bag.add(new MoveToComponent(Measure.units(80f)));
        bag.add(new VelocityComponent(0, 0));
        bag.add(new BlinkOnHitComponent());
        bag.add(new CenteringBoundaryComponent(new Rectangle(x, y, width, height)));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, width, height))));
        bag.add(new SkillsComponent(movement, slash));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, blob.color(Color.WHITE).build()));
        bag.add(new MovementRangeComponent(6));
        bag.add(new TargetComponent(Aspect.all(PlayerControlledComponent.class, CoordinateComponent.class)));
        bag.add(new UtilityAiComponent(dummyAi(movement, slash)));

        return bag;

    }


    public ComponentBag targetDummyWalker(float x, float y) {

        ComponentBag bag = targetDummy(x, y);
        bag.add(new DispellableComponent(DispellableComponent.Type.HORIZONTAL));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, blob.color(Color.BLACK).build()));
        bag.add(new MovementRangeComponent(3));

        bag.add(new StatComponent.StatBuilder().movementRange(3)
                .build());

        return bag;

    }


    //TODO fix AI

    public UtilityAiCalculator dummyAi(SkillDescription movement, SkillDescription slash){
        return new UtilityAiCalculator(
                new ActionScoreCalculator(new EndTurnAction()),
                new ActionScoreCalculator(new MeleeMoveToAction(movement), new IsNextToCalculator(null, 100f), new CanUseSkillCalculator(movement, 100f, null)),
                new ActionScoreCalculator(new MeleeAttackAction(slash), new IsNextToCalculator(150f, null), new CanUseSkillCalculator(slash, 100f, null)
                ));
    }


    public ComponentBag targetDummySprinter(float x, float y) {

        ComponentBag bag = targetDummy(x, y);
        bag.add(new DispellableComponent(DispellableComponent.Type.HORIZONTAL));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, blob.color(Color.WHITE).build()));
        bag.add(new MovementRangeComponent(6));
        bag.add(new StatComponent.StatBuilder().movementRange(6)
                .build());
        return bag;

    }

}

