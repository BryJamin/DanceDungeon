package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 29/10/2017.
 */

public class PlayerGraphicalTargetingSystem extends BaseSystem {


    private Bag<Entity> trackedEntities = new Bag<Entity>();

    private Coordinates targetCoordinates = null;


    @Override
    protected void processSystem() {

    }

    public Coordinates getTargetCoordinates() {
        return targetCoordinates;
    }

    public boolean createTarget(float x, float y){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Coordinates c = tileSystem.getCoordinatesUsingPosition(x, y);

        if(c == null) return false;

        if(c == targetCoordinates) {
            clearTrackedEntites(); return false;
        }

        clearTrackedEntites();
        targetCoordinates = c;
        Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(tileSystem.getRectangleUsingCoordinates(c)));
        trackedEntities.add(box);

        return true;

    }

    public void clearTrackedEntites() {
        if (trackedEntities.size() > 0) {
            for (Entity e : trackedEntities) {
                e.edit().add(new DeadComponent());
            }
        }
        trackedEntities.clear();
        targetCoordinates = null;
    }


    public ComponentBag highlightBox(Rectangle r) {
        ComponentBag bag = new ComponentBag();

        bag.add(new PositionComponent(r.x, r.y));
        bag.add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(new Color(Color.WHITE))
                        .width(r.getWidth())
                        .height(r.getHeight())
                        .build()));
        bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new HitBoxComponent(new HitBox(r)));
        bag.add(new CenteringBoundaryComponent());

        return bag;
    }



}
