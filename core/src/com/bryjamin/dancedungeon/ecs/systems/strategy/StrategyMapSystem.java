package com.bryjamin.dancedungeon.ecs.systems.strategy;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.FixedToCameraComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.ScaleTransformationComponent;
import com.bryjamin.dancedungeon.ecs.components.map.MapNodeComponent;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.MapNode;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 18/12/2017.
 */

public class StrategyMapSystem extends EntitySystem {

    private ComponentMapper<ActionOnTapComponent> actionOnTapMapper;
    private EventGenerationSystem eventGenerationSystem;

    float width = Measure.units(7f);
    float height = Measure.units(7f);
    float gap = Measure.units(10f);

    private MainGame game;
    private GameMap gameMap;

    private Color grey = new Color(0.4f, 0.4f, 0.4f, 1f);

    private PartyDetails partyDetails;

    private OrderedMap<MapNode, Entity> nodeEntityOrderedMap = new OrderedMap<MapNode, Entity>();
    private Array<Entity> activeNodes = new Array<Entity>();

    public StrategyMapSystem(MainGame game, GameMap gameMap, PartyDetails partyDetails) {
        super(Aspect.all(MapNodeComponent.class));
        this.game = game;
        this.gameMap = gameMap;
        this.partyDetails = partyDetails;
    }

