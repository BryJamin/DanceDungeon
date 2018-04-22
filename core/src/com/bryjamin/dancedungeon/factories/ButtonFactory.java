package com.bryjamin.dancedungeon.factories;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 08/01/2018.
 */

public class ButtonFactory {

    public static class ButtonBuilder {

        private float x;
        private float y;
        private float width;
        private float height;

        private int layer = Layer.FOREGROUND_LAYER_FAR;

        private String text = "TEXT NOT SET";

        private Color textColor = new Color(Color.BLACK);
        private Color backGroundColor = new Color(Color.WHITE);

        private WorldAction action = null;

        public ButtonBuilder pos(float x, float y) {
            this.x = x;
            this.y = y;;
            return this;
        }

        public ButtonBuilder posY(float val) {
            y = val;
            return this;
        }

        public ButtonBuilder width(float val) {
            width = val;
            return this;
        }

        public ButtonBuilder height(float val) {
            height = val;
            return this;
        }

        public ButtonBuilder layer(int val) {
            layer = val;
            return this;
        }

        public ButtonBuilder text(String val) {
            text = val;
            return this;
        }


        public ButtonBuilder buttonAction(WorldAction worldAction) {
            this.action = worldAction;
            return this;
        }

        public void build(World world) {

            Entity background = world.createEntity().edit()
                    .add(new PositionComponent(x, y))
                    .add(new HitBoxComponent(width, height))
                    .add(new CenteringBoundComponent(width, height))
                    .add(new DrawableComponent(layer,

                            new TextureDescription.Builder(TextureStrings.BLOCK)
                                    .color(backGroundColor)
                                    .width(width)
                                    .height(height)
                                    .build()
                    )).getEntity();

            if(action != null)
                background.edit().add(new ActionOnTapComponent(action));

            world.createEntity().edit()
                    .add(new PositionComponent(x, y))
                    .add(new HitBoxComponent(width, height))
                    .add(new CenteringBoundComponent(width, height))
                    .add(new DrawableComponent(layer,
                            new TextDescription.Builder(Fonts.MEDIUM)
                                    .text(text)
                                    .color(textColor)
                                    .build()
                    ));


        }

    }


}
