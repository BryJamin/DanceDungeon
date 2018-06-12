import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.factories.map.MapNode;
import com.bryjamin.dancedungeon.factories.map.event.EventLibrary;
import com.bryjamin.dancedungeon.factories.map.event.MapEvent;
import com.bryjamin.dancedungeon.factories.map.event.MapSection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by BB on 17/01/2018.
 */

public class MapGeneratorTest {


    @Test
    public void simpleMapGenerationTest() throws Exception {

        EventLibrary.loadFromJSON();

        int generations = 100000;
        float failCount = 0;

        for(int i = 0; i < generations; i++){
            try {
                MapGenerator mapGenerator = new MapGenerator();
                mapGenerator.generateGameMap();

            } catch(Exception e) {
                e.printStackTrace();
                failCount++;
            }
            //System.out.println(i);
        }

        System.out.println("Number of Generations: " + generations);
        System.out.println("Percentage Fail: " + (failCount / generations) * 100 + "%");

        Assert.assertTrue(failCount == 0);

    }

}

