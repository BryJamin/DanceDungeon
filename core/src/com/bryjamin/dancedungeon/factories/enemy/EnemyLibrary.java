package com.bryjamin.dancedungeon.factories.enemy;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.ai.ActionScoreCalculator;
import com.bryjamin.dancedungeon.ecs.ai.UtilityAiCalculator;
import com.bryjamin.dancedungeon.ecs.ai.actions.BasicAttackAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.EndTurnAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.FindBestMovementAreaToAttackFromAction;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanMoveCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanUseSkillCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.IsNextToCalculator;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

public class EnemyLibrary {


    private static final OrderedMap<String, UnitData> enemies = new OrderedMap<>();

    public static void loadFromJSON(){

        enemies.clear();

        Json json = new Json();
        json.setIgnoreUnknownFields(true);

        Array<UnitData> array = json.fromJson(Array.class, Gdx.files.internal("json/enemies/enemies.json"));

        for(UnitData unitData : array){
            if(enemies.containsKey(unitData.getId())) {
                throw new RuntimeException("Duplicate key found in Enemy Library: " + unitData.getId());
            }

            enemies.put(unitData.getId(), unitData);
        }


    }



    public static UnitData getUnitData(String id){

        if(enemies.containsKey(id)){
            return new UnitData(enemies.get(id));
        } else {
            throw new IllegalArgumentException("Enemy ID:" + id + " does not exist");
        }
    }



    public static Entity getUnit(World world, String id){

        float width = Measure.units(5f);
        float height = Measure.units(5f);

        UnitFactory unitFactory = new UnitFactory();

        UnitData unitData = getUnitData(id);

        Entity unit = unitFactory.baseUnitBag(world, unitData);
        unit.edit().add(new EnemyComponent());

        unit.edit().add(new MoveToComponent(Measure.units(80f)));

        unit.edit().add(new CenteringBoundComponent(width, height));

        unit.edit().add(new HitBoxComponent(new HitBox(width, height)));

        unit.edit().add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE,
                new TextureDescription.Builder(unitData.icon)
                .height(height)
                .width(width)
                .build()));

        unit.edit().add(new UtilityAiComponent(new UtilityAiCalculator(
                new ActionScoreCalculator(new EndTurnAction()),
                new ActionScoreCalculator(new FindBestMovementAreaToAttackFromAction(), new IsNextToCalculator(null, 100f),
                        new CanMoveCalculator(100f, null)),
                new ActionScoreCalculator(new BasicAttackAction(), new IsNextToCalculator(150f, null), new CanUseSkillCalculator(unitData.getSkills().first(), 100f, null)
                ))));

        int STANDING_ANIMATION = 23;

        unit.edit().add(new AnimationStateComponent(STANDING_ANIMATION));
        unit.edit().add(new AnimationMapComponent()
                .put(STANDING_ANIMATION, unitData.icon, 0.6f, Animation.PlayMode.LOOP));

        return unit;






    }


    public static void empty(){};


    public static int getLibrarySize(){
        return enemies.size;
    }


    public static final String MELEE_BLOB = "cf5db9a9-b053-4de8-ad17-4f56a1e008f6";
    public static final String RANGED_BLASTER = "7720994b-263a-439d-b83c-70586bb63777";



}
