package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.FriendlyComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import java.util.Comparator;


/**
 * Created by BB on 06/03/2018.
 */

public class FindBestMovementAreaToAttackFromAction implements WorldAction {



    TargetingFactory targetingFactory = new TargetingFactory();


    @Override
    public void performAction(World world, Entity entity) {

        StatComponent statComponent = entity.getComponent(StatComponent.class);
        Coordinates current = entity.getComponent(CoordinateComponent.class).coordinates;

        OrderedMap<Coordinates, Queue<Coordinates>> possiblePaths = new OrderedMap<Coordinates, Queue<Coordinates>>();

        Array<CoordinateScore> coordinateScores = new Array<CoordinateScore>();

        SkillsComponent skillsComponent = entity.getComponent(SkillsComponent.class);
        Skill mainSkill = skillsComponent.skills.first();

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        //Coordinates where your storedTargetCoordinates is
        Array<Coordinates> targetCoordinatesArray = new Array<Coordinates>();

        for(Entity e : entity.getComponent(TargetComponent.class).getTargets(world)){
            targetCoordinatesArray.add(new Coordinates(e.getComponent(CoordinateComponent.class).coordinates));
        };


        System.out.println("Target coords size " + targetCoordinatesArray.size);



        Array<Coordinates> allCoords = tileSystem.getCoordinateMap().orderedKeys();
        allCoords.shuffle(); //Avoids just picking the next coordinate in line

        for(Coordinates c : allCoords){

            float score = 0;

            for(Entity e : entity.getComponent(TargetComponent.class).getTargets(world)) {
                if(mainSkill.getAffectedCoordinates(world, c).contains(e.getComponent(CoordinateComponent.class).coordinates, false)){
                    score += 10; //Good Coordinate

                    if(e.getComponent(FriendlyComponent.class) != null){
                        score += 5; //Focus on objectives
                    }

                    Queue<Coordinates> path = new Queue<Coordinates>();
                    if(tileSystem.findShortestPath(entity, path, c,  statComponent.movementRange)){
                        possiblePaths.put(c, path);
                        if(path.size > statComponent.movementRange)
                            score -= 5; //Can't be reached in one movement
                    } else {
                        score = 0;
                    }


                } else {



                    //TODO WHAT HAPPENS IF YOU CAN'T ATTACK? DO YOU JUST TRY TO FIND COORDINATES CLOSEST?

                }
            }

            coordinateScores.add(new CoordinateScore(c, score));
        }

        coordinateScores.sort(new Comparator<CoordinateScore>() {
            @Override
            public int compare(CoordinateScore c1, CoordinateScore c2) {
                return c1.score > c2.score ? -1 : (c1.score == c2.score ? 0 : 1);
            }
        });


        Queue<Coordinates> path = new Queue<Coordinates>();

        for(CoordinateScore cs: coordinateScores){
            if(cs.score == 0) continue;
            path = possiblePaths.get(cs.coordinates);
            break;
        }

        world.getSystem(ActionCameraSystem.class).createMovementAction(entity, path);
        entity.getComponent(TurnComponent.class).movementActionAvailable = false;



        //TODO CREATE A SCORING SYSTEM THAT CHECKS COORDINATE AGAINST SCORE


        //TODO COORDINATES THAT HAVE NOTHING TO ATTACK FROM RECEIVE A NEGATIVE SCORE
        //TODO THE FURTHER THEY ARE THE MORE NEGATIVE THE SCORE.


        //TODO CONVERT SKILLS TO SHOW WHICH TILES THEY CAN AFFECT FROM A GIVEN TARGET POSITION

        //TODO BASED ON TARGETS THAT CAN BE ATTACK INCREASE THE SCORE OF A TILE BY +5

        //TODO DISTANCE REDUCES THE SCORE EVEN ON POSITIVE TILES THOUGH THIS WILL BE BASED MORE ON ENEMY PERSONAILITY?

        //TODO FOR NOW KEEP IT SIMPLE


        //TODO SORT SCORES BY MOST SCORED. THEN DECIDE WHETHER TO POP/REMOVE THE BEST RESULT BY A RANDOM CHANCE




















    }



    private class CoordinateScore {

        Coordinates coordinates;
        float score;

        public CoordinateScore(Coordinates coordinates, float score){
            this.coordinates = coordinates;
            this.score = score;
        }


    }






}
