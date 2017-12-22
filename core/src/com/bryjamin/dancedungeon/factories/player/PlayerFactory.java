package com.bryjamin.dancedungeon.factories.player;

import com.artemis.Aspect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.factories.player.spells.FireballSkill;
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

public class PlayerFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);

    private static final float health = 20;

    private DrawableDescription.DrawableDescriptionBuilder createPlayerTexture(){

        return new TextureDescription.Builder(TextureStrings.PLAYER)
                .size(height);

    }

    public ComponentBag baseUnitBag(StatComponent statComponent){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent());

        bag.add(new CoordinateComponent());
        bag.add(new PlayerControlledComponent());
        bag.add(new MoveToComponent(Measure.units(60f))); //TODO speed should be based on the class
        bag.add(new VelocityComponent());


        //Graphical
        bag.add(new BlinkOnHitComponent());


        bag.add(new TurnComponent());

        return bag;

    }

    public ComponentBag player(float x, float y, Coordinates coordinates){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x,y));
        bag.add(new HealthComponent(health));
        bag.add(new PlayerControlledComponent());
        bag.add(new CoordinateComponent(coordinates));
        bag.add(new BlinkOnHitComponent());
        //bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new MoveToComponent(Measure.units(60f)));
        bag.add(new VelocityComponent());
        bag.add(new TurnComponent());


        bag.add(new StatComponent.StatBuilder()
                .movementRange(4)
                .build());


        bag.add(new SkillsComponent());
        bag.add(new TargetComponent(Aspect.all(EnemyComponent.class, CoordinateComponent.class)));

        bag.add(new CenteringBoundaryComponent(new Rectangle(x, y, width, height)));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, createPlayerTexture().build()));


        return bag;


    }

    public ComponentBag player2(float x, float y, Coordinates coordinates){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x,y));
        bag.add(new HealthComponent(health));
        bag.add(new PlayerControlledComponent());
        bag.add(new CoordinateComponent(coordinates));
        bag.add(new BlinkOnHitComponent());
        //bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new MoveToComponent(Measure.units(60f)));
        bag.add(new VelocityComponent());
        bag.add(new TurnComponent());

        bag.add(new StatComponent.StatBuilder()
                .movementRange(4)
                .build());


        bag.add(new SkillsComponent(new MovementDescription(), new SlashDescription(), new FireballSkill()));
        bag.add(new TargetComponent(Aspect.all(EnemyComponent.class, CoordinateComponent.class)));

        //  bag.add(new TurnComponent());
        bag.add(new CenteringBoundaryComponent(new Rectangle(x, y, width, height)));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, createPlayerTexture().color(new Color(Color.WHITE)).build()));

        return bag;


    }



}
