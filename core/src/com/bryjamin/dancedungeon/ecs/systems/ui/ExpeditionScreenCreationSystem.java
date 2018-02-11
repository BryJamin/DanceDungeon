package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.GreyScaleComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenteringFrame;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 10/02/2018.
 */

public class ExpeditionScreenCreationSystem extends BaseSystem {

    private static int PARTY_SIZE = 4;

    private Viewport gameport;
    private MainGame game;

    private Array<UnitData> availableMembers;
    private Array<UnitData> partyMembers = new Array<UnitData>(4);

    public ExpeditionScreenCreationSystem(MainGame game, Viewport gameport, Array<UnitData> availableMembers, Array<UnitData> partyMembers){
        this.gameport = gameport;
        this.game = game;
        this.availableMembers = availableMembers;
        this.partyMembers.addAll(partyMembers);
    }

    @Override
    protected void initialize() {

        CenteringFrame centeringFrame = new CenteringFrame(Measure.units(80f), Measure.units(0), Measure.units(20f), gameport.getWorldHeight());
        centeringFrame.setWidthPer(Measure.units(10f));
        centeringFrame.setHeightPer(Measure.units(7.5f));
        centeringFrame.setColumns(2);
        centeringFrame.setRows(5);
        centeringFrame.setyGap(Measure.units(2.5f));
        centeringFrame.setxGap(Measure.units(2.5f));



        //Right hand column
        for(int i = 0; i < availableMembers.size; i++){

            Vector2 position = centeringFrame.calculatePositionReverseY(i);

            System.out.println(position);

            Entity e = createPartyIcon(position.x, position.y, availableMembers.get(i));

            if(partyMembers.contains(availableMembers.get(i), true))
                e.edit().add(new GreyScaleComponent());
            else
                e.edit().add(new ActionOnTapComponent(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {
                        addToParty(entity.getComponent(UnitComponent.class).getUnitData());
                    }
                }));
        }


        centeringFrame = new CenteringFrame(Measure.units(0), Measure.units(0f), gameport.getWorldWidth(), Measure.units(20f));
        centeringFrame.setWidthPer(Measure.units(10f));
        centeringFrame.setColumns(4);
        centeringFrame.setRows(1);
        centeringFrame.setxGap(Measure.units(2.5f));

        for(int i = 0; i < partyMembers.size; i++){

            Vector2 position = centeringFrame.calculatePosition(i);

            if(partyMembers.get(i) != null) {
                Entity e = createPartyIcon(position.x, position.y, partyMembers.get(i));
                e.edit().add(new ActionOnTapComponent(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {

                        int i = partyMembers.indexOf(entity.getComponent(UnitComponent.class).getUnitData(), false);

                        if(i != -1){
                            partyMembers.set(i, null);
                            updateUi();
                        }

                    }
                }));
            }

        }
    }


    private void addToParty(UnitData unitData){

        if(partyMembers.size > PARTY_SIZE) return;

        for(int i = 0; i < PARTY_SIZE; i++){
            try {
                if(partyMembers.get(i) == null) {//Gaps in the party are set to null
                    partyMembers.set(i, unitData);
                    updateUi();
                    return;
                }
            } catch (IndexOutOfBoundsException e){
                partyMembers.insert(i, unitData);
                updateUi();
                return;
            }
        }

    }


    private Entity createPartyIcon(float x, float y, UnitData unitData){

        float width = Measure.units(7.5f);

        Entity e = world.createEntity().edit()
                .add(new PositionComponent(x, y))
                .add(new UnitComponent(unitData))
                .add(new HitBoxComponent(width, width))
                .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                        new TextureDescription.Builder(unitData.icon)
                                .width(width)
                                .height(width)
                                .build())).getEntity();

        return e;
    }



    private void updateUi(){

        IntBag unitEntities = world.getAspectSubscriptionManager().get(Aspect.all(UnitComponent.class)).getEntities();

        for(int i = 0; i < unitEntities.size(); i++)
            world.delete(unitEntities.get(i));

        initialize();

    }


    @Override
    protected void processSystem() {

    }
}
