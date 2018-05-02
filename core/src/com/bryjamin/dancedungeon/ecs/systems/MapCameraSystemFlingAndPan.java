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

public class MapCameraSystemFlingAndPan extends EntitySystem {

    private ComponentMapper<PositionComponent> positionm;
    private ComponentMapper<FixedToCameraComponent> fixedm;

    private Camera camera;

    private float cameraVelocityX;
    private float cameraVelocityY;
    private boolean isFlung = false;

    private float minX;
    private float minY;
    private float maxX;
    private float maxY;

    private float speedDecay = Measure.units(125f);

    private static final float speedDecayX = Measure.units(2.5f);

    private float time = 0f;

    /**
     * In this context 'min' referes to the minimum point on the x and y axis a camera can show
     * 'max' references to the maximum point on the x and y axis a camera can show
     *
     * This system handles 'flinging' the map and panning the map using touch controls.
     *
     * This system also handles keeping the camera within the correct X and Y bounds, regardless of placement by
     * external systems.
     *
     */
    public MapCameraSystemFlingAndPan(Camera camera, float minX, float minY, float maxX, float maxY){
        super(Aspect.all(PositionComponent.class, FixedToCameraComponent.class));
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.camera = camera;
    }


    @Override
    public void inserted(Entity e) {
        updateEntityPosition(e);
    }

    @Override
    protected void processSystem() {


        if(isFlung) {
            time += world.delta;
            flingDecelerate(time);
        }

        //Check bounds of camera, if out of bounds stop and set position.

        if (CameraMath.getBtmLftX(camera) < minX) {
            stopFling();
            CameraMath.setBtmLeftX(camera, minX);
        }

        if (CameraMath.getBtmRightX(camera) > maxX) {
            stopFling();
            CameraMath.setBtmRightX(camera, maxX);
        }

        camera.update();
        updateEntityPositions();

    }


    /**
     * Some entities follow the camera's position and as such are updated using this method.
     * @param e
     */
    private void updateEntityPosition(Entity e){
        PositionComponent pc = positionm.get(e);
        FixedToCameraComponent ftcc = fixedm.get(e);

        pc.setX(CameraMath.getBtmLftX(camera) + ftcc.offsetX);
        pc.setY(CameraMath.getBtmY(camera) + ftcc.offsetY);
    }

    /**
     * Loops through all entities of the system and updates their position respective to the camera.
     */
    private void updateEntityPositions(){
        for(Entity e : this.getEntities()){
            updateEntityPosition(e);
        }
    }

    /**
     * Deceelerates the camera after it has been flung.
     * @param time - This variable references currentDuration that has passed since the camera was flung.
     */
    public void flingDecelerate(float time) {
        if (this.cameraVelocityX != 0f || this.cameraVelocityY != 0f) {
            float newFlingX = Math.max(0,  Math.abs(this.cameraVelocityX)-Math.abs(speedDecayX)*1f*time);
            float newFlingY = Math.max(0,  Math.abs(this.cameraVelocityY)-Math.abs(this.cameraVelocityY)*1f*time);

            //Based on the direction of the camera's velocity decelerate in the appropriate direction.
            this.cameraVelocityX = this.cameraVelocityX < 0 ? newFlingX*-1 : newFlingX;

            this.cameraVelocityY = this.cameraVelocityY < 0 ? newFlingY*-1 : newFlingY;

            float minStopSpeed = Measure.units(1f); //Minimum speed reached before forcing a complete stop
            if ( Math.abs(this.cameraVelocityX) < minStopSpeed ) this.cameraVelocityX = 0f;
            if ( Math.abs(this.cameraVelocityY) < minStopSpeed ) this.cameraVelocityY = 0f;


            //Y not added, not as only left and right directions needed, but in future if a y-direction is needed for
            //panning add in velocityY.
            camera.position.add(cameraVelocityX * world.delta, 0 * world.delta, 0);

            if (this.cameraVelocityX == 0 && this.cameraVelocityY == 0){
                isFlung = false;
                this.time = 0;
            }
              //  Gdx.graphics.setContinuousRendering(false);
        }
    }

    public void flingCamera(float velocityX, float velocityY){
        isFlung = true;
        cameraVelocityX = velocityX * 1.5f;
    }

    public void stopFling(){
        isFlung = false;
        cameraVelocityX = 0;
        cameraVelocityY = 0;
        time = 0;
    }

}
