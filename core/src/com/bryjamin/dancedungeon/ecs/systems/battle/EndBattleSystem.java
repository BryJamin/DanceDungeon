package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.screens.battle.PlayScreen;

/**
 * Created by BB on 28/11/2017.
 */

public class EndBattleSystem extends EntitySystem {

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerControlledComponent> pcMapper;


    private Bag<Entity> playerBag = new Bag<Entity>();
    private Bag<Entity> enemyBag = new Bag<Entity>();

    private MainGame game;

    private boolean processingFlag = false;

    public EndBattleSystem(MainGame game) {
        super(Aspect.one(EnemyComponent.class, PlayerControlledComponent.class));
        this.game = game;
    }

    @Override
    protected void processSystem() {

    }

    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }


    @Override
    public void inserted(Entity e) {
        if(enemyMapper.has(e)) enemyBag.add(e);
        if(pcMapper.has(e)) playerBag.add(e);
    }

    @Override
    public void removed(Entity e) {
        if(enemyMapper.has(e)) enemyBag.remove(e);
        if(pcMapper.has(e)) playerBag.remove(e);
        
        if(playerBag.isEmpty()){
            ((PlayScreen) game.getScreen()).defeat();
        }

        if(enemyBag.isEmpty()){
            ((PlayScreen) game.getScreen()).victory();
        }


    }


    public void endBattle(){

    }


}
