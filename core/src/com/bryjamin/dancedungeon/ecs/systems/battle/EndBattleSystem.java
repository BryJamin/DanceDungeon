package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.ui.TutorialSystem;
import com.bryjamin.dancedungeon.factories.map.event.TutorialEvent;
import com.bryjamin.dancedungeon.factories.unit.UnitData;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.BattleScreenInputSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;


/**
 * Created by BB on 28/11/2017.
 *
 * System used to track when a battle has ended.
 */

public class EndBattleSystem extends EntitySystem implements Observer {

    private PlayerPartyManagementSystem playerPartyManagementSystem;
    private BattleScreenUISystem battleScreenUISystem;

    private BattleScreenInputSystem battleScreenInputSystem;
    private TurnSystem turnSystem;
    private ActionQueueSystem actionQueueSystem;

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<UnitComponent> uMapper;
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
        actionQueueSystem.observable.addObserver(this);


        Array<AbstractObjective> abstractObjectives = new Array<AbstractObjective>();
        abstractObjectives.add(currentEvent.getPrimaryObjective());
        abstractObjectives.addAll(currentEvent.getBonusObjectives());


        for(int i = 0; i < abstractObjectives.size; i++) {
            for(int j = 0; j < abstractObjectives.get(i).getUpdateOnArray().length; j++){
                switch (abstractObjectives.get(i).getUpdateOnArray()[j]){
                    case END_TURN:
                        turnSystem.addPlayerTurnObserver(abstractObjectives.get(i));
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

        if(!processingFlag) return;


        if(playerPartyManagementSystem.getPartyDetails().getMorale() == 0 || playerPartyManagementSystem.getPartyDetails().isEveryoneDefeated()) {
            battleScreenUISystem.createDefeatScreen();
            turnSystem.setEnabled(false);
            battleScreenInputSystem.restrictInputToStage();

            actionQueueSystem.observable.removeObserver(this);
        }

        if (currentEvent.isComplete(world)) {

            for(Entity e : playerBag){
                HealthComponent hc = e.getComponent(HealthComponent.class);
                UnitData unitData = uMapper.get(e).getUnitData();

                unitData.setHealth(hc.health);
                unitData.setMaxHealth(hc.maxHealth);
            }

            if(TutorialSystem.isTutorial){
                battleScreenUISystem.createTutorialWindow(new Rectangle(), TutorialSystem.TutorialState.END);
            } else {
                actionQueueSystem.observable.removeObserver(this);
                battleScreenUISystem.createVictoryRewards(currentEvent, partyDetails);

                turnSystem.stop();
                battleScreenInputSystem.restrictInputToStage();
            }

        }

        battleScreenUISystem.updateObjectiveTable(currentEvent);


    }

}
