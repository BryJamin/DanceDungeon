package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.IntBag;
import com.bryjamin.dancedungeon.ecs.components.ExplosionComponent;
import com.bryjamin.dancedungeon.ecs.components.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerComponent;
import com.bryjamin.dancedungeon.utils.HitBox;

/**
 * Created by BB on 15/10/2017.
 */

public class ExplosionSystem extends EntityProcessingSystem {


    ComponentMapper<HitBoxComponent> hitboxm;
    ComponentMapper<HealthComponent> hm;
    ComponentMapper<ExplosionComponent> em;

    //ComponentMapper<FriendlyComponent> fm;

    ComponentMapper<PlayerComponent> playerm;

    ComponentMapper<EnemyComponent> enemym;


    private IntBag entities;


    @SuppressWarnings("unchecked")
    public ExplosionSystem() {
        super(Aspect.all(ExplosionComponent.class, PositionComponent.class, HitBoxComponent.class));
    }

    @Override
    protected void begin() {

        EntitySubscription subscription = world.getAspectSubscriptionManager().
                get(Aspect.all(HealthComponent.class, HitBoxComponent.class).one(PlayerComponent.class, EnemyComponent.class));

        entities = subscription.getEntities();

    }

    @Override
    protected void process(Entity explosion) {

        for(int i = 0; i < entities.size(); i++){

            int entity = entities.get(i);

/*            if(playerm.has(entity) && fm.has(explosionEntity)){
                continue;
            }*/

            if(enemym.has(entity) && enemym.has(explosion)){
                continue;
            }

            HitBoxComponent hitBoxComponent = hitboxm.get(entity);


            HitBoxComponent explosionHitBox = hitboxm.get(explosion);

            for(HitBox hb : explosionHitBox.hitBoxes){
                if(hitBoxComponent.overlaps(hb.hitbox)) {
                    HealthComponent hc = hm.get(entity);
                    ExplosionComponent explosionComponent = em.get(explosion);
                    hc.applyDamage(explosionComponent.damage);
                    break;
                }
            }

        }

        explosion.edit().remove(ExplosionComponent.class);


    }
}
