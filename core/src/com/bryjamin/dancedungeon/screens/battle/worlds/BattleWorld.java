package com.bryjamin.dancedungeon.screens.battle.worlds;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.SkillUISystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.BattleWorldInputHandlerSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BlinkOnHitSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BuffSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BulletSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.EndBattleSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ExplosionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.GenerateTargetsSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.HealthSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.NoMoreActionsSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.PlayerControlledSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.AnimationSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FollowPositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.HealthBarSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.PlayerGraphicalTargetingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.ScaleTransformationSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.spells.SpellFactory;
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;

/**
 * Created by BB on 28/11/2017.
 */

public class BattleWorld extends WorldContainer {

    private PartyDetails partyDetails;
    private GameMap gameMap;

    public BattleWorld(MainGame game, final Viewport gameport, GameMap gameMap, PartyDetails partyDetails) {
        super(game, gameport);
        this.partyDetails = partyDetails;
        this.gameMap = gameMap;
        createWorld();
    }

    public void createWorld(){

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        new BattleWorldInputHandlerSystem(gameport),

                        new MovementSystem(),
                        new FollowPositionSystem(),
                        new UpdatePositionSystem(),

                        new BuffSystem(),

                        //Initialize Tiles
                        new TileSystem(),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ConditionalActionSystem(),
                        new ExplosionSystem(),
                        new BulletSystem(),
                        new TurnSystem(),
                        new HealthSystem(),
                        new ParentChildSystem(),
                        new BlinkOnHitSystem(),
                        new ExpireSystem(),
                        new PlayerControlledSystem(game),
                        new EndBattleSystem(game, gameMap, partyDetails)
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new ActionOnTapSystem(),
                        new SkillUISystem(),
                        new ActionCameraSystem(),

                        //Rendering     Effects
                        new FadeSystem(),
                        new ScaleTransformationSystem(),

                        new NoMoreActionsSystem(),
                        new PlayerGraphicalTargetingSystem(),
                        new BattleMessageSystem(gameport),
                        new AnimationSystem(game),
                        new RenderingSystem(game, gameport),
                        new HealthBarSystem(game, gameport),
                        new BoundsDrawingSystem(batch),
                        new GenerateTargetsSystem(),
                        new SelectedTargetSystem(),
                        new DeathSystem()
                )
                .build();

        world = new World(config);

        BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().endTurnButton(0, 0));
    }

    @Override
    public void handleInput(InputMultiplexer inputMultiplexer) {
        world.getSystem(BattleWorldInputHandlerSystem.class).handleInput(inputMultiplexer);
    }


}

