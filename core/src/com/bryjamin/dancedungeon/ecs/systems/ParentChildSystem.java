package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ChildComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ParentComponent;

/**
 * Created by BB on 29/10/2017.
 */

public class ParentChildSystem extends EntitySystem {

    private OrderedMap<ParentComponent, Entity> parentMap = new OrderedMap<ParentComponent, Entity>();
    private OrderedMap<ChildComponent, Entity> childMap = new OrderedMap<ChildComponent, Entity>();


    private ComponentMapper<ChildComponent> childMapper;
    private ComponentMapper<ParentComponent> parentMapper;

    @Override
    public void inserted(Entity e) {

        if(childMapper.has(e)) childMap.put(e.getComponent(ChildComponent.class), e);
        if(parentMapper.has(e)) parentMap.put(e.getComponent(ParentComponent.class), e);
    }

    @Override
    public void removed(Entity e) {
        if(childMapper.has(e)) childMap.remove(e.getComponent(ChildComponent.class));
        if(parentMapper.has(e)) parentMap.remove(e.getComponent(ParentComponent.class));
    }

    @SuppressWarnings("unchecked")
    public ParentChildSystem() {
        super(Aspect.one(ChildComponent.class, ParentComponent.class));
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    @Override
    protected void processSystem() {

    }

    public Array<Entity> getChildren(ParentComponent parentComponent){
        Array<Entity> entityArray = new Array<Entity>();
        for(ChildComponent c : parentComponent.children){
            if(childMap.containsKey(c)) entityArray.add(childMap.get(c));
        }
        return entityArray;
    }


    public Entity getParent(ChildComponent childComponent){
        if(parentMap.containsKey(childComponent.parent)) return parentMap.get(childComponent.parent);
        return null;
    }

}
