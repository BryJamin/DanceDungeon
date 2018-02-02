package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.event.MapEvent;
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

    private enum State {
        CLEAN_UP, START_UP, DURING, END
    }

    private State state = State.START_UP;

    public Bag<Entity> getPlayerBag() {
        return playerBag;
    }

    public EndBattleSystem(MainGame game, GameMap gameMap, PartyDetails partyDetails) {
        super(Aspect.one(EnemyComponent.class, PlayerControlledComponent.class));
        this.partyDetails = partyDetails;
        this.game = game;
        this.gameMap = gameMap;
        //partyDetails.getPlayerParty().
        this.currentEvent = gameMap.getCurrentMapNode().getMapEvent();

    }

    @Override
    protected void initialize() {

        UnitMap unitMap = new UnitMap();

        for (int i = 0; i < partyDetails.getParty().length; i++) {

            if (partyDetails.getParty()[i] != null) {
                Unit unit = partyDetails.getParty()[i];
                ComponentBag player = unitMap.getUnit(unit);
                Entity e = BagToEntity.bagToEntity(world.createEntity(), player);
            }

        }

    }


    @Override
    protected void processSystem() {

        if (playerBag.isEmpty()) {
            ((BattleScreen) game.getScreen()).defeat();
        }

        switch (state){
            case START_UP:
                currentEvent.setUpEvent(world);
                state = State.DURING;
                break;

            case DURING:
                if (currentEvent.isComplete(world)) {
                    state = State.CLEAN_UP;
                    currentEvent.cleanUpEvent(world);
                }
                break;

            case CLEAN_UP:

                if(currentEvent.cleanUpComplete(world)){

                    for(Entity e : playerBag){
                        HealthComponent hc = e.getComponent(HealthComponent.class);
                        StatComponent statComponent = e.getComponent(StatComponent.class);

                        statComponent.health = (int) hc.health;
                        statComponent.maxHealth = (int) hc.maxHealth;
                    }


                    ((BattleScreen) game.getScreen()).victory();

                    state = State.END;
                }

                break;
        }

    }


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
