package com.bryjamin.dancedungeon.ecs.components.battle.ai;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 17/11/2017.
 */

public class TargetComponent extends Component {

    Aspect.Builder builder;

    public TargetComponent(){
        builder = Aspect.all();
    }


    public TargetComponent(Aspect.Builder builder){
        this.builder = builder;
    }


    public Array<Entity> getTargets(World world){

        Array<Entity> entityArray = new Array<Entity>();
        IntBag intBag = world.getAspectSubscriptionManager().get(builder).getEntities();


        for(int i = 0; i < intBag.size(); i++){
            entityArray.add(world.getEntity(intBag.get(i)));
        }

        return entityArray;

    }





}
