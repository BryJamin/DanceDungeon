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
 *
 * System that tracks which entity is currently selected by the blob.
 *
 * It changes the buttons and text displayed on the screen based on the entity selected
 *
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


    /**
     * Clears the button entities and selected entity from the system
     */
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


    /**
     * Removes the current targeting from being displayed
     */
    public void clearTargeting(){

        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(UITargetingComponent.class)).getEntities();

        for(int i = 0; i < bag.size(); i++){
            world.getEntity(bag.get(i)).deleteFromWorld();
        }

    }


    /**
     * Checks the given x and y input to see if a playerable character has been selected.
     * If this is the case the system sets up the character's spells
     * @param x - x input
     * @param y - y input
     * @return - True if a playerable character has been selected
     */
    public boolean selectCharacter(float x, float y){

        Coordinates c = world.getSystem(TileSystem.class).getCoordinatesUsingPosition(x, y);

        if(world.getSystem(TileSystem.class).getPlayerControlledMap().containsKey(c)){
            setUpCharacter(world.getSystem(TileSystem.class).getPlayerControlledMap().get(c));
            return true;
        };

        return false;
    }


    /**
     *  Uses the given entity and sets up it's skills
     *
     * @param playableCharacter - Selected playable character
     */
    private void setUpCharacter(final Entity playableCharacter){

        if(selectedEntity != null){
            selectedEntity.edit().remove(FadeComponent.class);
            selectedEntity.getComponent(DrawableComponent.class).drawables.first().getColor().a = 1;
        }

        this.selectedEntity = playableCharacter;

        //TODO just for now
        playableCharacter.edit().add(new FadeComponent(true, 1.25f, true));
        this.clear();

        final SkillsComponent skillsComponent = playableCharacter.getComponent(SkillsComponent.class);

        for(int i = 0; i < skillsComponent.skillDescriptions.size; i++){
            buttons.add(BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().skillButton(Measure.units(25f) * (i + 1), 0,
                    skillsComponent.skillDescriptions.get(i), playableCharacter)));
        }

    }



    public boolean isCharacterSelected(){
        return selectedEntity != null;
    }






}
