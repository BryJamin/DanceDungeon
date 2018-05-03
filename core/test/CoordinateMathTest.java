import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by BB on 21/12/2017.
 */

public class CoordinateMathTest extends GameTest {


    @Test
    public void coordinateMathInRangeTest() throws Exception {

        Assert.assertTrue(CoordinateMath.isWithinRange(new Coordinates(0,0), new Coordinates(1,0), 1));
        Assert.assertTrue(CoordinateMath.isWithinRange(new Coordinates(0,0), new Coordinates(2,0), 2));
        Assert.assertTrue(CoordinateMath.isWithinRange(new Coordinates(0,0), new Coordinates(-3,-3), 6));
    }


    @Test
    public void intersectTest() throws Exception {

        Queue<Integer> test = new Queue<Integer>();
        test.addFirst(1);
        test.addFirst(1);
        test.addFirst(5);
        test.addFirst(1);
        test.addFirst(5);
        test.addFirst(1);
        test.addFirst(5);

        for(Integer i : test){
            test.removeValue(1, true);
        }

        for(Integer i : test){
            System.out.println(i);
        }

    }






}
