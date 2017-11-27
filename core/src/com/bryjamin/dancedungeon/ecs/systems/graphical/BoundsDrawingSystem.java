package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.utils.BoundsDrawer;
import com.bryjamin.dancedungeon.utils.HitBox;

/**
 * Created by BB on 15/10/2017.
 */

public class BoundsDrawingSystem extends EntitySystem {

    ComponentMapper<HitBoxComponent> hitboxm;
    ComponentMapper<CenteringBoundaryComponent> boundm;

    private boolean isDrawing = true;

    private Array<Rectangle> bounds = new Array<Rectangle>();
    private Array<Rectangle> hitboxes = new Array<Rectangle>();
    private Array<Rectangle> proxhitboxes = new Array<Rectangle>();

    private SpriteBatch batch;


    public BoundsDrawingSystem(SpriteBatch batch) {
        super(Aspect.one(HitBoxComponent.class, CenteringBoundaryComponent.class));
        this.batch = batch;
    }


    @Override
    protected void processSystem() {

        if (!isDrawing) return;

        for (Entity e : this.getEntities()) {
            if (hitboxm.has(e)) {
                for (HitBox hb : hitboxm.get(e).hitBoxes) hitboxes.add(hb.hitbox);
            }

            if(boundm.has(e)){
                bounds.add(boundm.get(e).bound);
            }

/*            if(wm.has(e)) {
                bounds.add(wm.get(e).bound);
            }

            if(ptam.has(e)) {
                for (com.bryjamin.wickedwizard.utils.collider.HitBox hb : ptam.get(e).proximityHitBoxes) {
                    proxhitboxes.add(hb.hitbox);
                }
            }*/
        }

        BoundsDrawer.drawBounds(batch, bounds);
        BoundsDrawer.drawBounds(batch, Color.CYAN, hitboxes);
        BoundsDrawer.drawBounds(batch, Color.PINK, proxhitboxes);


        bounds.clear();
        hitboxes.clear();
        proxhitboxes.clear();


    }

}