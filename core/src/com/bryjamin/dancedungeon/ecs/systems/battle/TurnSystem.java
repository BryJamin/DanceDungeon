package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerComponent;

import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ALLY;

/**
 * Created by BB on 21/10/2017.
 */

public class TurnSystem extends EntitySystem {

    private ComponentMapper<TurnComponent> turnMapper;

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerComponent> playerMapper;

    public enum TURN {
        ENEMY, ALLY
    }

    public TURN turn = ALLY;

    @SuppressWarnings("unchecked")
    public TurnSystem() {
        super(Aspect.all(TurnComponent.class));
    }


    @Override
    protected void processSystem() {


        switch (turn){

            case ENEMY:

                for(Entity e : this.getEntities()){
                    if(enemyMapper.has(e) && !turnMapper.get(e).isTurnOver){
                            return;
                    }
                }

                turn = ALLY;

                for(Entity e : this.getEntities()){
                    if(playerMapper.has(e)){
                        turnMapper.get(e).isTurnOver = false;
                    }
                }

                break;


            case ALLY:


                for(Entity e : this.getEntities()){
                    if(playerMapper.has(e) && !turnMapper.get(e).isTurnOver){
                        return;
                    }
                }

                turn = ALLY;

                for(Entity e : this.getEntities()){
                    if(enemyMapper.has(e)){
                        turnMapper.get(e).isTurnOver = false;
                    }
                }

                break;





        }

    }







}