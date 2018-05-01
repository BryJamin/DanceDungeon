package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.utils.math.AngleMath;

/**
 * Created by BB on 11/10/2017.
 *
 * System for executing queued movement positions on an Entity.
 */

public class MoveToTargetSystem extends EntityProcessingSystem {


    private ComponentMapper<MoveToComponent> moveToMapper;
    private ComponentMapper<PositionComponent> pm;
    private ComponentMapper<VelocityComponent> vm;

    @SuppressWarnings("unchecked")
    public MoveToTargetSystem() {
        super(Aspect.all(MoveToComponent.class, VelocityComponent.class, PositionComponent.class));
    }

    @Override
    protected void process(Entity e) {

        PositionComponent positionComponent = pm.get(e);
        VelocityComponent velocityComponent = vm.get(e);
        MoveToComponent moveToComponent = moveToMapper.get(e);

        if (moveToComponent.movementPositions.size == 0) return;

        Vector3 targetPosition = moveToComponent.movementPositions.first();

        double angle = AngleMath.angleOfTravel(positionComponent.getX(), positionComponent.getY(),
                targetPosition.x, targetPosition.y);

        //Calculate Speed Entity needs to travel in the x and y direction in order to reach the destination
        float vx = AngleMath.velocityX(moveToComponent.speed, angle);
        float vy = AngleMath.velocityY(moveToComponent.speed, angle);
        velocityComponent.velocity.x = vx;
        velocityComponent.velocity.y = vy;

        //Checks If Entity is already at target destination
        boolean isOnTargetX = positionComponent.position.x == targetPosition.x;
        boolean isOnTargetY = positionComponent.position.y == targetPosition.y;

        //New Position of Entity When Velocity Applied
        float newX = positionComponent.getX() + (vx * world.delta);
        float newY = positionComponent.getY() + (vy * world.delta);

        //Based on Direction of Velocity Checks If Entity is On Target
        if(!isOnTargetX)
            isOnTargetX = (vx > 0) ? newX > targetPosition.x : newX < targetPosition.x;

        if(!isOnTargetY)
            isOnTargetY = (vy > 0) ? newY > targetPosition.y : newY < targetPosition.y;


        //Sets Position to Target Position if On Target
        if(isOnTargetX)
            positionComponent.position.x = targetPosition.x;

        if(isOnTargetY)
            positionComponent.position.y = targetPosition.y;


        if (isOnTargetX && isOnTargetY) moveToComponent.movementPositions.removeIndex(0);

        //Sets Velocity To Zero Once All Destination Have Been Visited
        if (moveToComponent.movementPositions.size <= 0) {
            velocityComponent.velocity.x = 0;
            velocityComponent.velocity.y = 0;
        }


    }

}
