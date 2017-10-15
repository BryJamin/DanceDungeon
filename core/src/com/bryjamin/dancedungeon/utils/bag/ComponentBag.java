package com.bryjamin.dancedungeon.utils.bag;

import com.artemis.Component;
import com.artemis.utils.Bag;

/**
 * Created by BB on 15/10/2017.
 */

public class ComponentBag extends Bag<Component> {

    /**
     * The componenet Bag checks to see if you are adding duplicate components.
     * @param component - Component to be added
     */
    @Override
    public void add(Component component) {

        try {
            if(BagSearch.contains(component.getClass(), this)) {

                BagSearch.removeObjectOfTypeClass(component.getClass(), this);
                super.add(component);
                throw new Exception("Class " + component.getClass().toString() + " already contained inside bag");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        super.add(component);
    }
}
