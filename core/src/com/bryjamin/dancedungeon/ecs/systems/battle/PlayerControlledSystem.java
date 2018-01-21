package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;

/**
 * Created by BB on 08/01/2018.
 */

public class PlayerControlledSystem extends EntitySystem{

    private Array<Entity> playerBag = new Array<Entity>();

    private MainGame game;

    public PlayerControlledSystem(MainGame game) {
        super(Aspect.all(PlayerControlledComponent.class, CoordinateComponent.class, MoveToComponent.class));
    }

    public Array<Entity> getPlayerBag() {
        return playerBag;
    }

    @Override
    public void inserted(Entity e) {
        playerBag.add(e);
    }

    @Override
    public void removed(Entity e) {
        playerBag.removeValue(e, true);
    }

    @Override
    protected void processSystem() {

    }
}
