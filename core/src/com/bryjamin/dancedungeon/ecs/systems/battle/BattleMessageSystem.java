package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UITargetingComponent;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 27/11/2017.
 */

public class BattleMessageSystem extends BaseSystem {

    private Viewport gameport;

    private float height = Measure.units(7.5f);

    public BattleMessageSystem(Viewport gameport) {
        this.gameport = gameport;
    }

    @Override
    protected void processSystem() {

    }


    public Entity createWarningMessage() {

        float width = gameport.getWorldWidth();

        Entity e = world.createEntity().edit()
                .add(new PositionComponent(CenterMath.centerOnPositionX(width, gameport.getWorldWidth() / 2),
                        CenterMath.centerOnPositionY(height, gameport.getWorldHeight() / 2) + Measure.units(25f)))
                .add(new CenteringBoundComponent(new Rectangle(0, 0, width, height)))
                .add(new UITargetingComponent())
                .add(new DrawableComponent(
                        Layer.FOREGROUND_LAYER_NEAR,
                        new TextureDescription.Builder(TextureStrings.BLOCK)
                                .width(width)
                                .color(new Color(1, 1, 1, 0.7f))
                                .height(height)
                                .build()
                )).getEntity();

        Entity e2 = world.createEntity().edit()
                .add(new PositionComponent(CenterMath.centerOnPositionX(width, gameport.getWorldWidth() / 2),
                        CenterMath.centerOnPositionY(height, gameport.getWorldHeight() / 2) + Measure.units(25f)))
                .add(new CenteringBoundComponent(new Rectangle(0, 0, width, height)))
                .add(new UITargetingComponent())
                .add(new DrawableComponent(
                        Layer.FOREGROUND_LAYER_NEAR,
                        new TextDescription.Builder(Fonts.MEDIUM)
                                .text(TextResource.TARGETING_NO_TARGETS_IN_RANGE)
                                .color(new Color(Color.BLACK))
                                .build()
                )).getEntity();


        return e;

    }


}
