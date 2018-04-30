package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyIntentUIComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.FriendlyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.options.DevOptions;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;

import java.util.Comparator;

public class UtilityAiSystem extends EntitySystem {

    private ActionQueueSystem actionQueueSystem;
    private TileSystem tileSystem;

    private ComponentMapper<UnitComponent> unitM;
    private ComponentMapper<TurnComponent> turnM;
    private ComponentMapper<CoordinateComponent> coordsM;
    private ComponentMapper<TargetComponent> targetM;
    private ComponentMapper<FriendlyComponent> friendlyM;


    private OrderedMap<Coordinates, Queue<Coordinates>> pathsMap = new OrderedMap<>();
    private OrderedMap<Coordinates, CoordinateScore> scoreMap = new OrderedMap<>();


    private Array<Coordinates> inLineOfFireCoordinates = new Array<>();
    private Array<Entity> targetsArray = new Array<>();

    public UtilityAiSystem() {
        super(Aspect.all(UtilityAiComponent.class, UnitComponent.class, CoordinateComponent.class, TurnComponent.class, TargetComponent.class));
    }


    private boolean debug = false;


    @Override
    protected void initialize() {
        Preferences preferences = Gdx.app.getPreferences(DevOptions.DEV_PREFS_KEY);
        debug = preferences.getBoolean(DevOptions.UTILITY_SCORE_DEBUG, false);
    }

    @Override
    protected void processSystem() {}


    private void calculateCoordinatesWithValidPaths(Entity e, UnitData unitData){


        Array<Coordinates> allMapCoordinates = new Array<>(tileSystem.getCoordinateMap().orderedKeys());
        allMapCoordinates.shuffle(); //Shuffle so the highest scored position is random if the scores are the same.

        for(Coordinates c : tileSystem.getCoordinateMap().orderedKeys()){
            Queue<Coordinates> queue = new Queue<>();
            if(tileSystem.findShortestPath(e, queue, c, unitData.getMovementRange())){//TODO change maxDistance
                pathsMap.put(c, queue);
                scoreMap.put(c, new CoordinateScore(c, 0));
            }
        };

        Coordinates current = coordsM.get(e).coordinates;
        Queue<Coordinates> queue = new Queue<>();
        queue.addLast(current);


        pathsMap.put(current, queue);
        scoreMap.put(current, new CoordinateScore(current, 0));



    }


    /**
     * Updates the array of 'In Line of Fire' Coordinates
     * This Coordinates track which places on the map would result in a character getting hit by a
     * telegraphed attack
     */
    private void updateInLineOfFireCoordinates(){

        inLineOfFireCoordinates.clear();

        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(EnemyIntentUIComponent.class, CoordinateComponent.class)).getEntities();