    @Override
    public void inserted(Entity e) {
        nodeEntityOrderedMap.put(e.getComponent(MapNodeComponent.class).getNode(), e);
        if (actionOnTapMapper.has(e)) {
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

        for (final MapNode node : gameMap.getAllNodes()) {

            Entity e = createNodeEntity(node);
            if (gameMap.getCurrentMapNode() == null) {
                if (gameMap.getMapNodeSections().first().getMapNodes().contains(node, true)) {
                    createActiveNode(e);
                }
            }

            for (MapNode innerNode : node.getSuccessors()) {
                drawLineEntity(node, innerNode);
            }

        }

        createGenerateMapButton();
        createSelectDestinationText();

    }

    @Override
    protected void processSystem() {

    }


    private WorldAction selectNodeAction(final MapNode mapNode) {

        return new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                gameMap.setCurrentMapNode(mapNode);
                mapNode.setMapEvent(eventGenerationSystem.getMapEvent(mapNode.getEventType()));
                game.setScreen(new BattleScreen(game, game.getScreen(), gameMap, partyDetails));
            }
        };


    }


    public void onVictory() {

        for (Entity e : activeNodes) {
            if (gameMap.getCurrentMapNode().equals(e.getComponent(MapNodeComponent.class).getNode())) {
                createCompletedNode(e);
            } else {
                createUnreachableNode(e);
            }
        }

        activeNodes.clear();

        for (MapNode mapNode : gameMap.getCurrentMapNode().getSuccessors()) {
            Entity nodeEntity = nodeEntityOrderedMap.get(mapNode);
            activeNodes.add(nodeEntity);
            createActiveNode(nodeEntity);
        }
    }

    private Entity createNodeEntity(MapNode node) {

        String texture;

        switch (node.getEventType()) {
            case BATTLE:
                texture = TextureStrings.ICON_COMBAT;
                break;
            case BOSS:
                texture = TextureStrings.ICON_COMBAT;
                break;
            case SHOP:
                texture = TextureStrings.ICON_MONEY;
                break;
            case REST:
                texture = TextureStrings.ICON_REST;
                break;
            default:
                texture = TextureStrings.BLOCK;
        }

        Entity e = world.createEntity();

        e.edit().add(new PositionComponent(CenterMath.centerPositionX(width, node.getPosX()),
                CenterMath.centerPositionY(height, node.getPosY())))
                .add(new HitBoxComponent(width, height))
                .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE, new TextureDescription.Builder(texture)
                        .width(width)
                        .height(height)
                        .color(new Color(grey))
                        .build()))
                .add(new MapNodeComponent(node));

        return e;

    }

    /**
     * Edits a MapNode to one that is 'unreachable' this means a player can not click on it
     */
    private void createUnreachableNode(Entity e) {
        e.edit().remove(ScaleTransformationComponent.class)
                .remove(ActionOnTapComponent.class);
        e.getComponent(DrawableComponent.class).setColor(new Color(grey));
    }

    ;


    /**
     * Edits a MapNode to one that is 'active' This means it can be touched and
     * is made more obviously to players
     */
    private void createActiveNode(Entity e) {
        e.edit().add(new ActionOnTapComponent(selectNodeAction(e.getComponent(MapNodeComponent.class).getNode())))
                .add(new ScaleTransformationComponent(1.2f, 1.2f));
        e.getComponent(DrawableComponent.class).setColor(new Color(Color.WHITE));
    }



    /**
     * Edits a MapNode to one that is 'unreachable' this means a player can not click on it
     */
    private void createCompletedNode(Entity e) {
        e.edit().remove(ScaleTransformationComponent.class)
                .remove(ActionOnTapComponent.class);
        e.getComponent(DrawableComponent.class).setColor(new Color(Color.GREEN));
    }


    /**
     * Creates a Line Entity between a two nodes. (Could just be two points) If I decide to create
     * lines in future
     */
    private void drawLineEntity(MapNode parent, MapNode child) {

        Entity line = world.createEntity();

        Vector2 startPos = parent.getPosition();
        Vector2 endPos = child.getPosition();

        line.edit().add(new PositionComponent(parent.getPosition()))
                .add(new DrawableComponent(Layer.BACKGROUND_LAYER_FAR,
                        new TextureDescription.Builder(TextureStrings.BLOCK)
                                .width(startPos.dst(endPos))
                                .height(15)
                                .origin(new Vector2(0, 0)) //Set the origin, As the default is centered
                                .rotation(Math.atan2(endPos.y - startPos.y, endPos.x - startPos.x) * 180 / Math.PI)
                                .build()
                ));

    }


    private void createGenerateMapButton() {

        float width = Measure.units(15f);
        float height = Measure.units(7.5f);

        Entity generate = world.createEntity().edit()
                .add(new PositionComponent(Measure.units(75f), Measure.units(50f)))
                .add(new HitBoxComponent(new HitBox(width, height)))
                .add(new FixedToCameraComponent(0, Measure.units(50f)))
                .add(new CenteringBoundaryComponent(new Rectangle(0, 0, width, height)))
                .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                        new TextureDescription.Builder(TextureStrings.BLOCK)
                                .width(width)
                                .height(height).build(),
                        new TextDescription.Builder(Fonts.MEDIUM)
                                .text("GENERATE")
                                .color(new Color(Color.BLACK))
                                .build()))
                .add(new ActionOnTapComponent(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {
                        game.getScreen().dispose();
                        game.setScreen(new MapScreen(game));
                    }
                })).getEntity();

    }


    private void createButton() {

        float width = Measure.units(15f);
        float height = Measure.units(7.5f);

        Entity generate = world.createEntity().edit()
                .add(new PositionComponent(Measure.units(75f), Measure.units(50f)))
                .add(new HitBoxComponent(new HitBox(width, height)))
                .add(new FixedToCameraComponent(0, Measure.units(50f)))
                .add(new CenteringBoundaryComponent(new Rectangle(0, 0, width, height)))
                .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                        new TextureDescription.Builder(TextureStrings.BLOCK)
                                .width(width)
                                .height(height).build(),
                        new TextDescription.Builder(Fonts.MEDIUM)
                                .text("GENERATE")
                                .color(new Color(Color.BLACK))
                                .build()))
                .add(new ActionOnTapComponent(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {
                        game.getScreen().dispose();
                        game.setScreen(new MapScreen(game));
                    }
                })).getEntity();

    }




    private void createSelectDestinationText() {

        float width = MainGame.GAME_WIDTH;
        float height = Measure.units(7.5f);

        Entity generate = world.createEntity().edit()
                .add(new PositionComponent())
                .add(new FixedToCameraComponent(CenterMath.centerPositionX(width, width / 2), Measure.units(50f)))
                .add(new CenteringBoundaryComponent(new Rectangle(0, 0, width, height)))
                .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                        new TextDescription.Builder(Fonts.MEDIUM)
                                .text("Select Your Next Destination")
                                .color(new Color(Color.WHITE))
                                .build())).getEntity();

    }

}
