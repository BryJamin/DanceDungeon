package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.FollowPositionComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.factories.spells.SpellFactory;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 18/11/2017.
 *
 * System that tracks which entity is currently selected by the blob.
 *
 * It changes the buttons and text displayed on the screen based on the entity selected
 *
 */

public class SelectedTargetSystem extends EntitySystem {


    private Entity selectedEntity;
    private Entity recticle;
    private Array<Entity> buttons = new Array<Entity>();


    public SelectedTargetSystem() {
        super(Aspect.all(PlayerControlledComponent.class));
    }


    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    @Override
    protected void processSystem() {}


    @Override
    public void removed(Entity e) {

        if(e == selectedEntity){
            selectedEntity = null;
            this.clear();
        }

    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    /**
     * Clears the button entities and selected entity from the system
     */
    public void clear(){
        for(Entity e : buttons){
            //e.edit().add(new DeadComponent());
            e.deleteFromWorld();
        }
        buttons.clear();

        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(UITargetingComponent.class)).getEntities();

        for(int i = 0; i < bag.size(); i++){
            //world.getEntity(bag.get(i)).edit().add(new DeadComponent());
            world.getEntity(bag.get(i)).deleteFromWorld();
        }

    }


    /**
     * Removes the current targeting from being displayed
     */
    public void clearTargeting(){

        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(UITargetingComponent.class)).getEntities();

        for(int i = 0; i < bag.size(); i++){
            world.getEntity(bag.get(i)).edit().add(new DeadComponent());
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


    public void reselectEntityAfterActionComplete(){

        Entity reselect = selectedEntity;
        selectedEntity = null;

        if(reselect != null){

            TurnComponent turnComponent = reselect.getComponent(TurnComponent.class);
            if(turnComponent.movementActionAvailable || turnComponent.attackActionAvailable ||
                    reselect.getComponent(SkillsComponent.class).canCast(world, reselect)) {

                //if (entityArray.size > 0) {
                setUpCharacter(reselect);
                //}

            } else {
                this.clear();
            }

        }



    }




    /**
     *  Uses the given entity and sets up it's skills
     *
     * @param playableCharacter - Selected playable character
     */
    private void setUpCharacter(final Entity playableCharacter){

        //Can't select a character with no actions

        //System.out.println(playableCharacter.getComponent(TurnActionMonitorComponent.class).hasActions());

        if(!playableCharacter.getComponent(TurnComponent.class).hasActions() ||
        world.getSystem(ActionCameraSystem.class).checkProcessing()) return;


        if(selectedEntity != null){

            //Characters can not be reselected
            if(selectedEntity.equals(playableCharacter)) return;
            selectedEntity.getComponent(DrawableComponent.class).drawables.first().getColor().a = 1;
        }

        this.selectedEntity = playableCharacter;


        float width = selectedEntity.getComponent(CenteringBoundaryComponent.class).bound.width * 2.5f;
        float height = selectedEntity.getComponent(CenteringBoundaryComponent.class).bound.height * 2.5f;

        this.clear(); // Clear buttons and recticle before remaking them.

        recticle = world.createEntity();
        recticle.edit().add(new PositionComponent());
        recticle.edit().add(new UITargetingComponent());
        recticle.edit().add(new FollowPositionComponent(selectedEntity.getComponent(PositionComponent.class).position,

                CenterMath.offsetX(selectedEntity.getComponent(CenteringBoundaryComponent.class).bound.width, width),
                CenterMath.offsetY(selectedEntity.getComponent(CenteringBoundaryComponent.class).bound.height, height)
                ));
        recticle.edit().add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.TARGETING)
                .width(width)
                .height(height)
                .build()));


        final SkillsComponent skillsComponent = playableCharacter.getComponent(SkillsComponent.class);

        for(int i = 0; i < skillsComponent.skillDescriptions.size; i++){
            buttons.add(BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().skillButton(Measure.units(25f) * (i + 1), 0,
                    skillsComponent.skillDescriptions.get(i), playableCharacter)));
        }




        if(playableCharacter.getComponent(TurnComponent.class).attackActionAvailable &&
                playableCharacter.getComponent(TurnComponent.class).movementActionAvailable) {
            new TargetingFactory().createMovementTiles(world, playableCharacter, playableCharacter.getComponent(StatComponent.class).movementRange);
        } else if(playableCharacter.getComponent(TurnComponent.class).attackActionAvailable &&
                !playableCharacter.getComponent(TurnComponent.class).movementActionAvailable){
            new TargetingFactory().createTargetTiles(world, playableCharacter,
                    playableCharacter.getComponent(SkillsComponent.class).basicAttack,
                    playableCharacter.getComponent(StatComponent.class).attackRange);

        }
    }



    public boolean isCharacterSelected(){
        return selectedEntity != null;
    }






}
