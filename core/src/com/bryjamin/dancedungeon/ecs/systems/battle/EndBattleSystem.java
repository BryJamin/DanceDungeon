package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.BattleWorldInputHandlerSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;


/**
 * Created by BB on 28/11/2017.
 */

public class EndBattleSystem extends EntitySystem implements Observer {

    private PlayerPartyManagementSystem playerPartyManagementSystem;
    private BattleScreenUISystem battleScreenUISystem;
    private BattleWorldInputHandlerSystem battleWorldInputHandlerSystem;
    private TurnSystem turnSystem;
    private ActionQueueSystem actionQueueSystem;

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerControlledComponent> pcMapper;


    private Bag<Entity> playerBag = new Bag<Entity>();
    private Bag<Entity> enemyBag = new Bag<Entity>();

    private MainGame game;

    private PartyDetails partyDetails;
    private GameMap gameMap;

    private boolean processingFlag = true;

    private BattleEvent currentEvent;


    public EndBattleSystem(MainGame game, BattleEvent battleEvent, PartyDetails partyDetails) {
        super(Aspect.one(EnemyComponent.class, PlayerControlledComponent.class));
        this.partyDetails = partyDetails;
        this.game = game;
        this.gameMap = gameMap;
        //partyDetails.getPlayerParty().
        this.currentEvent = battleEvent;

    }

    @Override
    protected void initialize() {
        actionQueueSystem.observerArray.add(this);


        Array<AbstractObjective> abstractObjectives = new Array<AbstractObjective>();
        abstractObjectives.add(currentEvent.getPrimaryObjective());
        abstractObjectives.addAll(currentEvent.getBonusObjective());


        for(int i = 0; i < abstractObjectives.size; i++) {
            for(int j = 0; j < abstractObjectives.get(i).getUpdateOnArray().length; j++){
                switch (abstractObjectives.get(i).getUpdateOnArray()[j]){
                    case END_TURN:
                        turnSystem.addNextTurnObserver(abstractObjectives.get(i));
                        break;
                }
            }
            abstractObjectives.get(i).addObserver(this);
        }

        battleScreenUISystem.updateObjectiveTable(currentEvent);

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


    public BattleEvent getCurrentEvent() {
        return currentEvent;
    }


    @Override
    public void update(Object o) {
        //TODO, decide if notify is appropriate. Also, there is not really a reason for the state change,

        //UNTIL I CAN FIX TEH ACTION CAMERA SYSTEM IN REGARDS TO THE SIMULTANEOUS ATTACKS HAPPENS
        //ON DIFFERENT PARTS OF THE MAP I NEED TO ADD A CHECK FOR IF THE FLAG IS FALSE
        //OTHERWISE THIS WILL CRASH

        if(!processingFlag) return;


        if(playerPartyManagementSystem.getPartyDetails().morale == 0) {
            ((BattleScreen) game.getScreen()).defeat();

            actionQueueSystem.observerArray.removeValue(this, true);
        }

        if (currentEvent.isComplete(world)) {

            for(Entity e : playerBag){
                HealthComponent hc = e.getComponent(HealthComponent.class);
                StatComponent statComponent = e.getComponent(StatComponent.class);

                statComponent.health = (int) hc.health;
                statComponent.maxHealth = (int) hc.maxHealth;
            }

            actionQueueSystem.observerArray.removeValue(this, true);

            battleScreenUISystem.createVictoryRewards(currentEvent, partyDetails);
            battleWorldInputHandlerSystem.setState(BattleWorldInputHandlerSystem.State.ONLY_STAGE);

        }

        battleScreenUISystem.updateObjectiveTable(currentEvent);


    }

}
