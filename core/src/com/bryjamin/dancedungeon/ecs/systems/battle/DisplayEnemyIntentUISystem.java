package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.GrowAndShrinkTransformationComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyIntentUIComponent;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.enums.Direction;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;


/**
 * Created by BB on 06/03/2018.
 *
 * Used to show enemy intent to players
 */

public class DisplayEnemyIntentUISystem extends EntitySystem implements Observer{


    TileSystem tileSystem;

    ComponentMapper<EnemyIntentUIComponent> eiMapper;
    ComponentMapper<StoredSkillComponent> storedMapper;
    ComponentMapper<CoordinateComponent> coordinateMapper;

    private ActionQueueSystem actionQueueSystem;

    TargetingFactory targetingFactory = new TargetingFactory();

    private boolean processingFlag = false;

    public DisplayEnemyIntentUISystem() {
        super(Aspect.all(StoredSkillComponent.class, CoordinateComponent.class));
    }

    @Override
    protected void initialize() {
        actionQueueSystem.observable.addObserver(this);
    }

    @Override
    protected void processSystem() {

        processingFlag = false;

        IntBag enemyIntent = world.getAspectSubscriptionManager().get(Aspect.all(EnemyIntentUIComponent.class)).getEntities();

        for(int i = 0; i < enemyIntent.size(); i++){
            world.delete(enemyIntent.get(i));
        }


        for(Entity e : this.getEntities()){
            Coordinates coordinates = e.getComponent(CoordinateComponent.class).coordinates;
            StoredSkillComponent storedSkillComponent = storedMapper.get(e);

            //For use of skills that have a fixed target, So upon movement their storedTargetCoordinates stays the same

            switch (storedSkillComponent.skill.getTargeting()){

                case StraightShot:

                   // if(!storedSkillComponent.storedCoordinates.equals(coordinates)){

                    Coordinates stored = storedSkillComponent.storedCoordinates;
                    Coordinates storedTarget = storedSkillComponent.storedTargetCoordinates;

                    Array<Coordinates> coordinatesArray = tileSystem.getFreeCoordinateInAGivenDirection(coordinates, new Direction[]{
                                CoordinateMath.getDirectionOfCoordinate(stored, storedTarget)});

                    Rectangle currentR1 = tileSystem.getRectangleUsingCoordinates(coordinates);

                    enemyIntentBox(tileSystem.getRectangleUsingCoordinates(coordinatesArray.peek()));
                    storedSkillComponent.storedTargetCoordinates = coordinatesArray.peek();
                    storedSkillComponent.storedCoordinates = coordinates;


                    //Adds red markers that have co-ordinates so AI known which areas may be attacked when they move.
                    for(Entity markers : new TargetingFactory().createRedTargetingMarkers(world, e.getComponent(CoordinateComponent.class).coordinates, coordinatesArray.peek())){

                        CoordinateComponent coordinateComponent = new CoordinateComponent(tileSystem.getCoordinatesUsingPosition(markers.getComponent(PositionComponent.class).position.x,

                                markers.getComponent(PositionComponent.class).position.y
                                ));

                        coordinateComponent.freePlacement = true;

                        markers.edit().add(coordinateComponent);


                    };
                    entityUIArrow(currentR1, CoordinateMath.getDirectionOfCoordinate(storedSkillComponent.storedCoordinates, storedSkillComponent.storedTargetCoordinates));


                    break;

                default:
                    if(!storedSkillComponent.storedCoordinates.equals(coordinates)){
                        int diffX = coordinates.getX() - storedSkillComponent.storedCoordinates.getX();
                        int diffY = coordinates.getY() - storedSkillComponent.storedCoordinates.getY();
                        storedSkillComponent.storedTargetCoordinates.addX(diffX);
                        storedSkillComponent.storedTargetCoordinates.addY(diffY);
                        storedSkillComponent.storedCoordinates.set(coordinates);
                    }

                    Rectangle r = tileSystem.getRectangleUsingCoordinates(storedSkillComponent.storedTargetCoordinates);
                    Rectangle currentR = tileSystem.getRectangleUsingCoordinates(storedSkillComponent.storedCoordinates);

                    if(r != null && currentR != null){ //This exists incase an enemies intent is pushed outside the bounds of the maps
                        //TODO decide what to do in this situation.
                        Entity highlight = enemyIntentBox(r);
                        entityUIArrow(currentR, CoordinateMath.getDirectionOfCoordinate(storedSkillComponent.storedCoordinates, storedSkillComponent.storedTargetCoordinates));
                    }
            }


            //Skills that have a malleable target which changes once they move, 'E.G a StraightShot type skill'

        }


    }

    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }

    public void updateIntent(){
        processingFlag = true;
    }


    @Override
    public void removed(Entity e) {
        updateIntent();
    }

    public boolean releaseAttack(){

        if(actionQueueSystem.isProcessing()) return true;

        if(this.getEntities().size() != 0){
            Entity e = this.getEntities().get(0);
            StoredSkillComponent storedSkillComponent = storedMapper.get(e);
            storedSkillComponent.skill.cast(world, e, storedSkillComponent.storedTargetCoordinates);
            e.edit().remove(StoredSkillComponent.class);
            return true;
        }
        return false;

    }

    public boolean releaseAttack(Entity e){
        if(this.getEntities().contains(e)){
            StoredSkillComponent storedSkillComponent = storedMapper.get(e);
            storedSkillComponent.skill.cast(world, e, storedSkillComponent.storedTargetCoordinates);
            e.edit().remove(StoredSkillComponent.class);
            return true;
        }
        return false;
    }



    public Entity enemyIntentBox(Rectangle r) {

        Entity e = world.createEntity();

        e.edit().add(new PositionComponent(r.x, r.y))
                .add(new DrawableComponent(Layer.BACKGROUND_LAYER_MIDDLE,
                        new TextureDescription.Builder(TextureStrings.ICON_WARNING)
                                .color(new Color(Colors.ENEMY_INTENT_HIGHLIGHT_BOX_COLOR))
                                .width(r.getWidth())
                                .height(r.getHeight())
                                .build()))
                .add(new HitBoxComponent(new HitBox(r)))
                .add(new CenteringBoundComponent())
                .add(new FadeComponent(new FadeComponent.FadeBuilder()
                        .minAlpha(0.5f)
                        .maximumTime(2f)))
                .add(new EnemyIntentUIComponent());

        return e;
    }


    private Entity entityUIArrow(Rectangle r, Direction d){

        float arrowSize = tileSystem.getMinimumCellSize() * 0.25f;

        Vector2 center = r.getCenter(new Vector2());

        Entity e = world.createEntity();

        float x = CenterMath.centerOnPositionX(arrowSize, center.x);
        float y = CenterMath.centerOnPositionY(arrowSize, center.y);

        float ratio = 0.45f;

        float offsetX = (d == Direction.RIGHT || d == Direction.LEFT) ?
                ((d == Direction.RIGHT) ? r.getWidth() * ratio : r.getWidth() * -ratio) : 0;
        float offsetY = (d == Direction.UP || d == Direction.DOWN) ?
                ((d == Direction.UP) ? r.getHeight() * ratio : r.getHeight() * -ratio) : 0;


        double rotation = (d == Direction.RIGHT || d == Direction.LEFT) ?
                (d == Direction.RIGHT ? 270 : 90) :
                (d == Direction.UP ? 0 : 180);

        e.edit().add(new PositionComponent(x + offsetX, y + offsetY))
                .add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                        new TextureDescription.Builder(TextureStrings.ICON_ARROW)
                                .color(new Color(Colors.ENEMY_INTENT_ARROW_COLOR))
                                .rotation(rotation)
                                .size(arrowSize)
                                .build()))
                .add(new HitBoxComponent(new HitBox(r)))
                .add(new CenteringBoundComponent())
                .add(new GrowAndShrinkTransformationComponent(1f, 1.3f, 1.3f))
                .add(new EnemyIntentUIComponent());


        return e;

    }


    @Override
    public void update(Object o) {//The Intent system watches both the turn and action camera system to decide when to update itself
        updateIntent();
    }
}
