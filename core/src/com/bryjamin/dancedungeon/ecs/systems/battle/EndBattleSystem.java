package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.Observer;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.event.MapEvent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 28/11/2017.
 */

public class EndBattleSystem extends EntitySystem implements Observer {

    private PlayerPartyManagementSystem playerPartyManagementSystem;
    private ActionCameraSystem actionCameraSystem;

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerControlledComponent> pcMapper;


    private Bag<Entity> playerBag = new Bag<Entity>();
    private Bag<Entity> enemyBag = new Bag<Entity>();

    private MainGame game;

    private PartyDetails partyDetails;
    private GameMap gameMap;

    private boolean processingFlag = true;

    private MapEvent currentEvent;

    @Override
    public void onNotify() {
        //TODO, decide if notify is appropriate. Also, there is not really a reason for the state change,

        //UNTIL I CAN FIX TEH ACTION CAMERA SYSTEM IN REGARDS TO THE SIMULTANEOUS ATTACKS HAPPENS
        //ON DIFFERENT PARTS OF THE MAP I NEED TO ADD A CHECK FOR IF THE FLAG IS FALSE
        //OTHERWISE THIS WILL CRASH

        if(!processingFlag) return;


        if(playerPartyManagementSystem.getPartyDetails().morale == 0) {
            ((BattleScreen) game.getScreen()).defeat();

            actionCameraSystem.observerArray.removeValue(this, true);
        }

        if (currentEvent.isComplete(world)) {
            state = State.CLEAN_UP;
            currentEvent.cleanUpEvent(world);

            for(Entity e : playerBag){
                HealthComponent hc = e.getComponent(HealthComponent.class);
                StatComponent statComponent = e.getComponent(StatComponent.class);

                statComponent.health = (int) hc.health;
                statComponent.maxHealth = (int) hc.maxHealth;
            }

            actionCameraSystem.observerArray.removeValue(this, true);

            ((BattleScreen) game.getScreen()).victory(partyDetails);

        }

    }

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
        actionCameraSystem.observerArray.add(this);
    }

    @Override
    protected void processSystem() {

    }

    @Override
    protected boolean checkProcessing() {
        return false;
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
