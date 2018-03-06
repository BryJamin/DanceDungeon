package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyIntentComponent;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 06/03/2018.
 *
 * Used to show enemy intent to players
 */

public class EnemyIntentSystem extends EntitySystem {


    TileSystem tileSystem;

    ComponentMapper<EnemyIntentComponent> eiMapper;
    ComponentMapper<StoredSkillComponent> storedMapper;
    ComponentMapper<CoordinateComponent> coordinateMapper;

    TargetingFactory targetingFactory = new TargetingFactory();

    private boolean processingFlag = false;

    public EnemyIntentSystem() {
        super(Aspect.all(StoredSkillComponent.class, CoordinateComponent.class));
    }

    @Override
    protected void processSystem() {

        IntBag enemyIntent = world.getAspectSubscriptionManager().get(Aspect.all(EnemyIntentComponent.class)).getEntities();

        for(int i = 0; i < enemyIntent.size(); i++){
            world.delete(enemyIntent.get(i));
        }



        for(Entity e : this.getEntities()){
            Coordinates coordinates = e.getComponent(CoordinateComponent.class).coordinates;
            StoredSkillComponent storedSkillComponent = storedMapper.get(e);

            //For use of skills that have a fixed target, So upon movement their target stays the same

            if(!storedSkillComponent.current.equals(coordinates)){
                int diffX = coordinates.getX() - storedSkillComponent.current.getX();
                int diffY = coordinates.getY() - storedSkillComponent.current.getY();
                storedSkillComponent.target.addX(diffX);
                storedSkillComponent.target.addY(diffY);
                storedSkillComponent.current.set(coordinates);
            }

            Entity highlight = BagToEntity.bagToEntity(world.createEntity(), targetingFactory.highlightBox(tileSystem.getRectangleUsingCoordinates(storedSkillComponent.target)));


            //Skills that have a malleable target which changes once they move, 'E.G a StraightShot type skill'

        }




        processingFlag = false;

    }

    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }

    public void updateIntent(){
        processingFlag = true;
    }


    public boolean releaseAttack(){

        for(Entity e : this.getEntities()){
            StoredSkillComponent storedSkillComponent = storedMapper.get(e);
            storedSkillComponent.skill.cast(world, e, storedSkillComponent.target);
            e.edit().remove(StoredSkillComponent.class);
            return true;
        }

        return false;

    }















}
