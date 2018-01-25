package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.factories.spells.animations.SkillAnimation;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.HighlightedText;

/**
 * Created by BB on 18/11/2017.
 */

public class Skill {

    public enum Targeting {Ally, Enemy, Self}

    public enum Attack {Melee, Ranged, Transformative}

    public enum Animatic {Projectile, Slash}

    public enum ActionType {MoveAndAction, Action, Movement, Free}


    private String name = "N/A";
    private String icon;
    private Targeting targeting = Targeting.Enemy;
    private Attack attack = Attack.Ranged;
    private ActionType actionType = ActionType.MoveAndAction;


    public Skill(Builder b) {
        this.name = b.name;
        this.icon = b.icon;
        this.targeting = b.targeting;
        this.attack = b.attack;
    }


    protected SkillAnimation skillAnimation;

    public Array<Entity> createTargeting(World world, Entity player) {

        Array<Entity> entityArray = new Array<Entity>();

        int range = attack == Attack.Melee ? 1 : player.getComponent(StatComponent.class).attackRange;

        switch (targeting) {
            case Enemy:
                entityArray = new TargetingFactory().createTargetTiles(world, player, this, range);
                break;
            case Ally:
                entityArray = new TargetingFactory().createAllyTargetTiles(world, player, this, range);
                break;
        }

        return entityArray;
    }

    ;

    public boolean canCast(World world, Entity entity) {
        return false;
    }

    public void cast(World world, Entity entity, Coordinates target) {
        TurnComponent turnComponent = entity.getComponent(TurnComponent.class);
        switch (actionType) {
            case Movement:
                turnComponent.movementActionAvailable = false;
                break;
            case Action:
                turnComponent.movementActionAvailable = false;
                break;
            case MoveAndAction:
                turnComponent.movementActionAvailable = false;
                turnComponent.attackActionAvailable = false;
                break;
        }
    }

    ;

    public void endTurnUpdate() {

    }

    ;

    public String getIcon() {
        return icon;
    }


    public String getName() {
        return name;
    }

    public String getDescription(World world, Entity entity) {
        return "ERROR: DESCRIPTION NOT SET";
    }

    public HighlightedText getHighlight(World world, Entity entity) {
        return null;
    }


    public static class Builder {

        private String name = "N/A";
        private String icon = TextureStrings.BLOCK;
        private Targeting targeting = Targeting.Enemy;
        private Attack attack = Attack.Ranged;

        public Builder name(String val) {
            this.name = val;
            return this;
        }

        public Builder icon(String val) {
            this.icon = val;
            return this;
        }

        public Builder targeting(Targeting val) {
            this.targeting = val;
            return this;
        }

        public Builder attack(Attack val) {
            this.attack = val;
            return this;
        }

        public Skill build() {
            return new Skill(this);
        }

    }


    //public


}





