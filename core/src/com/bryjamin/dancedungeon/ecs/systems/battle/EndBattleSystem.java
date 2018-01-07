package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.systems.input.GameMap;
import com.bryjamin.dancedungeon.ecs.systems.input.MapEvent;
import com.bryjamin.dancedungeon.factories.player.Unit;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 28/11/2017.
 */

public class EndBattleSystem extends EntitySystem {

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerControlledComponent> pcMapper;


    private Bag<Entity> playerBag = new Bag<Entity>();
    private Bag<Entity> enemyBag = new Bag<Entity>();

    private MainGame game;

    private PartyDetails partyDetails;
    private GameMap gameMap;

    private boolean processingFlag = true;

    private MapEvent currentEvent;

    public Bag<Entity> getPlayerBag() {
        return playerBag;
    }

    public EndBattleSystem(MainGame game, GameMap gameMap, PartyDetails partyDetails) {
        super(Aspect.one(EnemyComponent.class, PlayerControlledComponent.class));
        this.partyDetails = partyDetails;
        this.game = game;
        this.gameMap = gameMap;
        //partyDetails.getPlayerParty().
        this.currentEvent = gameMap.getNextEvent();


    }

    @Override
    protected void initialize() {

        UnitMap unitMap = new UnitMap();

        for (int i = 0; i < partyDetails.getPlayerParty().size; i++) {

            if (partyDetails.getPlayerParty().get(i) != null) {
                Unit unit = partyDetails.getPlayerParty().get(i);
                ComponentBag player = unitMap.getUnit(unit);
                BagToEntity.bagToEntity(world.createEntity(), player);
            }

        }

       // setupEvent(currentEvent);
    }


    @Override
    protected void processSystem() {

        if (playerBag.isEmpty()) {
            ((BattleScreen) game.getScreen()).defeat();
        }

        if(gameMap.getMapEvents().size <= 0 && currentEvent.isComplete(world)) {
            ((BattleScreen) game.getScreen()).victory();
        }

        if (currentEvent.isComplete(world)) {
            next();
        }
        ;

    }



    /**
     * Should be called when the next event is ready to be called
     */
    public void next() {
        this.currentEvent =  gameMap.getNextEvent();
        setupEvent(this.currentEvent);
    }

    ;

    private void setupEvent(MapEvent mapEvent) {
        mapEvent.setUpEvent(world);
    }


    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }


    @Override
    public void inserted(Entity e) {
        if (enemyMapper.has(e)) enemyBag.add(e);
        if (pcMapper.has(e)) playerBag.add(e);
    }

    @Override
    public void removed(Entity e) {
        if (enemyMapper.has(e)) enemyBag.remove(e);
        if (pcMapper.has(e)) playerBag.remove(e);
    }


    public void endBattle() {

    }



}
