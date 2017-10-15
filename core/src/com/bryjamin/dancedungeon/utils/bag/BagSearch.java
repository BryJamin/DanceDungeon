package com.bryjamin.dancedungeon.utils.bag;

import com.artemis.utils.Bag;

/**
 * Created by BB on 15/10/2017.
 */

public class BagSearch {

    /**
     * Checks to see if a bag contains a class.
     * @param cls - Class
     * @param bag - Bag
     * @param <T> - Class of class
     * @param <B> - Class of Objects within Bag
     * @return - True if the bag has an object of type class
     */
    public static <T, B> boolean contains(Class<T> cls, Bag<B> bag){
        for(B ok : bag){
            if(ok.getClass() == cls){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an object from a bag that has the same class as T
     * @param cls - Class
     * @param bag - Bag
     * @param <T> - Class of class
     * @param <B> - Class of Objects within Bag
     * @return - Returns object of type class if contained within bag. Otherwise returns null
     */
    public static <T, B> T getObjectOfTypeClass(Class<T> cls, Bag<B> bag){
        for(B ok : bag){
            if(ok.getClass() == cls){
                return cls.cast(ok);
            }
        }
        return null;
    }

    public static <T, B> void removeObjectOfTypeClass(Class<T> cls, Bag<B> bag){
        for(B ok : bag){
            if(ok.getClass() == cls){
                bag.remove(ok);
            }
        }
    }

}
