package com.bryjamin.dancedungeon.factories.player;

import com.artemis.Aspect;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.factories.AbstractFactory;
import com.bryjamin.dancedungeon.factories.player.spells.FireballSkill;
import com.bryjamin.dancedungeon.factories.player.spells.FrostBallDescription;
import com.bryjamin.dancedungeon.factories.player.spells.MovementDescription;
import com.bryjamin.dancedungeon.factories.player.spells.SlashDescription;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 14/10/2017.
 */

public class PlayerFactory extends AbstractFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);


    public static final DrawableDescription.DrawableDescriptionBuilder player = new TextureDescription.Builder(TextureStrings.PLAYER)
            .size(height);
    public PlayerFactory(AssetManager assetManager) {
        super(assetManager);
    }



    public ComponentBag player(float x, float y, Coordinates coordinates){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x,y));
        bag.add(new HealthComponent(40));
        bag.add(new PlayerControlledComponent());
        bag.add(new CoordinateComponent(coordinates));
        bag.add(new BlinkOnHitComponent());
        bag.add(new AbilityPointComponent(4));
        //bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new MoveToComponent(Measure.units(60f)));
        bag.add(new VelocityComponent());
        bag.add(new TurnComponent());


        bag.add(new SkillsComponent(new MovementDescription(), new FireballSkill(), new FrostBallDescription()));
        bag.add(new TargetComponent(Aspect.all(EnemyComponent.class, CoordinateComponent.class)));

      //  bag.add(new TurnComponent());
        bag.add(new CenteringBoundaryComponent(new Rectangle(x, y, width, height)));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.build()));

        return bag;


    }

    public ComponentBag player2(float x, float y, Coordinates coordinates){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x,y));
        bag.add(new HealthComponent(40));
        bag.add(new PlayerControlledComponent());
        bag.add(new CoordinateComponent(coordinates));
        bag.add(new BlinkOnHitComponent());
        bag.add(new AbilityPointComponent(4));
        //bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new MoveToComponent(Measure.units(60f)));
        bag.add(new VelocityComponent());
        bag.add(new TurnComponent());


        bag.add(new SkillsComponent(new MovementDescription(), new SlashDescription(), new FireballSkill()));
        bag.add(new TargetComponent(Aspect.all(EnemyComponent.class, CoordinateComponent.class)));

        //  bag.add(new TurnComponent());
        bag.add(new CenteringBoundaryComponent(new Rectangle(x, y, width, height)));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.color(new Color(Color.WHITE)).build()));

        return bag;


    }



}
