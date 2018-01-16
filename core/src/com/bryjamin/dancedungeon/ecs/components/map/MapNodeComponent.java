package com.bryjamin.dancedungeon.ecs.components.map;

import com.artemis.Component;
import com.bryjamin.dancedungeon.factories.map.MapNode;

/**
 * Created by BB on 07/01/2018.
 */

public class MapNodeComponent extends Component {

    private enum Type {
        BATTLE, SHOP, BOSS
    }

    private Type type;

    private MapNode node;

    public MapNodeComponent(){

    };

    public MapNodeComponent(MapNode node){
        type = Type.BATTLE;
        this.node = node;
    }

    public MapNode getNode() {
        return node;
    }

    public Type getType() {
        return type;
    }
}
