package com.bryjamin.dancedungeon.screens.strategy.worlds;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
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
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.player.Unit;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.factories.spells.FireballSkill;
import com.bryjamin.dancedungeon.factories.spells.basic.MageAttack;
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.screens.battle.BattleDetails;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 17/12/2017.
 */

public class MapWorld extends WorldContainer {


    private Array<Unit> playerParty = new Array<Unit>();

    private Array<Array<String>> enemyParty = new Array<Array<String>>();

    //BattleScreen battleScreen;
    private VictoryAdapter adapter;



    public MapWorld(MainGame game, Viewport gameport) {
        super(game, gameport);
        this.adapter = new VictoryAdapter();


        Unit warrior = new Unit(UnitMap.UNIT_WARRIOR);
        warrior.setStatComponent(new StatComponent.StatBuilder()
                .movementRange(3)
                .power(5)
                .maxHealth(10).build());


        Unit warrior2 = new Unit(UnitMap.UNIT_WARRIOR);
        warrior2.setStatComponent(new StatComponent.StatBuilder()
                .power(5)
                .movementRange(5)
                .maxHealth(10).build());

        Unit mage = new Unit(UnitMap.UNIT_MAGE);
        mage.setStatComponent(new StatComponent.StatBuilder()
                .movementRange(3)
                .maxHealth(20)
                .attackRange(3)
                .magic(6)
                .power(5).build());

        SkillsComponent skillsComponent = new SkillsComponent();
        skillsComponent.basicAttack = new MageAttack();
        skillsComponent.skillDescriptions.add(new FireballSkill());
        mage.setSkillsComponent(skillsComponent);

        playerParty.add(mage);
        playerParty.add(warrior);
        playerParty.add(warrior2);

        Array<String> fight1 = new Array<String>();
        fight1.add(EnemyFactory.BLOB);
        fight1.add(EnemyFactory.MAGE_BLOB);


        Array<String> fight2 = new Array<String>();
        fight2.add(EnemyFactory.BLOB);
        fight2.add(EnemyFactory.FAST_BLOB);
        fight2.add(EnemyFactory.BLOB);
        fight2.add(EnemyFactory.MAGE_BLOB);

        Array<String> fight3 = new Array<String>();
        fight3.add(EnemyFactory.FAST_BLOB);
        fight3.add(EnemyFactory.MAGE_BLOB);
        fight3.add(EnemyFactory.MAGE_BLOB);
        fight3.add(EnemyFactory.MAGE_BLOB);

        enemyParty.add(fight1);
        enemyParty.add(fight2);
        enemyParty.add(fight3);

        createWorld();


    }








    private void createWorld(){

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,
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
                        new ActionOnTapSystem(gameport),
                        new FadeSystem(),
                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch))
                .build();

        world = new World(config);

        float width = Measure.units(15f);
        float height = Measure.units(7.5f);

        createMap();

        Entity startButton = world.createEntity();
        startButton.edit().add(new PositionComponent(CenterMath.offsetX(gameport.getWorldWidth(), width), CenterMath.offsetY(gameport.getWorldHeight(), height) - Measure.units(5f)));
        startButton.edit().add(new HitBoxComponent(new HitBox(width, height)));
        startButton.edit().add(new CenteringBoundaryComponent(new Rectangle(0,0, width, height)));
        startButton.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .width(width)
                        .height(height).build(),
                new TextDescription.Builder(Fonts.MEDIUM)
                        .text(TextResource.GAME_TITLE_START)
                        .color(new Color(Color.BLACK))
                        .build()));
        startButton.edit().add(new ActionOnTapComponent(new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                //game.getScreen().dispose();

                BattleDetails battleDetails = new BattleDetails();
                battleDetails.setPlayerParty(playerParty);


                enemyParty.shuffle();

                EnemyFactory enemyFactory = new EnemyFactory();

                for(String s : enemyParty.first()){
                    battleDetails.getEnemyParty().add(enemyFactory.get(s));
                }

                game.setScreen(new BattleScreen(game, game.getScreen(), battleDetails));
            }
        }));



    }



    public void createMap(){

        Coordinates[] coordinates = {
                new Coordinates(0,1),
                new Coordinates(1,1),
                new Coordinates(2,1),
                new Coordinates(3,1),
                new Coordinates(4,1),
                new Coordinates(5,1),
                new Coordinates(1,0),
                new Coordinates(2,0),
                new Coordinates(3,0),
                new Coordinates(4,0),
                new Coordinates(1,2),
                new Coordinates(2,2),
                new Coordinates(3,2),
                new Coordinates(4,2),
        };

        float x = Measure.units(5f);
        float y = Measure.units(10f);

        float width = Measure.units(5f);
        float height = Measure.units(5f);
        float gap = Measure.units(10f);


        for(Coordinates c : coordinates){


            Entity e = world.createEntity();
            e.edit().add(new PositionComponent(x + (x * c.getX()) + (gap * c.getX()),
                    y + (y * (c.getY()) + gap * c.getY())));
            e.edit().add(new HitBoxComponent(new HitBox(width, height)));
            e.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.BLOCK)
                    .width(width)
                    .height(height)
                    .build()));

        }
/*
        for(int i = 0; i < coordinates.length; i++){

            Entity e = world.createEntity();
            e.edit().add(new PositionComponent(x + (x * coordinates[i].getX()) + (gap * coordinates[i].getX()),
                    y + (y * (coordinates[i].getY()) + gap * coordinates[i].getY())));
            e.edit().add(new HitBoxComponent(new HitBox(width, height)));
            e.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.BLOCK)
                    .width(width)
                    .height(height)
                    .build()));

        }*/

    }




    @Override
    public void handleInput(InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(adapter);
    }

    private class VictoryAdapter extends InputAdapter {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 input = gameport.unproject(new Vector3(screenX, screenY, 0));
            if(world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)){
                return  true;
            };
            return false;
        }
    }


    public class StrategyMap {





    }


}
