package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
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
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.player.spells.animations.Skill;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 29/10/2017.
 */

public class PlayerGraphicalTargetingSystem extends BaseSystem {


    private Bag<Entity> trackedEntities = new Bag<Entity>();

    private Coordinates targetCoordinates = null;


    @Override
    protected void processSystem() {

    }

    public Coordinates getTargetCoordinates() {
        return targetCoordinates;
    }

    public boolean createTarget(float x, float y){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Coordinates c = tileSystem.getCoordinatesUsingPosition(x, y);

        if(c == null) return false;

        if(c == targetCoordinates) {
            clearTrackedEntites(); return false;
        }

        clearTrackedEntites();
        targetCoordinates = c;
        Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(tileSystem.getRectangleUsingCoordinates(c)));
        trackedEntities.add(box);

        return true;

    }


    public void createTargetTile(final Entity entity, final Skill skill, int range) {

        clearTrackedEntites();

        IntBag intBag = world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class, HealthComponent.class, CoordinateComponent.class)).getEntities();

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for (int i = 0; i < intBag.size(); i++) {


            Coordinates c1 = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class).coordinates;
            Coordinates c2 = world.getEntity(intBag.get(i)).getComponent(CoordinateComponent.class).coordinates;

            if (tileSystem.getOccupiedMap().containsValue(world.getEntity(intBag.get(i)), true)
                    && CoordinateMath.isWithinRange(c1, c2, range)) {


                final Coordinates c = tileSystem.getOccupiedMap().findKey(world.getEntity(intBag.get(i)), true);

                Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(tileSystem.getRectangleUsingCoordinates(c)));

                box.edit().add(new ActionOnTapComponent(new WorldAction() {
                    @Override
                    public void performAction(World world, final Entity e) {

                        skill.cast(world, entity, c);
                        clearTrackedEntites();

                    }
                }));

                trackedEntities.add(box);
            }

        }


    }

    public void clearTrackedEntites() {
        if (trackedEntities.size() > 0) {
            for (Entity e : trackedEntities) {
                e.edit().add(new DeadComponent());
            }
        }
        trackedEntities.clear();
        targetCoordinates = null;
    }


    public ComponentBag highlightBox(Rectangle r) {
        ComponentBag bag = new ComponentBag();

        bag.add(new PositionComponent(r.x, r.y));
        bag.add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOCK)
                        .color(new Color(Color.WHITE))
                        .width(r.getWidth())
                        .height(r.getHeight())
                        .build()));
        bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new HitBoxComponent(new HitBox(r)));
        bag.add(new BoundComponent());

        return bag;
    }


    public void createMovementTiles(Entity entity, int movementRange) {

        clearTrackedEntites();

        CoordinateComponent coordinateComponent = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class);

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for (Coordinates c : CoordinateMath.getCoordinatesInMovementRange(coordinateComponent.coordinates, movementRange)) {

            if(!tileSystem.getCoordinateMap().containsKey(c)) continue;

            final Queue<Coordinates> coordinatesQueue = new Queue<Coordinates>();

            Array<Coordinates> coordinatesArray = new Array<Coordinates>();
            coordinatesArray.add(c);

            boolean bool = tileSystem.findShortestPath(coordinatesQueue, coordinateComponent.coordinates, coordinatesArray);


            System.out.println(coordinatesQueue.size);
            if (coordinatesQueue.size <= movementRange && bool) {



                Rectangle r = tileSystem.getRectangleUsingCoordinates(c);


                Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(r));
                trackedEntities.add(box);

                box.edit().add(new ActionOnTapComponent(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {

                        Entity player = world.getSystem(FindPlayerSystem.class).getPlayerEntity();

                        for (Coordinates c : coordinatesQueue) {
                            player.getComponent(MoveToComponent.class).movementPositions.add(
                                    world.getSystem(TileSystem.class).getPositionUsingCoordinates(
                                            c, player.getComponent(BoundComponent.class).bound));
                        }

                        player.getComponent(AbilityPointComponent.class).abilityPoints -= 2;

                        clearTrackedEntites();

                    }
                }));


            }

        }

    }


}
