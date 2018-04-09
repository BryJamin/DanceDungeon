package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.FollowPositionComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.ScaleTransformationComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SelectedEntityComponent;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 18/11/2017.
 * <p>
 * System that tracks which entity is currently selected by the blob.
 * <p>
 * It changes the buttons and text displayed on the screen based on the entity selected
 */

public class SelectedTargetSystem extends EntityProcessingSystem {

    private TileSystem tileSystem;
    private ComponentMapper<PlayerControlledComponent> playerControlledM;
    private ComponentMapper<TurnComponent> turnMapper;
    private boolean processingFlag = false;

    public SelectedTargetSystem() {
        super(Aspect.all(SelectedEntityComponent.class));
    }

    @Override
    protected void process(Entity e) {
        if (!e.getComponent(TurnComponent.class).hasActions()) {
            world.getSystem(BattleScreenUISystem.class).reset();
            e.edit().remove(SelectedEntityComponent.class);
        }
    }


    @Override
    public void inserted(Entity e) {

        if (this.getEntities().size() >= 1) {
            for (Entity entity : this.getEntities()) {
                entity.edit().remove(SelectedEntityComponent.class);
            }
            world.getSystem(BattleScreenUISystem.class).reset();
        }

        setUpCharacter(e);
    }

    @Override
    public void removed(Entity e) {
        if (this.getEntities().size() <= 0) {
            //world.getSystem(BattleScreenUISystem.class).reset();
        }
    }


    @Override
    protected boolean checkProcessing() {
        return this.getEntities().size() > 0;
    }



    /**
     * Checks the given x and y input to see if a playerable character has been selected.
     * If this is the case the system sets up the character's spells
     *
     * @param x - x input
     * @param y - y input
     * @return - True if a playerable character has been selected
     */
    public boolean selectCharacter(float x, float y) {

        Coordinates c = world.getSystem(TileSystem.class).getCoordinatesUsingPosition(x, y);



        if (world.getSystem(TileSystem.class).getOccupiedMap().containsValue(c, false)) {

            Entity selected = world.getSystem(TileSystem.class).getOccupiedMap().findKey(c, false);

            if(turnMapper.has(selected)) {

                if (selected.getComponent(TurnComponent.class).hasActions()) {//TODO you can't select a character if it has no actions left
                    world.getSystem(TileSystem.class).getOccupiedMap().findKey(c, false).edit().add(new SelectedEntityComponent());
                }
                return true;
            }
        }

        return false;
    }


    /**
     * Uses the given entity and sets up it's skills
     *
     * @param playableCharacter - Selected playable character
     */
    private void setUpCharacter(final Entity playableCharacter) {

        //Can't select a character with no actions

        //This only exists for players
        //if(playerControlledM.has(selectedEntity)) {
        if (playerControlledM.has(playableCharacter)) {
            if (!playableCharacter.getComponent(TurnComponent.class).hasActions()) return;
        }

        createTargetReticle(world, playableCharacter);
        //createUnitInformationEntity(world, playableCharacter);

        if (playerControlledM.has(playableCharacter)) {
            createMovementAndAttackTiles(playableCharacter);
            world.getSystem(BattleScreenUISystem.class).createCharacterSkillHUD(playableCharacter);
        }
    }


    /**
     * Upon being selected creates UsesMoveAction and Attacking tiles for the player, based on
     * the avaliablity of an entites attack and movement actions
     *
     * @param e
     */
    private void createMovementAndAttackTiles(Entity e) {

        TurnComponent turnComponent = e.getComponent(TurnComponent.class);

        if (turnComponent.attackActionAvailable && turnComponent.movementActionAvailable) {
            new TargetingFactory().createMovementTiles(world, e, e.getComponent(StatComponent.class).movementRange);
        } else if (turnComponent.attackActionAvailable) {
            /*new TargetingFactory().createTargetTiles(world, e,
                    e.getComponent(SkillsComponent.class).basicAttack,
                    e.getComponent(StatComponent.class).attackRange);*/
        }


    }

    private void createTargetReticle(World world, Entity entity) {

        float width = entity.getComponent(CenteringBoundaryComponent.class).bound.width * 2.5f;
        float height = entity.getComponent(CenteringBoundaryComponent.class).bound.height * 2.5f;

        Entity recticle = world.createEntity();
        recticle.edit().add(new PositionComponent())
                .add(new ScaleTransformationComponent(1.1f))
                .add(new UITargetingComponent())
                .add(new FollowPositionComponent(entity.getComponent(PositionComponent.class).position,
                        CenterMath.offsetX(entity.getComponent(CenteringBoundaryComponent.class).bound.width, width),
                        CenterMath.offsetY(entity.getComponent(CenteringBoundaryComponent.class).bound.height, height)))
                .add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.TARGETING)
                        .width(width)
                        .height(height)
                        .color(playerControlledM.has(entity) ? new Color(Color.WHITE) : new Color(Color.RED))
                        .build()));

    }


}
