package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.AvailableActionsCompnent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.factories.unit.UnitData;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import java.util.Stack;

/**
 * Keeps track of older Coordinate placements and Unit stats to be called upon when the undo
 * button is pressed
 */
public class UndoMoveSystem extends EntitySystem{

    private TileSystem tileSystem;
    private TurnSystem turnSystem;




    private Stack<OrderedMap<Entity, UnitSnapShot>> snapShotStack = new Stack<>();

    /**
     * KeepCreates an entity system that uses the specified aspect as a matcher
     * against entities.
     *
     *
     */
    public UndoMoveSystem() {
        super(Aspect.all(UnitComponent.class, CoordinateComponent.class, AvailableActionsCompnent.class));
    }


    @Override
    protected void initialize() {
    }

    @Override
    protected void processSystem() {

    }


    public void snapShotUnits(){

        OrderedMap<Entity, UnitSnapShot> entityMap = new OrderedMap<>();

        //Save the UnitData and Coordinate Position of entities.
        for(Entity e : this.getEntities()){
            UnitSnapShot snapShot = new UnitSnapShot(e);
            entityMap.put(e, snapShot);
        }

        if(entityMap.size > 0){
            snapShotStack.push(entityMap);
        }

    }

    public void popSnapShot(){

        if(snapShotStack.isEmpty()) return;

        OrderedMap<Entity, UnitSnapShot> entityMap = snapShotStack.pop();

        for(Entity e : this.getEntities()){
            if(entityMap.containsKey(e)){
                entityMap.get(e).applySnapShot(e);
            }
        }

    }

    public boolean hasSnapShot(){
        return snapShotStack.isEmpty();
    }


    private class UnitSnapShot {

        private UnitData unitData;
        private Coordinates coordinates;
        private AvailableActionsCompnent availableActionsCompnent;

        public UnitSnapShot(Entity e){
            this.unitData = new UnitData(e.getComponent(UnitComponent.class).getUnitData());
            this.coordinates = new Coordinates(e.getComponent(CoordinateComponent.class).coordinates);


            this.availableActionsCompnent = new AvailableActionsCompnent(e.getComponent(AvailableActionsCompnent.class));
        }

        public void applySnapShot(Entity e){

            e.edit().remove(AvailableActionsCompnent.class);
            e.edit().add(availableActionsCompnent);

            e.getComponent(UnitComponent.class).getUnitData().copyUnitDataAttributes(unitData);
            e.getComponent(CoordinateComponent.class).coordinates.set(coordinates);

            e.getComponent(PositionComponent.class).position.set(
                    tileSystem.getPositionUsingCoordinates(
                            e.getComponent(CoordinateComponent.class).coordinates,
                            e.getComponent(CenteringBoundComponent.class).bound));

        }





    }


    public void clearSnapShots(){
        this.snapShotStack.clear();
    }


}
