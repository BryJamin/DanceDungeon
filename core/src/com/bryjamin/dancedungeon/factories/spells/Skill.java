package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.QueuedInstantAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.AvailableActionsCompnent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.StunnedComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnPushableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.KillOnAnimationEndComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SolidComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnkillableComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.spells.animations.BasicProjectile;
import com.bryjamin.dancedungeon.factories.spells.animations.BasicSlashAnimation;
import com.bryjamin.dancedungeon.factories.spells.animations.BasicThrown;
import com.bryjamin.dancedungeon.utils.enums.Direction;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.HighlightedText;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

import java.util.UUID;

/**
 * Created by BB on 18/11/2017.
 */

public class Skill {

    public enum Targeting {Ally, Normal, Self, StraightShot}
    public enum ActionType {UsesMoveAndAttackAction, UsesAttackAction, UsesMoveAction, Free}
    public enum SpellAnimation {Projectile, Slash, Glitter, Thrown}
    public enum AttackType {Heal, HealOverTime, Damage, Burn, None}
    public enum SpellCoolDown {NoCoolDown, OverTime, Limited}
    private transient String skillId = UUID.randomUUID().toString();
    private String name = "N/A";
    private String description = "N/A";
    private String icon = TextureStrings.BLOCK;
    private int uses = 2;
    private int coolDown = 2;
    private int coolDownTracker = 0;
    private int push = 0;
    private int stun = 0;
    private int storePrice = 1;
    private int sellPrice = 1;
    private boolean purchasable = true;
    private int baseDamage = 1;
    //Min and Max Range only affects certain skills
    private int minRange = 1;
    private int maxRange = 1;
    public Skill affectedAreaSkill;
    public Coordinates[] affectedAreas = new Coordinates[]{};



    public static final int MAX_MAX_RANGE = 10; //Maximum range possible for a skill. To avoid counting

    private Targeting targeting = Targeting.Normal;
    private ActionType actionType = ActionType.UsesMoveAndAttackAction;
    private SpellAnimation spellAnimation = SpellAnimation.Projectile;
    private AttackType attackType = AttackType.Damage;
    private SpellCoolDown spellCoolDown = SpellCoolDown.NoCoolDown;
    private SpellEffect[] spellEffects;

    public enum SpellEffect {
        Stun, OnFire, Dodge, Armor;

        public float number;
        public int duration;

        public SpellEffect value(float number) {
            this.number = number;
            return this;
        }

        public SpellEffect duration(int duration) {
            this.duration = duration;
            return this;
        }

    }

    public Skill(){ }

    public Skill (Skill s){
        this.name = s.name;
        this.description = s.description;
        this.icon = s.icon;

        this.uses = s.uses;
        this.coolDown = s.coolDown;
        this.coolDownTracker = s.coolDownTracker;
        this.push = s.push;
        this.stun = s.stun;
        this.storePrice = s.storePrice;
        this.purchasable = s.purchasable;
        this.baseDamage = s.baseDamage;
        this.minRange = s.minRange;
        this.maxRange = s.maxRange;
        if(s.affectedAreaSkill != null) {
            this.affectedAreaSkill = new Skill(s.affectedAreaSkill);
        }
        this.affectedAreas = s.affectedAreas;
        this.targeting = s.targeting;
        this.actionType = s.actionType;
        this.spellAnimation = s.spellAnimation;
        this.attackType = s.attackType;
        this.spellCoolDown = s.spellCoolDown;
        this.spellEffects = s.spellEffects;
    }

    public Array<Coordinates> getAffectedCoordinates(World world, Coordinates coordinates) {

        Array<Coordinates> coordinatesArray = new Array<Coordinates>();

        switch (targeting) {
            case Normal:
                coordinatesArray = CoordinateMath.getCoordinatesInLine(coordinates, minRange, maxRange);
                break;
            case StraightShot:
                Direction[] directions = {Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP};
                coordinatesArray = world.getSystem(TileSystem.class).getFreeCoordinateInAGivenDirection(coordinates, directions);
            break;
        }

        world.getSystem(TileSystem.class).removeInvalidCoordinates(coordinatesArray);

        return coordinatesArray;

    }


