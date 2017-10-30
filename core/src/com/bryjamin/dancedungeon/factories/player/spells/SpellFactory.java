package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ParentComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.PlayerGraphicalTargetingSystem;
import com.bryjamin.dancedungeon.factories.AbstractFactory;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 28/10/2017.
 */

public class SpellFactory extends AbstractFactory {

    private static final float SIZE = Measure.units(10f);

    public SpellFactory(AssetManager assetManager) {
        super(assetManager);
    }

    public ComponentBag endTurnButton(float x, float y){

        return defaultButton(x, y, new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {

                world.getSystem(TurnSystem.class).setUp(TurnSystem.TURN.ENEMY);
            }
        });

    }


    public ComponentBag moveToButton(float x, float y){

        ComponentBag bag = defaultButton(x, y, new WorldAction() {

            @Override
            public void performAction(World world, Entity entity) {

                System.out.println(entity.getComponent(ParentComponent.class).children.size);

                if(entity.getComponent(ParentComponent.class).children.size > 0){
                    world.getSystem(DeathSystem.class).killChildComponents(entity.getComponent(ParentComponent.class));
                } else {
                    world.getSystem(PlayerGraphicalTargetingSystem.class).createMovementTiles(entity, 3);
                }
            }
        });
        bag.add(new ParentComponent());

        return bag;

    }


    public ComponentBag defaultButton(float x, float y, WorldAction action){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, SIZE, SIZE))));
        bag.add(new ActionOnTapComponent(action));

        return bag;

    }












}
