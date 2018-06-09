import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by BB on 21/12/2017.
 */

public class CoordinateMathTest {


    @Test
    public void jsonStuff(){

        Json json = new Json();

        System.out.println(json.prettyPrint(new BattleEvent()));




    }



    @Test
    public void coordinateMathInRangeTest() throws Exception {

        Assert.assertTrue(CoordinateMath.isWithinRange(new Coordinates(0,0), new Coordinates(1,0), 1));
        Assert.assertTrue(CoordinateMath.isWithinRange(new Coordinates(0,0), new Coordinates(2,0), 2));
        Assert.assertTrue(CoordinateMath.isWithinRange(new Coordinates(0,0), new Coordinates(-3,-3), 6));
    }


}
