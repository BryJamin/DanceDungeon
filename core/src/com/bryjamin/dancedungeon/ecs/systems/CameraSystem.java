package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.bryjamin.dancedungeon.ecs.components.FixedToCameraComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CameraMath;

/**
 * Created by BB on 18/01/2018.
 */

public class CameraSystem extends EntitySystem {

    private ComponentMapper<PositionComponent> positionm;
    private ComponentMapper<FixedToCameraComponent> fixedm;

    private Camera camera;

    private float cameraVelocityX;
    private float cameraVelocityY;
    private boolean flingActionFlag = false;

    private float minX;
    private float minY;
    private float maxX;
    private float maxY;

    private float speedDecay = Measure.units(125f);

    private float time = 0f;

    /**
     * In this context 'min' referes to the minimum point on the x and y axis a camera can show
     * 'max' references to the maximum point on the x and y axis a camera can show
     *
     * The CameraSystem is placed afer all renderable systems, so that way on the next loop,
     * anything referencing the camera position is not incorrect
     *
     */
    public CameraSystem(Camera camera, float minX, float minY, float maxX, float maxY){
        super(Aspect.all(PositionComponent.class, FixedToCameraComponent.class));
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.camera = camera;
    }


    @Override
    public void inserted(Entity e) {
        //super.inserted(e);
        updateEntityPosition(e);
    }

    @Override
    protected void processSystem() {


        if(flingActionFlag) {
            time += world.delta;
            flingDecelerate(time);

            if (CameraMath.getBtmLftX(camera) < minX) {
                stopFling();
                CameraMath.setBtmLeftX(camera, minX);
            }

            if (CameraMath.getBtmRightX(camera) > maxX) {
                stopFling();
                CameraMath.setBtmRightX(camera, maxX);
            }
            camera.update();
        }

        updateEntityPositions();

        //pc.position.add(vc.velocity.x * world.delta, vc.velocity.y * world.delta, 0);

    }


    private void updateEntityPosition(Entity e){
        PositionComponent pc = positionm.get(e);
        FixedToCameraComponent ftcc = fixedm.get(e);

        pc.setX(CameraMath.getBtmLftX(camera) + ftcc.offsetX);
        pc.setY(CameraMath.getBtmY(camera) + ftcc.offsetY);
    }

    private void updateEntityPositions(){
        for(Entity e : this.getEntities()){
            updateEntityPosition(e);
        }
    }

    public void flingDecelerate(float time) {
        if (this.cameraVelocityX != 0f || this.cameraVelocityY != 0f) {
            float newFlingX = Math.max(0,  Math.abs(this.cameraVelocityX)-Math.abs(Measure.units(2.5f))*1f*time);
            float newFlingY = Math.max(0,  Math.abs(this.cameraVelocityY)-Math.abs(this.cameraVelocityY)*1f*time);
            if ( this.cameraVelocityX < 0 )
                this.cameraVelocityX = newFlingX*-1;
            else
                this.cameraVelocityX = newFlingX;
            if ( this.cameraVelocityY < 0 )
                this.cameraVelocityY = newFlingY*-1;
            else
                this.cameraVelocityY = newFlingY;

            float minStopSpeed = Measure.units(1f);
            if ( Math.abs(this.cameraVelocityX) < minStopSpeed ) this.cameraVelocityX = 0f;
            if ( Math.abs(this.cameraVelocityY) < minStopSpeed ) this.cameraVelocityY = 0f;

            camera.position.add(cameraVelocityX * world.delta, 0 * world.delta, 0);


            if (this.cameraVelocityX == 0 && this.cameraVelocityY == 0){
                flingActionFlag = false;
                this.time = 0;
            }
              //  Gdx.graphics.setContinuousRendering(false);
        }
    }

    public void flingCamera(float velocityX, float velocityY){
        flingActionFlag = true;
        cameraVelocityX = velocityX * 1.5f;
    }

    public void stopFling(){
        flingActionFlag = false;
        cameraVelocityX = 0;
        time = 0;
    }

}
