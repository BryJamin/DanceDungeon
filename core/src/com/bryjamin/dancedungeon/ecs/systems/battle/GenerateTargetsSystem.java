package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.FriendlyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;

/**
 * Created by BB on 04/01/2018.
 *
 * System used to set up the Aspect builders for units in the battle System.
 *
 * This is based on whether the unit is an enemy or an ally.
 *
 * //TODO maybe move the target Component to instead just hold, enums of what the characters
 * //TODO can attack and move the methods for generation to a different place?
 *
 */

public class GenerateTargetsSystem extends EntitySystem {

    ComponentMapper<PlayerControlledComponent> playerM;
    ComponentMapper<EnemyComponent> enemyM;
    ComponentMapper<TargetComponent> targetM;


    public GenerateTargetsSystem() {
        super(Aspect.all(TargetComponent.class));
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

        TargetComponent targetComponent = targetM.get(e);

        if(playerM.has(e)){
            targetComponent.allyBuilder = Aspect.all(PlayerControlledComponent.class, CoordinateComponent.class);
            targetComponent.enemyBuilder = Aspect.all(EnemyComponent.class, CoordinateComponent.class);
        } else if(enemyM.has(e)){
            targetComponent.enemyBuilder = Aspect.one(PlayerControlledComponent.class, FriendlyComponent.class).all(CoordinateComponent.class);
            targetComponent.allyBuilder = Aspect.all(EnemyComponent.class, CoordinateComponent.class);
        }

    }





}
