package com.bryjamin.dancedungeon.ecs.components.battle.ai;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 17/11/2017.
 *
 * Used To Store Builders that represent the targets of an Entity.
 *
 * The {@link com.bryjamin.dancedungeon.ecs.systems.battle.GenerateTargetsSystem} sets up the builder for the Entity.
 */

public class TargetComponent extends Component {

    public Aspect.Builder enemyBuilder;
    public Aspect.Builder allyBuilder;

    public TargetComponent(){

    }


    /**
     * Uses the Enemy Builder to return Targets of this Entity
     */
    public Array<Entity> getTargets(World world){

        Array<Entity> entityArray = new Array<Entity>();
        IntBag intBag = world.getAspectSubscriptionManager().get(enemyBuilder).getEntities();


        for(int i = 0; i < intBag.size(); i++){
            entityArray.add(world.getEntity(intBag.get(i)));
        }

        return entityArray;

    }

    /**
     * Uses the Ally Builder to return Allies of this Entity
     */
    public Array<Entity> getAllies(World world){

        Array<Entity> entityArray = new Array<Entity>();
        IntBag intBag = world.getAspectSubscriptionManager().get(allyBuilder).getEntities();


        for(int i = 0; i < intBag.size(); i++){
            entityArray.add(world.getEntity(intBag.get(i)));
        }

        return entityArray;

    }





}
