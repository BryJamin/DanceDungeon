package com.bryjamin.dancedungeon.ecs.components.identifiers;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 29/10/2017.
 */

public class ParentComponent extends Component {

    public Array<ChildComponent> children = new Array<ChildComponent>();

    public ParentComponent(){}

    public ParentComponent(ChildComponent... children){
        this.children.addAll(children);
    }

}
