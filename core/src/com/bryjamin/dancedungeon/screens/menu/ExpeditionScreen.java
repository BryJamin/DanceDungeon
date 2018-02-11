package com.bryjamin.dancedungeon.screens.menu;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.input.BasicInputSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.ExpeditionScreenCreationSystem;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.factories.spells.basic.DodgeUp;
import com.bryjamin.dancedungeon.factories.spells.basic.FireWeapon;
import com.bryjamin.dancedungeon.factories.spells.basic.StunStrike;
import com.bryjamin.dancedungeon.factories.spells.restorative.Heal;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.utils.BaseStatStatics;
import com.bryjamin.dancedungeon.utils.GameDelta;

/**
 * Created by BB on 10/02/2018.
 */

public class ExpeditionScreen extends AbstractScreen {

    private World world;

    public ExpeditionScreen(MainGame game) {
        super(game);
        createWorld();
    }


    private UnitData createWarrior(){
        UnitData warrior = new UnitData(UnitMap.UNIT_WARRIOR);
        warrior.setStatComponent(new StatComponent.StatBuilder()
                .movementRange(BaseStatStatics.BASE_MOVEMENT)
                .attackRange(3)
                .attack(5)
                .healthAndMax(15).build());

        warrior.setSkillsComponent(new SkillsComponent(
                new FireWeapon()));

        return warrior;
    }

    private UnitData createMage(){
        UnitData mage = new UnitData(UnitMap.UNIT_MAGE);
        mage.setStatComponent(
                new StatComponent.StatBuilder()
                        .movementRange(BaseStatStatics.BASE_MOVEMENT )
                        .healthAndMax(20)
                        .attackRange(6)
                        .attack(7).build());

        mage.setSkillsComponent(
                new SkillsComponent(
                        new FireWeapon(),
                        new DodgeUp(),
                        new Heal(),
                        new StunStrike()
                ));

        return mage;
    }

    private void createWorld() {


        Array<UnitData> availiable = new Array<UnitData>();
        availiable.addAll(createMage(), createWarrior(), createMage(), createMage(), createWarrior(), createWarrior(), createWarrior());


        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        new ExpeditionScreenCreationSystem(game, gameport, availiable, new Array<UnitData>()),
                        new BasicInputSystem(gameport),

                        new MovementSystem(),
                        new UpdatePositionSystem(),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ConditionalActionSystem(),
                        new ParentChildSystem(),
                        new ExpireSystem(),
                        new DeathSystem()
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new ActionOnTapSystem(),
                        new FadeSystem(),
                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch))
                .build();

        world = new World(config);

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GameDelta.delta(world, delta);
    }



}