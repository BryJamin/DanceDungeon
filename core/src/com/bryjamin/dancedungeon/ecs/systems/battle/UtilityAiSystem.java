package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.ai.actions.EndTurnAction;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyIntentUIComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.FriendlyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

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


    public UtilityAiSystem() {
        super(Aspect.all(UtilityAiComponent.class, UnitComponent.class, CoordinateComponent.class, TurnComponent.class, TargetComponent.class));
    }

    @Override
    protected void processSystem() {}


    /**
     * Calculates
     * @param e
     */
    public void calculateMove(Entity e){

        pathsMap.clear();
        scoreMap.clear();

        if(!this.getEntities().contains(e)) return;

        if(!turnM.get(e).hasActions()) new EndTurnAction().performAction(world, e);

        TurnComponent turnComponent = turnM.get(e);

        //Currently is assumed Enemy AIs only have 1 skill.
        UnitData unitData = unitM.get(e).getUnitData();

        Skill skill = unitData.getSkills().first();

        //Get Coordinates that can be reached.

        Array<Coordinates> allMapCoordinates = new Array<>(tileSystem.getCoordinateMap().orderedKeys());
        allMapCoordinates.shuffle(); //Shuffle so the highest scored position is random if the scores are the same.

        for(Coordinates c : tileSystem.getCoordinateMap().orderedKeys()){
            Queue<Coordinates> queue = new Queue<>();
            if(tileSystem.findShortestPath(e, queue, c, unitData.getMovementRange())){//TODO change maxDistance
                pathsMap.put(c, queue);
                scoreMap.put(c, new CoordinateScore(c, 0));
            }
        };

        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(EnemyIntentUIComponent.class, CoordinateComponent.class)).getEntities();
        Array<Coordinates> inLineOfFireCoordinates = new Array<>();
        for(int i = 0; i < bag.size(); i++){
            inLineOfFireCoordinates.add(world.getEntity(bag.get(i)).getComponent(CoordinateComponent.class).coordinates);
        }




        //Array containing co-ordinates of targets
        Array<Coordinates> targetCoordinates = new Array<>();

        for(Entity target : targetM.get(e).getTargets(world)){
            targetCoordinates.add(new Coordinates(coordsM.get(target).coordinates));
        };


        Array<Entity> targets = targetM.get(e).getTargets(world);


        //TODO reduce score for if EnemyIntentUI is in a certain coordinate

        //Calculate score based on which coordinates you can attack from.
        for(Coordinates c : pathsMap.orderedKeys()) {
            Array<Coordinates> affectedCoords = skill.getAffectedCoordinates(world, c);

            AttackScore attackScore = new AttackScore();
            float score = 0;

            for(int i = 0; i < targets.size; i++){

                float temp = 0;

                Coordinates targetCoordinate = targets.get(i).getComponent(CoordinateComponent.class).coordinates;

                if(affectedCoords.contains(targetCoordinate, false)){

                    temp = 10;

                    if(friendlyM.has(targets.get(i))){
                        temp = 20;
                    }

                };

                if(score < temp) {

                    attackScore.score = temp;
                    attackScore.attack = skill;
                    attackScore.target = targetCoordinate;

                    score = temp;

                }
            }

            scoreMap.get(c).score += attackScore.score;
            scoreMap.get(c).s = attackScore.attack;
            scoreMap.get(c).target = attackScore.target;

            if(pathsMap.get(c).last() != c){//If the does not end the target co-ordinate due to it being too long, reduce the score
                scoreMap.get(c).score -= 5;
            }

            //Reduce score if in the path of another enemy shot
       /*     if(inLineOfFireCoordinates.contains(c, false)){
                scoreMap.get(c).score -= 10;
            }*/

        }


        Array<CoordinateScore> scores = scoreMap.values().toArray();

        scores.sort(new Comparator<CoordinateScore>() {
            @Override
            public int compare(CoordinateScore c1, CoordinateScore c2) {
                return c1.score > c2.score ? -1 : (c1.score == c2.score ? 0 : 1);
            }
        });


        final CoordinateScore chosen = scores.get(0);

        System.out.println(chosen.score);

        actionQueueSystem.createMovementAction(e, pathsMap.get(chosen.coordinates));


        if((pathsMap.get(chosen.coordinates).last().equals(chosen.coordinates))) {

            actionQueueSystem.pushLastAction(e, new WorldConditionalAction() {
                @Override
                public boolean condition(World world, Entity entity) {
                    return true;
                }

                @Override
                public void performAction(World world, Entity entity) {

                    entity.edit().add(new StoredSkillComponent(entity.getComponent(CoordinateComponent.class).coordinates,
                            chosen.target, chosen.s));

                    world.getSystem(EnemyIntentUISystem.class).updateIntent();

                    //chosen.s.cast(world, entity, chosen.target);
                }
            });

        }


        turnComponent.movementActionAvailable = false;
        turnComponent.attackActionAvailable = false;

    }

    private class CoordinateScore {

        Coordinates coordinates;
        float score;

        public Skill s;
        public Coordinates target;

        public CoordinateScore(Coordinates coordinates, float score){
            this.coordinates = coordinates;
            this.score = score;
        }

    }


    private class AttackScore {

        public float score;
        public Skill attack;
        public Coordinates castLocation;
        public Coordinates target;

    }




}







