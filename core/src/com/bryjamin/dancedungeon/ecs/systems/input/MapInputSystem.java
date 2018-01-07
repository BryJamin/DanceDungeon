package com.bryjamin.dancedungeon.ecs.systems.input;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.map.MapNodeComponent;

/**
 * Created by BB on 07/01/2018.
 */

public class MapInputSystem extends EntitySystem {

    private Viewport gamePort;
    private MainGame game;

    public MapInputSystem(MainGame game, Viewport gamePort) {
        super(Aspect.all(MapNodeComponent.class, HitBoxComponent.class));
        this.gamePort = gamePort;
        this.game = game;
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    @Override
    protected void processSystem() {

    }


    /**
     * Runs a check to see if the inserted co-ordinates are contained in any of this system's entities'
     * collision boundaries. If they are an action is performed
     *
     * @param x - x position of the area of the screen that was touched
     * @param y - y position of the area of the screen that was touched
     * @return - True if an entity has been touched, False otherwise
     */
    public boolean touch(float x, float y) {

        for (Entity e : this.getEntities()) {

            if (e.getComponent(HitBoxComponent.class).contains(x, y)) {

                e.getComponent(MapNodeComponent.class);
                return true;
            }
        }
        return false;
    }


}