        for(int i = 0; i < bag.size(); i++){
            inLineOfFireCoordinates.add(world.getEntity(bag.get(i)).getComponent(CoordinateComponent.class).coordinates);
        }

    }

    private void updateTargetsArray(Entity e){
        targetsArray = targetM.get(e).getTargets(world);
    }



    private AttackScore calculateAttackScoreOfACoordinate(Coordinates c, Skill s){

        Array<Coordinates> affectedCoords = s.getAffectedCoordinates(world, c);
        affectedCoords.shuffle(); //Randomizes the Top Score
        AttackScore attackScore = new AttackScore();

        for(int i = 0; i < targetsArray.size; i++){

            float score = 0;
            Coordinates targetCoordinate = coordsM.get(targetsArray.get(i)).coordinates;

            if(affectedCoords.contains(targetCoordinate, false)){
                //Affected coordinates with 'Friendly' targets e.g bases. Are ranked higher
                score = friendlyM.has(targetsArray.get(i)) ? 20 : 10; //TODO If Ally AI was introduced, what would they prioritize?
            };

            if(attackScore.score < score) { //Finds the highest scoring Attack and stores the Skill and Coordinate of Target
                attackScore.score = score;
                attackScore.attack = s;
                attackScore.target = targetCoordinate;
            }
        }

        return attackScore;
    }


    /**
     * Update the Score Map.
     * Checks if the Paths generated are too long, Or if the Coordinate is in the line of fire.
     * Checks if the 'attack' score of the co-ordinate
     *
     *
     * @param skill
     */
    private void updateScoreMap(Skill skill){

        for(Coordinates c : pathsMap.orderedKeys()) {

            scoreMap.get(c).attackScore = calculateAttackScoreOfACoordinate(c, skill);

            if(pathsMap.get(c).last() != c){//If the path does not end at the target co-ordinate due to it being too long, reduce the movementScore
                scoreMap.get(c).movementScore -= 5;
            }

            //Reduce movementScore if in the path of another enemy shot
            if(inLineOfFireCoordinates.contains(c, false)){
                scoreMap.get(c).movementScore -= 15;
            }

        }

    }


    /**
     * Calculates the Attack and Movement scores for Each co-ordinate reachable by this entity.
     */
    private boolean calculateScoreForAllCoordinates(Entity e){

        pathsMap.clear();
        scoreMap.clear();

        if(!this.getEntities().contains(e)) return false;

        UnitData unitData = unitM.get(e).getUnitData();
        Skill skill = unitData.getSkills().first();

        calculateCoordinatesWithValidPaths(e, unitData);
        updateInLineOfFireCoordinates();
        updateTargetsArray(e);
        updateScoreMap(skill);

        return true;
    }


    /**
     * Calculates
     * @param e
     */
    public void calculateMove(Entity e){

        if(!calculateScoreForAllCoordinates(e)) return;

        Array<CoordinateScore> scores = scoreMap.values().toArray();

        scores.sort(new Comparator<CoordinateScore>() {
            @Override
            public int compare(CoordinateScore c1, CoordinateScore c2) {
                return Float.compare(c2.total(), c1.total()); //Reversed as needs higher value.
            }
        });

        final CoordinateScore chosen = scores.get(0);
        actionQueueSystem.createMovementAction(e, pathsMap.get(chosen.coordinates));

        //System.out.println(pathsMap.get(chosen.coordinates).last().equals(chosen.coordinates));

        //Checks if the character is at the correct coordinate to attack. And that the attack is not null.
        // (Null Skills exist if skills can't target anything on a particulat coordinate)
        if((pathsMap.get(chosen.coordinates).last().equals(chosen.coordinates)) && chosen.attackScore.attack != null) {

            actionQueueSystem.pushLastAction(e, new WorldConditionalAction() {
                @Override
                public boolean condition(World world, Entity entity) {
                    return true;
                }

                @Override
                public void performAction(World world, Entity entity) {

                    entity.edit().add(new StoredSkillComponent(entity.getComponent(CoordinateComponent.class).coordinates,
                            chosen.attackScore.target, chosen.attackScore.attack));

                    world.getSystem(EnemyIntentUISystem.class).updateIntent();

                    //chosen.s.cast(world, entity, chosen.target);
                }
            });

        }

        TurnComponent turnComponent = turnM.get(e);
        turnComponent.movementActionAvailable = false;
        turnComponent.attackActionAvailable = false;

    }


    private void checkIfCoordinateIsInLineOfFire(){






    }



    private class CoordinateScore {

        AttackScore attackScore = new AttackScore();
        Coordinates coordinates;
        float movementScore;

        public CoordinateScore(Coordinates coordinates, float movementScore){
            this.coordinates = coordinates;
            this.movementScore = movementScore;
        }

        public float total(){
            return attackScore.score + movementScore;
        }

    }




    private class AttackScore {

        public float score;
        public Skill attack;
        public Coordinates castLocation;
        public Coordinates target;

    }





    public void createDebugScoreTools(Entity e){

        if(!debug) return;

        calculateScoreForAllCoordinates(e);

        for(Coordinates c : scoreMap.orderedKeys()){

            Entity ui = world.createEntity();
            float size = Measure.units(1.5f);
            Rectangle r = tileSystem.createRectangleUsingCoordinates(c);
            Vector2 center = r.getCenter(new Vector2());

            ui.edit().add(new PositionComponent(CenterMath.centerOnPositionX(size, center.x),
                    CenterMath.centerOnPositionY(size, center.y)));
            ui.edit().add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                    new TextDescription.Builder(Fonts.SMALL)
                            .text("" + scoreMap.get(c).total())
                            //.width(r.width)
                            //.height(r.height)
                            .build()));

            ui.edit().add(new CenteringBoundComponent(size, size));
            ui.edit().add(new UITargetingComponent());

        }
    }




}







