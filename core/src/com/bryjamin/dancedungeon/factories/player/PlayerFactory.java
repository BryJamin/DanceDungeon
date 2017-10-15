package com.bryjamin.dancedungeon.factories.player;

import com.badlogic.gdx.assets.AssetManager;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerComponent;
import com.bryjamin.dancedungeon.factories.AbstractFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
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



    public ComponentBag player(float x, float y){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x,y));
        bag.add(new HealthComponent(10));
        bag.add(new PlayerComponent());
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player));

        return bag;


    }


}
