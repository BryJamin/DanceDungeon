package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.systems.graphical.PlayerGraphicalTargetingSystem;
import com.bryjamin.dancedungeon.factories.player.spells.SpellFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 18/11/2017.
 */

public class SelectedTargetSystem extends BaseSystem {


    private Entity selectedEntity;


    @Override
    protected void processSystem() {

    }




    public boolean selectCharacter(float x, float y){

        Coordinates c = world.getSystem(TileSystem.class).getCoordinatesUsingPosition(x, y);

        if(world.getSystem(TileSystem.class).getPlayerControlledMap().containsKey(c)){
            world.getSystem(PlayerGraphicalTargetingSystem.class).createTarget(x, y);

            final Entity e = world.getSystem(TileSystem.class).getPlayerControlledMap().get(c);

            final SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);

            for(int i = 0; i < skillsComponent.skillDescriptions.size; i++){

                final int j = i;

                BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().defaultButton(Measure.units(25f) * (i + 1), 0, new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {
                        skillsComponent.skillDescriptions.get(j).createTargeting(world, e);
                    }
                }));
            }

            return true;
        };

        return false;
    }




    public boolean isCharacterSelected(){
        return selectedEntity != null;
    }


    public void createHighlight(){

    }





}
