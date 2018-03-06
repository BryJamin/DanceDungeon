package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.BuffComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.KillOnAnimationEndComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
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

    public enum Targeting {Ally, Melee, Self, FreeAim, StraightShot}
    public enum Attack {Melee, Ranged, Transformative}
    public enum ActionType {UsesMoveAndAttackAction, UsesAttackAction, UsesMoveAction, Free}
    public enum SpellDamageApplication {Instant, AfterSpellAnimation}
    public enum SpellAnimation {Projectile, Slash, Glitter}
    public enum SpellType {Heal, HealOverTime, MagicAttack, PhysicalAttack, Burn}
    public enum SpellEffect {
        Stun, OnFire, Dodge, Armor;

        public float number;
        public int duration;

        public SpellEffect value(float number){
            this.number = number;
            return this;
        }

        public SpellEffect duration(int duration){
            this.duration = duration;
            return this;
        }

    }
    public enum SpellCoolDown {NoCoolDown, OverTime, Limited}


    private String name = "N/A";
    private String description = "N/A1";
    private String icon;

    private int uses = 2;
    private int coolDown = 2;
    private int coolDownTracker = 0;
    private int push = 0;


    private Targeting targeting = Targeting.Melee;
    private Attack attack = Attack.Ranged;
    private ActionType actionType = ActionType.UsesMoveAndAttackAction;
    private SpellAnimation spellAnimation = SpellAnimation.Projectile;
    private SpellType spellType = SpellType.MagicAttack;
    private SpellDamageApplication spellDamageApplication = SpellDamageApplication.Instant;
    private SpellCoolDown spellCoolDown = SpellCoolDown.NoCoolDown;
    private SpellEffect[] spellEffects;


    public Skill(Builder b) {
        this.name = b.name;
        this.description = b.description;
        this.icon = b.icon;
        this.targeting = b.targeting;
        this.attack = b.attack;
        this.actionType = b.actionType;
        this.spellAnimation = b.spellAnimation;
        this.spellType = b.spellType;
        this.spellDamageApplication = b.spellDamageApplication;
        this.spellEffects = b.spellEffects;
        this.spellCoolDown = b.spellCoolDown;
        this.coolDown = b.cooldown;
        this.push = b.push;
    }

    public Array<Entity> createTargeting(World world, Entity player) {

        Array<Entity> entityArray = new Array<Entity>();

        int range = attack == Attack.Melee ? 1 : player.getComponent(StatComponent.class).attackRange;

        switch (targeting) {
            case Ally:
                entityArray = new TargetingFactory().createAllyTargetTiles(world, player, this, range);
                break;
            case FreeAim:
                entityArray = new TargetingFactory().createFreeAimTargetTiles(world, player, this, range);
                break;
            case StraightShot:
                entityArray = new TargetingFactory().createStraightShotTargetTiles(world, player, this);
                break;
            case Self:
                entityArray = new TargetingFactory().createSelfTargetTiles(world, player, this, range);
                break;
            case Melee:
                entityArray = new TargetingFactory().createAdjacentTiles(world, player, this, 1);
                break;

        }

        return entityArray;
    }


