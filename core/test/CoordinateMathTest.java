import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
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


        Vector2 vector2 = new Vector2();
        System.out.println(Intersector.intersectSegments(0, -2, 0,-1, 0,0, 1, 0, vector2));

        System.out.println(vector2);






    }






}
