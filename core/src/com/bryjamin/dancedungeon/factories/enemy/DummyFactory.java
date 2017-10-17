package com.bryjamin.dancedungeon.factories.enemy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.battle.DispellableComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.factories.AbstractFactory;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 15/10/2017.
 */

public class DummyFactory extends AbstractFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);


    public static final DrawableDescription player = new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOB)
            .index(2)
            .size(height)
            .color(Colors.BLOB_RED)
            .build();

    public DummyFactory(AssetManager assetManager) {
        super(assetManager);
    }



    public ComponentBag targetDummy(float x, float y){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x,y));
        bag.add(new HealthComponent(10));
        bag.add(new DispellableComponent());
        bag.add(new EnemyComponent());
        bag.add(new BlinkOnHitComponent());
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x,y, width, height))));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player));

        return bag;


    }


}

