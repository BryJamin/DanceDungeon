package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 16/10/2017.
 */

public class DispellableComponent extends Component {


    public enum Type {
        VERTICAL, HORIZONTAL, FRONT_SLASH, BACK_SLASH
    }

    public Array<Type> dispelArray = new Array<Type>();


    public int maxSize;

    public DispellableComponent(){
        dispelArray.add(Type.HORIZONTAL);
    }

    public DispellableComponent(int maxSize){
        this.maxSize = maxSize;
        dispelArray.add(Type.HORIZONTAL);
    }



}
