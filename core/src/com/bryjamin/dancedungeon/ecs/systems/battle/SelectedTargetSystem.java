package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.factories.player.spells.SpellFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 18/11/2017.
 */

public class SelectedTargetSystem extends BaseSystem {


    private Entity selectedEntity;
    private Array<Entity> buttons = new Array<Entity>();


    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    @Override
    protected void processSystem() {

    }


    public void clear(){
        for(Entity e : buttons){
            e.deleteFromWorld();
        }
        buttons.clear();

        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(UITargetingComponent.class)).getEntities();

        for(int i = 0; i < bag.size(); i++){
            world.getEntity(bag.get(i)).deleteFromWorld();
        }

    }


    public void clearTargeting(){

        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(UITargetingComponent.class)).getEntities();

        for(int i = 0; i < bag.size(); i++){
            world.getEntity(bag.get(i)).deleteFromWorld();
        }

    }



    public boolean selectCharacter(float x, float y){

        Coordinates c = world.getSystem(TileSystem.class).getCoordinatesUsingPosition(x, y);


        System.out.println(c);

        if(world.getSystem(TileSystem.class).getPlayerControlledMap().containsKey(c)){

            if(selectedEntity != null){
                selectedEntity.edit().remove(FadeComponent.class);
                selectedEntity.getComponent(DrawableComponent.class).drawables.first().getColor().a = 1;
            }

            final Entity e = world.getSystem(TileSystem.class).getPlayerControlledMap().get(c);
            this.selectedEntity = e;

            //TODO just for now
            e.edit().add(new FadeComponent(true, 2.5f, true));


            this.clear();

            final SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);

            for(int i = 0; i < skillsComponent.skillDescriptions.size; i++){

                buttons.add(BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().skillButton(Measure.units(25f) * (i + 1), 0,
                        skillsComponent.skillDescriptions.get(i), e)));
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
