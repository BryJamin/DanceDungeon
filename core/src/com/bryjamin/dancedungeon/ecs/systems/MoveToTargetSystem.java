package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.AngleMath;

/**
 * Created by BB on 11/10/2017.
 */

public class MoveToTargetSystem extends EntityProcessingSystem {


    ComponentMapper<MoveToComponent> moveToMapper;

    private final static float speed = Measure.units(10f);

    @SuppressWarnings("unchecked")
    public MoveToTargetSystem() {
        super(Aspect.all(MoveToComponent.class, VelocityComponent.class, PositionComponent.class));
    }

    @Override
    protected void process(Entity e) {

        PositionComponent positionComponent = e.getComponent(PositionComponent.class);
        VelocityComponent velocityComponent = e.getComponent(VelocityComponent.class);
        MoveToComponent moveToComponent = e.getComponent(MoveToComponent.class);

        if(moveToComponent.movementPositions.size == 0) return;


        Vector3 targetPosition = moveToComponent.movementPositions.peek();

        double angle = AngleMath.angleOfTravel(positionComponent.getX(), positionComponent.getY(),
                targetPosition.x, targetPosition.y);

        float vx = AngleMath.velocityX(speed, angle);
        float vy = AngleMath.velocityY(speed, angle);


        boolean isPositionX = positionComponent.position.x == targetPosition.x;
        boolean isPositionY = positionComponent.position.y == targetPosition.y;

        if(positionComponent.getX() < targetPosition.x){

            if(positionComponent.getX() + vx * world.delta > targetPosition.x){
                positionComponent.position.x = targetPosition.x;
                isPositionX = true;
            } else {
                velocityComponent.velocity.x = vx;
            }

        } else {

            if(positionComponent.getX() + vx * world.delta < targetPosition.x){
                positionComponent.position.x = targetPosition.x;
                isPositionX = true;
            } else {
                velocityComponent.velocity.x = vx;
            }

        }



        if(positionComponent.getY() < targetPosition.y){

            if(positionComponent.getY() + vy * world.delta > targetPosition.y){
                positionComponent.position.y = targetPosition.y;
                isPositionY = true;
            } else {
                velocityComponent.velocity.y = vy;
            }

        } else {

            if(positionComponent.getY() + vy * world.delta < targetPosition.y){
                positionComponent.position.y = targetPosition.y;
                isPositionY = true;
            } else {
                velocityComponent.velocity.y = vy;
            }

        }



        if(isPositionX && isPositionY) moveToComponent.movementPositions.pop();

        if(moveToComponent.movementPositions.size <= 0){
            velocityComponent.velocity.x = 0;
            velocityComponent.velocity.y = 0;
        }



    }

}