/*
    public Array<Entity> createEnemyIntent(World world, ){











    }
*/





    ;

    public boolean canCast(World world, Entity entity) {

        TurnComponent turnComponent = entity.getComponent(TurnComponent.class);

        switch (spellCoolDown) {
            case Limited:
                if (uses <= 0) return false;
                break;
            case OverTime:
                if (coolDownTracker > 0) return false;
                break;
        }


        switch (actionType) {
            case UsesMoveAction:
                return turnComponent.movementActionAvailable;
            case UsesAttackAction:
                return turnComponent.attackActionAvailable;
            case UsesMoveAndAttackAction:
                return turnComponent.hasActions();
        }

        return true;
    }

    public void cast(World world, Entity entity, Coordinates target) {
        TurnComponent turnComponent = entity.getComponent(TurnComponent.class);
        setTurnComponentActionBoolean(actionType, turnComponent);


        switch (spellCoolDown) {
            case Limited:
                uses--;
                break;
            case OverTime:
                coolDownTracker = coolDown;
                break;
        }


        Rectangle rectangle = world.getSystem(TileSystem.class).createRectangleUsingCoordinates(target);

        switch (spellAnimation) {
            case Glitter:
                final int GLITTER_ANIMATION_ID = 0;
                Entity heal = world.createEntity();
                heal.edit().add(new PositionComponent(rectangle.x, rectangle.y))
                        .add(new DrawableComponent(Layer.FOREGROUND_LAYER_FAR, new TextureDescription.Builder(TextureStrings.SKILLS_HEAL)
                                .color(Color.GREEN)
                                .width(rectangle.getWidth())
                                .height(rectangle.getHeight())
                                .build()))
                        .add(new AnimationStateComponent(GLITTER_ANIMATION_ID))
                        .add(new AnimationMapComponent().put(GLITTER_ANIMATION_ID, TextureStrings.SKILLS_HEAL, 0.2f, Animation.PlayMode.NORMAL))
                        .add(new KillOnAnimationEndComponent(GLITTER_ANIMATION_ID));
                world.getSystem(ActionCameraSystem.class).createDeathWaitAction(heal);

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
                        .add(new AnimationStateComponent(SLASH_ANIMATION))
                        .add(new AnimationMapComponent().put(SLASH_ANIMATION, TextureStrings.SKILLS_SLASH, 0.3f, Animation.PlayMode.NORMAL))
                        .add(new KillOnAnimationEndComponent(SLASH_ANIMATION));

                world.getSystem(ActionCameraSystem.class).createDeathWaitAction(slash);

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
                        .damage(entity.getComponent(StatComponent.class).attack)
                        .skill(this)
                        .build()
                        .cast(world, entity, target);

                return;
        }


        switch (spellDamageApplication) {

            case Instant:
                castSpellOnTargetLocation(world, entity, target);
                break;


        }


    }




    public void castSpellOnTargetLocation(World world, Entity casterEntity, Coordinates target){

        for (Entity e : world.getSystem(TileSystem.class).getCoordinateMap().get(target)) {


            for (SpellEffect spellEffect : spellEffects) {
                switch (spellEffect) {
                    case Stun:
                        e.getComponent(StatComponent.class).stun = (int) spellEffect.number;
                        //break;
                    case Dodge:
                        e.getComponent(BuffComponent.class).spellEffectArray.add(spellEffect);
                }
            }


            if (world.getMapper(HealthComponent.class).has(e)) {

                StatComponent sc = casterEntity.getComponent(StatComponent.class);

                switch (spellType) {

                    case MagicAttack:
                    case PhysicalAttack:

                        e.getComponent(HealthComponent.class).applyDamage(sc.attack);

                        break;

                    case Heal:
                        e.getComponent(HealthComponent.class).applyHealing(sc.attack);
                        break;
                }
            }


            /**
             * USED FOR CALCUALTING THE PUSH MOVEMENT OF AN ENEMY
             */

            if(push != 0){

                TileSystem tileSystem = world.getSystem(TileSystem.class);

                Coordinates castCoords = casterEntity.getComponent(CoordinateComponent.class).coordinates;

                if(push > 0){

                    //PUSH MECHANIC.

                    Coordinates[] pushCoordinateArray = new Coordinates[push + 1];


                    for(int i = 0; i <= push; i++){ //Decides the direction used to shove a target

                        if(castCoords.getX() < target.getX() && castCoords.getY() == target.getY()){
                            pushCoordinateArray[i] = new Coordinates(target.getX() + i, target.getY());
                        } else if(castCoords.getX() > target.getX() && castCoords.getY() == target.getY()){
                            pushCoordinateArray[i] = new Coordinates(target.getX() - i, target.getY());
                        } else if(castCoords.getX() == target.getX() && castCoords.getY() < target.getY()){
                            pushCoordinateArray[i] = new Coordinates(target.getX(), target.getY() + i);
                        } else if(castCoords.getX() == target.getX() && castCoords.getY() > target.getY()){
                            pushCoordinateArray[i] = new Coordinates(target.getX(), target.getY() - i);
                        } else {
                            System.out.println("ERROR");
                            return;
                        }
                    }


                    for(int i = 1; i < pushCoordinateArray.length; i++){

                        Coordinates pushCoords = pushCoordinateArray[i];
                        Coordinates prev = pushCoordinateArray[i - 1];

                        //Check if coordinate is off the side of the map. If it is, look back to the previous coordinate.
                        if(!tileSystem.getCoordinateMap().containsKey(pushCoords)){
                            world.getSystem(ActionCameraSystem.class).createMovementAction(e,
                                    tileSystem.getPositionUsingCoordinates(prev, e.getComponent(CenteringBoundaryComponent.class).bound));
                            world.getSystem(ActionCameraSystem.class).createIntentAction(e);
                            break;
                        }

                        if(tileSystem.getOccupiedMap().containsValue(pushCoords, false)){ //Pretend move but bounce back
                            world.getSystem(ActionCameraSystem.class).createMovementAction(e,
                                    tileSystem.getPositionUsingCoordinates(pushCoords, e.getComponent(CenteringBoundaryComponent.class).bound),
                                    tileSystem.getPositionUsingCoordinates(prev, e.getComponent(CenteringBoundaryComponent.class).bound)
                                    );
                            world.getSystem(ActionCameraSystem.class).createIntentAction(e);
                            break;
                        };

                        if(i == pushCoordinateArray.length - 1){ //Final loop
                            world.getSystem(ActionCameraSystem.class).createMovementAction(e,
                                    tileSystem.getPositionUsingCoordinates(pushCoords, e.getComponent(CenteringBoundaryComponent.class).bound));
                            world.getSystem(ActionCameraSystem.class).createIntentAction(e);
                        }

                        //Check if end of coordinate array

                    }

                }
            }
        }
    }







    ;

    public void endTurnUpdate() {
        switch (spellCoolDown) {
            case OverTime:
                coolDownTracker--;
        }
    }


    public String getIcon() {
        return icon;
    }


    public String getName() {
        return name;
    }

    public String getDescription(World world, Entity entity) {
        return description;
    }

    public HighlightedText getHighlight(World world, Entity entity) {
        return null;
    }


    public SpellAnimation getSpellAnimation() {
        return spellAnimation;
    }

    public SpellType getSpellType() {
        return spellType;
    }

    public Attack getAttack() {
        return attack;
    }

    public SpellDamageApplication getSpellDamageApplication() {
        return spellDamageApplication;
    }

    public SpellCoolDown getSpellCoolDown() {
        return spellCoolDown;
    }

    public int getCoolDownTracker() {
        return coolDownTracker;
    }

    public SpellEffect[] getSpellEffects() {
        return spellEffects;
    }

    private void setTurnComponentActionBoolean(ActionType actionType, TurnComponent turnComponent) {

        switch (actionType) {
            case UsesMoveAction:
                turnComponent.movementActionAvailable = false;
                break;
            case UsesAttackAction:
                turnComponent.attackActionAvailable = false;
                break;
            case UsesMoveAndAttackAction:
                turnComponent.movementActionAvailable = false;
                turnComponent.attackActionAvailable = false;
                break;
        }

    }


    public static class Builder {

        private String name = "N/A";
        private String description = "N/A";
        private String icon;
        private Targeting targeting = Targeting.StraightShot;
        private Attack attack = Attack.Ranged;
        private ActionType actionType = ActionType.UsesMoveAndAttackAction;
        private SpellAnimation spellAnimation = SpellAnimation.Projectile;
        private SpellType spellType = SpellType.Burn;
        private SpellDamageApplication spellDamageApplication = SpellDamageApplication.Instant;
        private SpellEffect[] spellEffects = new SpellEffect[]{};
        private SpellCoolDown spellCoolDown = SpellCoolDown.NoCoolDown;
        private int cooldown = 1;
        private int push = 0;

        public Builder name(String val) {
            this.name = val;
            return this;
        }

        public Builder description(String val) {
            this.description = val;
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

        public Builder spellCoolDown(int coolDown) {
            this.spellCoolDown = SpellCoolDown.OverTime;
            this.cooldown = coolDown;
            return this;
        }


        public Builder spellEffects(SpellEffect... val) {
            this.spellEffects = val;
            return this;
        }

        public Builder push(int val) {
            this.push = val;
            return this;
        }

        public Skill build() {
            return new Skill(this);
        }

    }


    //public


}





