package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TileEffectComponent;
import com.bryjamin.dancedungeon.utils.observer.Observable;
import com.bryjamin.dancedungeon.utils.observer.Observer;

public class TileEffectSystem extends EntityProcessingSystem implements Observer{

    private TileSystem tileSystem;
    private ActionQueueSystem actionQueueSystem;

    private ComponentMapper<CoordinateComponent> cm;
    private ComponentMapper<TileEffectComponent> tem;


    private ComponentMapper<HealthComponent> hm;

    private boolean processingFlag = false;

    /**
     *
     */
    public TileEffectSystem() {
        super(Aspect.all(TileEffectComponent.class, CoordinateComponent.class));
    }

    @Override
    protected void process(Entity e) {

        TileEffectComponent tec = tem.get(e);

        Array<Entity> entityArray = tileSystem.getCoordinateMap().get(cm.get(e).coordinates);

        for(int i = 0; i < entityArray.size; i++){

            Entity occupier = entityArray.get(i);

            for(TileEffectComponent.Effect effect : tec.effects){

                switch (effect){
                    case DEATH:
                        if(hm.has(occupier)){
                            hm.get(occupier).applyDamage(hm.get(occupier).maxHealth * 2);
                        };
                }
            }


        }
    }


    @Override
    protected void initialize() {
        actionQueueSystem.observable.addObserver(this);
    }

    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }

    @Override
    public void update(Object o) {
        processingFlag = true;
    }
}
