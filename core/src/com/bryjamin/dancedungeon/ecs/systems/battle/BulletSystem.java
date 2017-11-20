package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.IntBag;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.BulletComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.FriendlyComponent;

/**
 * Created by BB on 19/10/2017.
 */

public class BulletSystem extends EntityProcessingSystem {

    private ComponentMapper<BulletComponent> bulletMapper;
    private ComponentMapper<HitBoxComponent> hitBoxMapper;
    private ComponentMapper<BoundComponent> boundMapper;
    private ComponentMapper<HealthComponent> healthMapper;

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<FriendlyComponent> friendlyMapper;

    @SuppressWarnings("unchecked")
    public BulletSystem() {
        super(Aspect.all(BulletComponent.class, BoundComponent.class));
    }


    @Override
    protected void process(Entity e) {

        if(friendlyMapper.has(e)){
            EntitySubscription subscription = world.getAspectSubscriptionManager()
                    .get(Aspect.all(HitBoxComponent.class, HealthComponent.class, EnemyComponent.class));
            IntBag entityIds = subscription.getEntities();
            bulletScan(e, entityIds);
        }

    }


    /**
     * Checks if the bullet is overlapping any of the hitboxes of the entities provided.
     * If there is overlap apply the bullet damage and kill the bullet entity
     * @param bullet - The bullet entity
     * @param entityIds - Bag of entities
     */
    private void bulletScan(Entity bullet, IntBag entityIds){


        for(int i = 0; i < entityIds.size(); i++){

            int entity = entityIds.get(i);

            if(hitBoxMapper.get(entity).overlaps(boundMapper.get(bullet).bound) && hitBoxMapper.get(entity).enabled){

                HealthComponent hc = healthMapper.get(entity);

                hc.applyDamage(bulletMapper.get(bullet).damage);

                bullet.edit().add(new DeadComponent());

                break;
            }

        }

    }


}
