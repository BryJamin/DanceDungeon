package com.bryjamin.dancedungeon.ecs.systems.strategy;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.map.MapNodeComponent;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.MapNode;
import com.bryjamin.dancedungeon.factories.player.Unit;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 18/12/2017.
 */

public class StrategyMapSystem extends EntitySystem {

    private ComponentMapper<ActionOnTapComponent> actionOnTapMapper;

    float width = Measure.units(5f);
    float height = Measure.units(5f);
    float gap = Measure.units(10f);

    private MainGame game;
    private GameMap gameMap;

    private Array<Unit> playerParty;

    private OrderedMap<MapNode, Entity> nodeEntityOrderedMap = new OrderedMap<MapNode, Entity>();
    private Array<Entity> activeNodes = new Array<Entity>();

    public StrategyMapSystem(MainGame game, GameMap gameMap, Array<Unit> playerParty) {
        super(Aspect.all(MapNodeComponent.class));
        this.game = game;
        this.gameMap = gameMap;
        this.playerParty = playerParty;
    }

    @Override
    public void inserted(Entity e) {
        nodeEntityOrderedMap.put(e.getComponent(MapNodeComponent.class).getNode(), e);
        if(actionOnTapMapper.has(e)){
            activeNodes.add(e);
        }
    }

    @Override
    public void removed(Entity e) {
        nodeEntityOrderedMap.remove(e.getComponent(MapNodeComponent.class).getNode());
        activeNodes.removeValue(e, true);
    }

    @Override
    protected void initialize() { //Create a visualize representation of the map

        for(final MapNode node : gameMap.getAllNodes()) {

            Entity e = createNodeEntity(node);
            if(gameMap.getCurrentMapNode() == null) {
                if (gameMap.getMapNodeSections().first().getMapNodes().contains(node, true)) {
                    createActiveNode(e);
                }
            }

            for (MapNode innerNode : node.getSuccessors()) {
                drawLineEntity(node, innerNode);
            }

        }

    }

    @Override
    protected void processSystem() {

    }



    private WorldAction selectNodeAction(final MapNode mapNode){

        return new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                gameMap.setCurrentMapNode(mapNode);
                PartyDetails partyDetails = new PartyDetails();
                partyDetails.setPlayerParty(playerParty);
                game.setScreen(new BattleScreen(game, game.getScreen(), gameMap, partyDetails));
            }
        };


    }



    public void onVictory(){

        for(Entity e : activeNodes){
            e.edit().remove(ActionOnTapComponent.class);

            if(gameMap.getCurrentMapNode().equals(e.getComponent(MapNodeComponent.class).getNode())) {
                createCompletedNode(e);
            } else {
                createUnreachableNode(e);
            }
        }

        activeNodes.clear();

        for(MapNode mapNode : gameMap.getCurrentMapNode().getSuccessors()){
            Entity nodeEntity = nodeEntityOrderedMap.get(mapNode);
            activeNodes.add(nodeEntity);
            createActiveNode(nodeEntity);
        }
    }

    private Entity createNodeEntity(MapNode node){

        Entity e = world.createEntity();

        e.edit().add(new PositionComponent(CenterMath.centerPositionX(width, node.getPosX()),
                CenterMath.centerPositionY(height, node.getPosY())));
        e.edit().add(new HitBoxComponent(width, height));

        e.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.BLOCK)
                .width(width)
                .height(height)
                .color(new Color(0.7f,0.7f,0.7f, 1f))
                .build()));

        e.edit().add(new MapNodeComponent(node));

        return e;

    }

    private void createUnreachableNode(Entity e){
        e.edit().add(new ActionOnTapComponent(selectNodeAction(e.getComponent(MapNodeComponent.class).getNode())));
        e.getComponent(DrawableComponent.class).setColor(new Color(0.7f,0.7f,0.7f, 1f));
    };


    private void createActiveNode(Entity e){
        e.edit().add(new ActionOnTapComponent(selectNodeAction(e.getComponent(MapNodeComponent.class).getNode())));
        e.getComponent(DrawableComponent.class).setColor(new Color(Color.WHITE));
    };

    private void createCompletedNode(Entity e){
        e.getComponent(DrawableComponent.class).setColor(new Color(Color.GREEN));
    }



    private void drawLineEntity(MapNode parent, MapNode child){

        Entity line = world.createEntity();

        line.edit().add(new PositionComponent(parent.getPosition()));

        Vector2 startPos = parent.getPosition();
        Vector2 endPos = child.getPosition();

        line.edit().add(new DrawableComponent(Layer.BACKGROUND_LAYER_FAR,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .width(startPos.dst(endPos))
                        .height(15)
                        .origin(new Vector2(0, 0))
                        .rotation(Math.atan2(endPos.y - startPos.y, endPos.x - startPos.x) * 180 / Math.PI)
                        .build()
        ));

    }







}
