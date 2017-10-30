package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ChildComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ParentComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 29/10/2017.
 */

public class PlayerGraphicalTargetingSystem extends BaseSystem {
    @Override
    protected void processSystem() {

    }





    public void createMovementTiles(Entity entity, int movementRange){

        CoordinateComponent coordinateComponent = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class);

        Array<Coordinates> coordinatesArray = CoordinateMath.getCoordinatesInRange(coordinateComponent.coordinates, movementRange);

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for(Coordinates c : coordinatesArray){

            final Queue<Coordinates> coordinatesQueue = new Queue<Coordinates>();

            boolean bool = tileSystem.findShortestPath(coordinateComponent.coordinates, c, coordinatesQueue, false);
            System.out.println("Path bool is" + bool);
            if(coordinatesQueue.size <= movementRange && bool) {

                Rectangle r = new Rectangle();

                if(tileSystem.getRectangleUsingCoordinates(c, r)) {


                    Entity box = world.createEntity();
                    box.edit().add(new PositionComponent(r.x, r.y));
                    box.edit().add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                            new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOCK)
                                    .color(new Color(Color.WHITE))
                                    .width(r.getWidth())
                                    .height(r.getHeight())
                                    .build()));
                    box.edit().add(new FadeComponent(true, 1.0f, true));
                    box.edit().add(new ChildComponent(entity.getComponent(ParentComponent.class)));
                    box.edit().add(new HitBoxComponent(new HitBox(r)));
                    box.edit().add(new ActionOnTapComponent(new WorldAction() {
                        @Override
                        public void performAction(World world, Entity entity) {

                            Entity player = world.getSystem(FindPlayerSystem.class).getPlayerEntity();

                            System.out.println("Size of queue is " + coordinatesQueue.size);

                            for(Coordinates c : coordinatesQueue){
                                player.getComponent(MoveToComponent.class).movementPositions.add(
                                        world.getSystem(TileSystem.class).getPositionUsingCoordinates(
                                                c, player.getComponent(BoundComponent.class).bound));

                            }

                            world.getSystem(DeathSystem.class).killChildComponents(
                                    world.getSystem(ParentChildSystem.class).getParent(entity.getComponent(ChildComponent.class)));

                        }
                    }));


                }

            }

        }

    }



}
