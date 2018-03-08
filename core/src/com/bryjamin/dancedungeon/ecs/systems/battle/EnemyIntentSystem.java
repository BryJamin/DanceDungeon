package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyIntentComponent;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.enums.Direction;
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

            //For use of skills that have a fixed target, So upon movement their storedTargetCoordinates stays the same

            System.out.println(storedSkillComponent.skill.getTargeting());

            switch (storedSkillComponent.skill.getTargeting()){

                case StraightShot:

                    //if(!storedSkillComponent.storedCoordinates.equals(coordinates)){

                        Coordinates stored = storedSkillComponent.storedCoordinates;
                        Coordinates storedTarget = storedSkillComponent.storedTargetCoordinates;

                        Array<Coordinates> coordinatesArray = new Array<Coordinates>();

                    System.out.println(stored);
                    System.out.println(storedTarget);
                    System.out.println(coordinates);

                        if(stored.getX() < storedTarget.getX() && stored.getY() == storedTarget.getY()){
                            coordinatesArray = tileSystem.getFreeCoordinateInAGivenDirection(coordinates, new Direction[]{Direction.RIGHT});
                        } else if(stored.getX() > storedTarget.getX() && stored.getY() == storedTarget.getY()){
                            coordinatesArray = tileSystem.getFreeCoordinateInAGivenDirection(coordinates, new Direction[]{Direction.LEFT});
                        } else if(stored.getX() == storedTarget.getX() && stored.getY() > storedTarget.getY()){
                            coordinatesArray = tileSystem.getFreeCoordinateInAGivenDirection(coordinates, new Direction[]{Direction.DOWN});
                        } else if(stored.getX() == storedTarget.getX() && stored.getY() < storedTarget.getY()){
                            coordinatesArray = tileSystem.getFreeCoordinateInAGivenDirection(coordinates, new Direction[]{Direction.UP});
                        }

                        System.out.println("array size " + coordinatesArray.size);
                    System.out.println(coordinatesArray.first());

                        for(Coordinates c : coordinatesArray) { //There is only one I need to refactor to have a none coordinates array return value
                            System.out.println("INSIDE");

                            Entity highlight = BagToEntity.bagToEntity(world.createEntity(), targetingFactory.highlightBox(tileSystem.getRectangleUsingCoordinates(c)));
                            storedSkillComponent.storedTargetCoordinates = c;
                            storedSkillComponent.storedCoordinates = coordinates;
                            new TargetingFactory().createRedTargetingMarkers(world, e.getComponent(CoordinateComponent.class).coordinates, c);
                        }

                    //}


                    break;



                default:
                    if(!storedSkillComponent.storedCoordinates.equals(coordinates)){
                        int diffX = coordinates.getX() - storedSkillComponent.storedCoordinates.getX();
                        int diffY = coordinates.getY() - storedSkillComponent.storedCoordinates.getY();
                        storedSkillComponent.storedTargetCoordinates.addX(diffX);
                        storedSkillComponent.storedTargetCoordinates.addY(diffY);
                        storedSkillComponent.storedCoordinates.set(coordinates);
                    }

                    Entity highlight = BagToEntity.bagToEntity(world.createEntity(), targetingFactory.highlightBox(tileSystem.getRectangleUsingCoordinates(storedSkillComponent.storedTargetCoordinates)));

            }


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
            storedSkillComponent.skill.cast(world, e, storedSkillComponent.storedTargetCoordinates);
            e.edit().remove(StoredSkillComponent.class);
            return true;
        }

        return false;

    }















}
