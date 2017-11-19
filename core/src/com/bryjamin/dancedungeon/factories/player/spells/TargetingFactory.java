package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 18/11/2017.
 */

public class TargetingFactory {


    public void createTargetTile(World world, final Entity entity, final Spell spell, int range) {

        TargetComponent targetComponent = entity.getComponent(TargetComponent.class);

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for(Entity e : targetComponent.getTargets(world)){

            Coordinates c1 = entity.getComponent(CoordinateComponent.class).coordinates;
            Coordinates c2 = e.getComponent(CoordinateComponent.class).coordinates;

            if (tileSystem.getOccupiedMap().containsValue(e, true)
                    && CoordinateMath.isWithinRange(c1, c2, range)) {

                final Coordinates c = tileSystem.getOccupiedMap().findKey(e, true);

                Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(tileSystem.getRectangleUsingCoordinates(c)));

                box.edit().add(new ActionOnTapComponent(new WorldAction() {
                    @Override
                    public void performAction(World world, final Entity e) {
                        spell.cast(entity, world, c);
                    }
                }));
            }
        }


    }



    public ComponentBag highlightBox(Rectangle r) {
        ComponentBag bag = new ComponentBag();

        bag.add(new PositionComponent(r.x, r.y));
        bag.add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOCK)
                        .color(new Color(Color.WHITE))
                        .width(r.getWidth())
                        .height(r.getHeight())
                        .build()));
        bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new HitBoxComponent(new HitBox(r)));
        bag.add(new BoundComponent());

        return bag;
    }





}
