import com.bryjamin.dancedungeon.factories.map.MapGenerator;

import org.junit.Test;

/**
 * Created by BB on 17/01/2018.
 */

public class MapGeneratorTest extends GameTest {


    @Test
    public void simpleMapGenerationTest() throws Exception {

        int generations = 20000;

        MapGenerator mapGenerator = new MapGenerator();

        for(int i = 0; i < generations; i++){
            mapGenerator.generateGameMap();
        }

    }


}

