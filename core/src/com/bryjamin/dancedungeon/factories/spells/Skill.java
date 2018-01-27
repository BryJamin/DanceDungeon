package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.WaitActionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.KillOnAnimationEndComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.spells.animations.BasicProjectile;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.HighlightedText;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 18/11/2017.
 */

public class Skill {

    public enum Targeting {Ally, Enemy, Self}

    public enum Attack {Melee, Ranged, Transformative}

    public enum ActionType {MoveAndAction, Action, Movement, Free}

    public enum SpellDamageApplication {Instant, AfterSpellAnimation}

    public enum SpellAnimation {Projectile, Slash, Glitter}

    public enum SpellType {Heal, HealOverTime, MagicAttack, PhysicalAttack, Burn}

    public enum SpellCoolDown {PerTurn, OverTime, Limited}


    private String name = "N/A";
    private String icon;

    private int coolDown = 0;
    private int resetCoolDownNumber = 1;

    private Targeting targeting = Targeting.Enemy;
    private Attack attack = Attack.Ranged;
    private ActionType actionType = ActionType.MoveAndAction;
    private SpellAnimation spellAnimation = SpellAnimation.Projectile;
    private SpellType spellType = SpellType.MagicAttack;
    private SpellDamageApplication spellDamageApplication = SpellDamageApplication.Instant;
    private SpellCoolDown spellCoolDown = SpellCoolDown.PerTurn;


    public Skill(Builder b) {
        this.name = b.name;
        this.icon = b.icon;
        this.targeting = b.targeting;
        this.attack = b.attack;
        this.actionType = b.actionType;
        this.spellAnimation = b.spellAnimation;
        this.spellType = b.spellType;
        this.spellDamageApplication = b.spellDamageApplication;
    }

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

        TurnComponent turnComponent = entity.getComponent(TurnComponent.class);

        switch (actionType) {
            case Movement:
                return turnComponent.movementActionAvailable;
            case Action:
                return turnComponent.attackActionAvailable;
            case MoveAndAction:
                return turnComponent.hasActions();
        }

        return true;
    }

    public void cast(World world, Entity entity, Coordinates target) {
        TurnComponent turnComponent = entity.getComponent(TurnComponent.class);
        setTurnComponentActionBoolean(actionType, turnComponent);


        Rectangle rectangle = world.getSystem(TileSystem.class).createRectangleUsingCoordinates(target);

        switch (spellAnimation) {
            case Glitter:
                final int GLITTER_DRAWABLE_ID = 25;
                final int GLITTER_ANIMATION_ID = 0;
                Entity heal = world.createEntity();
                heal.edit().add(new PositionComponent(rectangle.x, rectangle.y))
                        .add(new DrawableComponent(Layer.FOREGROUND_LAYER_FAR, new TextureDescription.Builder(TextureStrings.SKILLS_HEAL)
                                .identifier(GLITTER_DRAWABLE_ID)
                                .color(Color.GREEN)
                                .width(rectangle.getWidth())
                                .height(rectangle.getHeight())
                                .build()))
                        .add(new AnimationStateComponent().put(GLITTER_DRAWABLE_ID, GLITTER_ANIMATION_ID))
                        .add(new AnimationMapComponent().put(GLITTER_ANIMATION_ID, TextureStrings.SKILLS_HEAL, 0.2f, Animation.PlayMode.NORMAL))
                        .add(new KillOnAnimationEndComponent(GLITTER_ANIMATION_ID))
                        .add(new WaitActionComponent());
                break;

            case Slash:

                final int SLASH_DRAWABLE_ID = 25;
                final int SLASH_ANIMATION = 0;

                Entity slash = world.createEntity();
                slash.edit().add(new PositionComponent(rectangle.x, rectangle.y))
                        .add(new DrawableComponent(Layer.FOREGROUND_LAYER_FAR, new TextureDescription.Builder(TextureStrings.SKILLS_SLASH)
                                .identifier(SLASH_DRAWABLE_ID)
                                .width(rectangle.getWidth())
                                //.color(new Color(Colors.AMOEBA_FAST_PURPLE))
                                .height(rectangle.getHeight())
                                //.scaleX(-1)
                                .build()))
                        .add(new AnimationStateComponent().put(SLASH_DRAWABLE_ID, SLASH_ANIMATION))
                        .add(new AnimationMapComponent().put(SLASH_ANIMATION, TextureStrings.SKILLS_SLASH, 0.3f, Animation.PlayMode.NORMAL))
                        .add(new KillOnAnimationEndComponent(SLASH_ANIMATION))
                        .add(new WaitActionComponent());

                break;

            case Projectile:

                float width = Measure.units(5f);
                float height = Measure.units(5f);

                new BasicProjectile.BasicProjectileBuilder()
                        .drawableComponent(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                                new TextureDescription.Builder(TextureStrings.BLOCK)
                                        .color(new Color(Colors.BOMB_ORANGE))
                                        .width(width)
                                        .height(height)
                                        .build()))
                        .width(width)
                        .height(height)
                        .speed(Measure.units(85f))
                        .damage(entity.getComponent(StatComponent.class).magic)
                        .build()
                        .cast(world, entity, target);

                break;
        }


        switch (spellDamageApplication) {

            case Instant:


                for (Entity e : world.getSystem(TileSystem.class).getCoordinateMap().get(target)) {
                    if (world.getMapper(HealthComponent.class).has(e)) {

                        StatComponent sc = entity.getComponent(StatComponent.class);

                        switch (spellType) {

                            case MagicAttack:
                            case PhysicalAttack:

                                e.getComponent(HealthComponent.class).applyDamage(
                                        spellType == SpellType.PhysicalAttack ? sc.power : sc.magic);

                                break;

                            case Heal:
                                e.getComponent(HealthComponent.class).applyHealing(sc.magic);
                                break;
                        }
                    }
                }
                ;


                break;


        }


    }

    ;

    public void endTurnUpdate() {

    }


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


    private void setTurnComponentActionBoolean(ActionType actionType, TurnComponent turnComponent) {

        switch (actionType) {
            case Movement:
                turnComponent.movementActionAvailable = false;
                break;
            case Action:
                turnComponent.attackActionAvailable = false;
                break;
            case MoveAndAction:
                turnComponent.movementActionAvailable = false;
                turnComponent.attackActionAvailable = false;
                break;
        }

    }


    public static class Builder {

        private String name = "N/A";
        private String icon;
        private Targeting targeting = Targeting.Enemy;
        private Attack attack = Attack.Ranged;
        private ActionType actionType = ActionType.MoveAndAction;
        private SpellAnimation spellAnimation = SpellAnimation.Projectile;
        private SpellType spellType = SpellType.MagicAttack;
        private SpellDamageApplication spellDamageApplication = SpellDamageApplication.Instant;

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

        public Builder actionType(ActionType val) {
            this.actionType = val;
            return this;
        }

        public Builder spellAnimation(SpellAnimation val) {
            this.spellAnimation = val;
            return this;
        }

        public Builder spellType(SpellType val) {
            this.spellType = val;
            return this;
        }

        public Builder spellApplication(SpellDamageApplication val) {
            this.spellDamageApplication = val;
            return this;
        }


        public Skill build() {
            return new Skill(this);
        }

    }


    //public


}





