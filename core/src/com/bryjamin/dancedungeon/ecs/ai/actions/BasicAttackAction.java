package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.EnemyIntentSystem;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 14/11/2017.
 */

public class BasicAttackAction implements WorldAction {

    @Override
    public void performAction(World world, Entity entity) {

        Array<Entity> entityArray = entity.getComponent(TargetComponent.class).getTargets(world);
        if (entityArray.size <= 0) return;
        entityArray.sort(CoordinateSorter.SORT_BY_NEAREST(entity));

        Skill skill = entity.getComponent(SkillsComponent.class).skills.first();
        Array<Coordinates> targetCoordinatesArray = new Array<Coordinates>();

        for (Entity e : entityArray) {
            targetCoordinatesArray.add(new Coordinates(e.getComponent(CoordinateComponent.class).coordinates));
        }
        ;


        for (Coordinates c : skill.getAffectedCoordinates(world, entity.getComponent(CoordinateComponent.class).coordinates)) {
            if (targetCoordinatesArray.contains(c, false)) {

                entity.edit().add(new StoredSkillComponent(entity.getComponent(CoordinateComponent.class).coordinates,
                        c, skill));

                //skill.cast(world, entity, c);
                break;
            }
        }

        world.getSystem(EnemyIntentSystem.class).updateIntent();

        entity.getComponent(TurnComponent.class).attackActionAvailable = false;
        entity.getComponent(TurnComponent.class).movementActionAvailable = false;


    }


    private class CoordinateScore {

        Coordinates coordinates;
        float score;

        public CoordinateScore(Coordinates coordinates, float score) {
            this.coordinates = coordinates;
            this.score = score;
        }


    }

}
