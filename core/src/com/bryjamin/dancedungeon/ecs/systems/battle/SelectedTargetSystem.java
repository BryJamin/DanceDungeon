package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.battle.AvailableActionsCompnent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SelectedEntityComponent;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 18/11/2017.
 * <p>
 * System that tracks which entity is currently selected by the blob.
 * <p>
 * It changes the buttons and text displayed on the screen based on the entity selected
 */

public class SelectedTargetSystem extends EntityProcessingSystem {

    private BattleScreenUISystem battleScreenUISystem;
    private TileSystem tileSystem;
    private ComponentMapper<PlayerControlledComponent> playerControlledM;
    private ComponentMapper<AvailableActionsCompnent> turnMapper;
    private boolean processingFlag = false;

    public SelectedTargetSystem() {
        super(Aspect.all(SelectedEntityComponent.class));
    }

    @Override
    protected void process(Entity e) {
        if (!e.getComponent(AvailableActionsCompnent.class).hasActions()) {
            world.getSystem(BattleScreenUISystem.class).resetBottomContainer();
            e.edit().remove(SelectedEntityComponent.class);
        }
    }


    @Override
    public void inserted(Entity e) {

        if (this.getEntities().size() >= 1) {
            for (Entity entity : this.getEntities()) {
                entity.edit().remove(SelectedEntityComponent.class);
            }
            world.getSystem(BattleScreenUISystem.class).resetBottomContainer();
        }

        setUpCharacter(e);
    }

    @Override
    public void removed(Entity e) {
        if (this.getEntities().size() <= 0) {
            //world.getSystem(BattleScreenUISystem.class).resetBottomContainer();
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

            Entity selected = world.getSystem(TileSystem.class).getOccupiedMap().getKey(c, false);

            if(turnMapper.has(selected)) {
                world.getSystem(TileSystem.class).getOccupiedMap().getKey(c, false).edit().add(new SelectedEntityComponent());
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
        if (playerControlledM.has(playableCharacter)) {
            if (!playableCharacter.getComponent(AvailableActionsCompnent.class).hasActions()) return;
        }

        battleScreenUISystem.setUpSelectedCharacterHUD(playableCharacter);
    }


    public void setUpSelectedCharacter() {
        if(this.getEntities().size() > 0){
            setUpCharacter(this.getEntities().get(0));
        }
    }


}
