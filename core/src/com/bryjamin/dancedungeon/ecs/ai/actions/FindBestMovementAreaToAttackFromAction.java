package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.utils.math.Coordinates;


/**
 * Created by BB on 06/03/2018.
 */

public class FindBestMovementAreaToAttackFromAction implements WorldAction {



    TargetingFactory targetingFactory = new TargetingFactory();


    @Override
    public void performAction(World world, Entity entity) {

        StatComponent statComponent = entity.getComponent(StatComponent.class);
        Coordinates current = entity.getComponent(CoordinateComponent.class).coordinates;

        OrderedMap<Coordinates, Queue<Coordinates>> possiblePaths = targetingFactory.createPathsToCoordinatesInMovementRange(world.getSystem(TileSystem.class), entity, current, statComponent.movementRange);

        Array<CoordinateScore> coordinateScores = new Array<CoordinateScore>();


        for(Coordinates c : possiblePaths.orderedKeys()){


            float score = 1;

          //  coordinateScores.add(c);

        }






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
