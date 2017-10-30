package com.bryjamin.dancedungeon.factories.player;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerComponent;
import com.bryjamin.dancedungeon.factories.AbstractFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 14/10/2017.
 */

public class PlayerFactory extends AbstractFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);


    public static final DrawableDescription player = new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.PLAYER)
            .size(height)
            .build();

    public PlayerFactory(AssetManager assetManager) {
        super(assetManager);
    }



    public ComponentBag player(float x, float y, Coordinates coordinates){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x,y));
        bag.add(new HealthComponent(10));
        bag.add(new PlayerComponent());
        bag.add(new CoordinateComponent(coordinates));
        bag.add(new BlinkOnHitComponent());
        //bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new MoveToComponent());
        bag.add(new VelocityComponent());
        bag.add(new TurnComponent());

      //  bag.add(new TurnComponent());
        bag.add(new BoundComponent(new Rectangle(x, y, width, height)));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player));

        return bag;


    }


}
