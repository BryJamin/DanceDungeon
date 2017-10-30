package com.bryjamin.dancedungeon.ecs.components.identifiers;

import com.artemis.Component;

/**
 * Created by BB on 29/10/2017.
 */

public class ChildComponent extends Component{


    public ParentComponent parent;

    public ChildComponent(){}

    public ChildComponent(ParentComponent parent){
        this.parent = parent;
        parent.children.add(this);
    }


}
