package com.bryjamin.dancedungeon.ecs.components.map;

import com.artemis.Component;
import com.bryjamin.dancedungeon.factories.map.MapNode;

/**
 * Created by BB on 07/01/2018.
 *
 * Used Within the MapNode System to keep track of which Nodes are loaded when an Entity is tapped on.
 */

public class MapNodeComponent extends Component {

    private MapNode node;

    public MapNodeComponent(){

    };

    public MapNodeComponent(MapNode node){
        this.node = node;
    }

    public MapNode getNode() {
        return node;
    }

}