    public Array<Entity> createTargeting(World world, Entity player) {

        Array<Entity> entityArray = new Array<Entity>();

        switch (targeting) {
            case Normal:
            case StraightShot:
                for (Coordinates c : getAffectedCoordinates(world, player.getComponent(CoordinateComponent.class).coordinates)) {
                    entityArray.add(new TargetingFactory().createTargetingBox(world, player, c, this, true));

                    if(targeting == Targeting.StraightShot){
                        entityArray.addAll(new TargetingFactory().createWhiteTargetingMarkers(world, player.getComponent(CoordinateComponent.class).coordinates,
                                c));
                    }

                }

                break;
            case Self:
                entityArray = new TargetingFactory().createSelfTargetTiles(world, player, this, 1);
                break;
        }

        return entityArray;
    }


    public boolean canCast(World world, Entity entity) {

        AvailableActionsCompnent availableActionsCompnent = entity.getComponent(AvailableActionsCompnent.class);

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
                return availableActionsCompnent.movementActionAvailable;
            case UsesAttackAction:
                return availableActionsCompnent.attackActionAvailable;
            case UsesMoveAndAttackAction:
                return availableActionsCompnent.hasActions();
        }

        return true;
    }

    public void cast(World world, Entity caster, Coordinates target) {
        AvailableActionsCompnent availableActionsCompnent = caster.getComponent(AvailableActionsCompnent.class);
        setTurnComponentActionBoolean(actionType, availableActionsCompnent);


        switch (spellCoolDown) {
            case Limited:
                uses--;
                break;
            case OverTime:
                coolDownTracker = coolDown;
                break;
        }


        createSpellEffects(world, caster, caster.getComponent(CoordinateComponent.class).coordinates, target);

/*        if(spellAnimation != SpellAnimation.Projectile) {
            castSpellOnTargetLocation(world, caster, caster.getComponent(CoordinateComponent.class).coordinates, target);
        }*/

    }


    /**
     * Method for creating the Skill Animations. These animations usually store the skill information
     * and then use them after their animation has been played
     * @param world
     * @param entity - The owner of the skill.
     * @param castCoordinates - The Coordinates from which the the skill used.
     * @param target - The Target coordinate for the skill.
     */
    public void createSpellEffects(World world, Entity entity, Coordinates castCoordinates, Coordinates target){

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
                world.getSystem(ActionQueueSystem.class).createDeathWaitAction(heal);


                break;

            case Slash:
                new BasicSlashAnimation().cast(world, entity, this, castCoordinates, target);
                break;
            case Projectile:
                new BasicProjectile().cast(world, entity, this, castCoordinates, target);
                break;
            case Thrown:
                new BasicThrown().cast(world, entity, this, castCoordinates, target);
                break;
        }
    }


    public String getSkillId() {
        return skillId;
    }

    public void castSpellOnTargetLocation(String id, World world, Entity caster, Coordinates casterCoords, Coordinates target) {

        Array<Entity> entityArray = world.getSystem(TileSystem.class).getCoordinateMap().get(target);
        //If a coordinate is selected outside of map coordinates
        if(entityArray == null) return;


        boolean isUnkillable = false;

        for (final Entity e : world.getSystem(TileSystem.class).getCoordinateMap().get(target)) {

            if(stun > 0){
                e.getComponent(UnitComponent.class).getUnitData().stun = stun;
                e.edit().add(new StunnedComponent());
            }

            if (world.getMapper(HealthComponent.class).has(e)) {

                switch (attackType) {

                    case Damage:

                        if(e.getComponent(HealthComponent.class).health - baseDamage < 0){
                            isUnkillable = true;
                        }

                        e.getComponent(HealthComponent.class).applyDamage(baseDamage);

                        break;
                    case Heal:
                        e.getComponent(HealthComponent.class).applyHealing(baseDamage);
                        break;
                }
            }


            /**
             * USED FOR CALCUALTING THE PUSH MOVEMENT OF AN ENEMY
             */
            if (push != 0 && e.getComponent(UnPushableComponent.class) == null && e.getComponent(SolidComponent.class) != null) { //If the entity can be pushed

                TileSystem tileSystem = world.getSystem(TileSystem.class);
                //Coordinates casterCoords = casterEntity.getComponent(CoordinateComponent.class).coordinates;

                if (push != 0) {

                    //PUSH MECHANIC.

                    Coordinates[] pushCoordinateArray = new Coordinates[Math.abs(push) + 1];


                    for (int i = 0; i <= Math.abs(push); i++) { //Decides the direction used to shove a target

                        int x = casterCoords.getY() == target.getY() ? casterCoords.getX() < target.getX() ? i : -i : 0;
                        int y = casterCoords.getX() == target.getX() ? casterCoords.getY() < target.getY() ? i : -i : 0;

                        if(push < 0){
                            x *= -1;
                            y *= -1;
                        }

                        if((x == 0 && y != 0) || (x != 0 && y == 0) || (x == 0 && y == 0)) {
                            pushCoordinateArray[i] = new Coordinates(target.getX() + x, target.getY() + y);
                        }
                    }


                    for (int i = 1; i < pushCoordinateArray.length; i++) { //Starts at one incase you are knocked back to the previous coordinate

                        Coordinates pushCoords = pushCoordinateArray[i];
                        Coordinates prev = pushCoordinateArray[i - 1];

                        //Check if coordinate is off the side of the map. If it is, look back to the previous coordinate.
                        if (!tileSystem.getCoordinateMap().containsKey(pushCoords)) {
                            world.getSystem(ActionQueueSystem.class).createMovementAction(e, skillId,
                                    tileSystem.getPositionUsingCoordinates(prev, e.getComponent(CenteringBoundComponent.class).bound));

                            break;
                        }

                        if (tileSystem.getOccupiedMap().containsValue(pushCoords, false)) { //Pretend move but bounce back

                            world.getSystem(ActionQueueSystem.class).createMovementAction(e, skillId,
                                    tileSystem.getPositionUsingCoordinates(pushCoords, e.getComponent(CenteringBoundComponent.class).bound),
                                    tileSystem.getPositionUsingCoordinates(prev, e.getComponent(CenteringBoundComponent.class).bound)
                            );

                            world.getSystem(ActionQueueSystem.class).createDamageApplicationAction(e, 1); //Push damage is one.
                            world.getSystem(ActionQueueSystem.class).createDamageApplicationAction(tileSystem.getOccupiedMap().getKey(pushCoords, false), 1);

                            break;
                        }
                        ;

                        if (i == pushCoordinateArray.length - 1) { //Final loop
                            world.getSystem(ActionQueueSystem.class).createMovementAction(e, skillId,
                                    tileSystem.getPositionUsingCoordinates(pushCoords, e.getComponent(CenteringBoundComponent.class).bound));

                        }

                        //Check if end of coordinate array

                    }

                }
            }

            if(isUnkillable){
                e.edit().add(new UnkillableComponent());
                world.getSystem(ActionQueueSystem.class).pushLastAction(e, new QueuedInstantAction() {
                    @Override
                    public void act() {
                        if(e.getComponent(UnkillableComponent.class) != null) {
                            e.edit().remove(UnkillableComponent.class);
                        }
                    }
                });
            }


        }


        if(affectedAreas.length > 0){

            affectedAreaSkill.setSkillId(getSkillId());

            for(Coordinates c : affectedAreas){
                Coordinates affected = new Coordinates(target.getX() + c.getX(), target.getY() + c.getY());


                affectedAreaSkill.createSpellEffects(world, caster, target, affected);

                if(affectedAreaSkill.spellAnimation != SpellAnimation.Projectile) {
                    affectedAreaSkill.castSpellOnTargetLocation(id, world, caster, target, affected);
                }
            }

        }

    }


    ;

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

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

    public String getDescription() {
        return description;
    }

    public HighlightedText getHighlight(World world, Entity entity) {
        return null;
    }


    public int getBaseDamage() {
        return baseDamage;
    }

    public SpellAnimation getSpellAnimation() {
        return spellAnimation;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public Targeting getTargeting() {
        return targeting;
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

    public int getStorePrice() {
        return storePrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    private void setTurnComponentActionBoolean(ActionType actionType, AvailableActionsCompnent availableActionsCompnent) {

        switch (actionType) {
            case UsesMoveAction:
                availableActionsCompnent.movementActionAvailable = false;
                break;
            case UsesAttackAction:
                availableActionsCompnent.attackActionAvailable = false;
                break;
            case UsesMoveAndAttackAction:
                availableActionsCompnent.movementActionAvailable = false;
                availableActionsCompnent.attackActionAvailable = false;
                break;
        }

    }

}





