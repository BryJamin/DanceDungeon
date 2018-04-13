package com.bryjamin.dancedungeon.factories.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.ai.ActionScoreCalculator;
import com.bryjamin.dancedungeon.ecs.ai.UtilityAiCalculator;
import com.bryjamin.dancedungeon.ecs.ai.actions.BasicAttackAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.EndTurnAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.FindBestMovementAreaToAttackFromAction;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanMoveCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanUseSkillCalculator;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 17/03/2018.
 */

public class SpitterFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);

    private static final int basicAttackRange = 3;
    private static final int movementRange = 4;

    private static final int health = 2;

    private UnitFactory unitFactory = new UnitFactory();


    public final DrawableDescription.DrawableDescriptionBuilder player = new TextureDescription.Builder(TextureStrings.SPITTER)
            .index(2)
            .size(height);


    public ComponentBag rangedDummy() {

        Skill fireball = SkillLibrary.getEnemySkill(SkillLibrary.ENEMY_SKILL_THROW_ROCK);

        StatComponent statComponent = new StatComponent.StatBuilder()
                .healthAndMax(health)
                .attack(3)
                .attackRange(basicAttackRange)
                .movementRange(4)
                .build();

        UnitData unitData = new UnitData("Eugh");
        unitData.setStatComponent(statComponent);

        ComponentBag bag = unitFactory.baseEnemyUnitBag(unitData);
        bag.add(new SkillsComponent(fireball));
        bag.add(new CenteringBoundaryComponent(width, height));
        bag.add(new HitBoxComponent(new HitBox(width, height)));

        bag.add(new UtilityAiComponent(
                new UtilityAiCalculator(
                        new ActionScoreCalculator(new EndTurnAction()),
                        new ActionScoreCalculator(new FindBestMovementAreaToAttackFromAction(),
                                new CanMoveCalculator(100f, null)),
                        new ActionScoreCalculator(new BasicAttackAction(), new CanUseSkillCalculator(fireball, 100f, null)
                        )
                )));

        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.build()));

        int STANDING_ANIMATION = 23;

        bag.add(new AnimationStateComponent(STANDING_ANIMATION));
        bag.add(new AnimationMapComponent()
                .put(STANDING_ANIMATION, TextureStrings.SPITTER, 0.75f, Animation.PlayMode.LOOP));

        return bag;


    }


}