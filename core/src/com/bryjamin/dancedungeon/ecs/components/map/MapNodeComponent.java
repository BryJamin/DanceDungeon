package com.bryjamin.dancedungeon.ecs.components.map;

import com.artemis.Component;

/**
 * Created by BB on 07/01/2018.
 */

public class MapNodeComponent extends Component {

    private enum Type {
        BATTLE, SHOP, BOSS
    }

    private Type type;

    public MapNodeComponent(){
        type = Type.BATTLE;
    }


    public Type getType() {
        return type;
    }
}
