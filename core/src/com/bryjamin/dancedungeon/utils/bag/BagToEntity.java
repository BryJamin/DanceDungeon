package com.bryjamin.dancedungeon.utils.bag;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;

/**
 * Created by BB on 15/10/2017.
 */

public class BagToEntity {

    public static Bag<Entity> bagsToEntities(World world, Bag<? extends Bag<Component>> bags){

        Bag<Entity> entityBag = new Bag<Entity>();

        for(Bag<Component> bag : bags){
            entityBag.add(bagToEntity(world.createEntity(), bag));
        }

        return entityBag;
    }

    public static Entity bagToEntity(Entity e, Bag<Component> bag){
        for(Component c : bag){
            e.edit().add(c);
        }
        return e;
    }

}

