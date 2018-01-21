import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.factories.map.MapNode;
import com.bryjamin.dancedungeon.factories.map.event.MapEvent;
import com.bryjamin.dancedungeon.factories.map.event.MapSection;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by BB on 17/01/2018.
 */

public class MapGeneratorTest extends GameTest {


    @Test
    public void simpleMapGenerationTest() throws Exception {

        int generations = 100000;

        MapGenerator mapGenerator = new MapGenerator();

        for(int i = 0; i < generations; i++){
            mapGenerator.generateGameMap();
        }

    }


    @Test
    public void testMapGenerationSpacingOfEvents() throws Exception {

        int generations = 100000;

        MapGenerator mapGenerator = new MapGenerator();

        for(int i = 0; i < generations; i++){
            GameMap gameMap = mapGenerator.generateGameMap();

            MapSection firstSection = gameMap.getMapNodeSections().first();

            System.out.println(i);

            for(MapNode mapNode : firstSection.getMapNodes()) {
                Assert.assertTrue(noNeighbouringSpecialEvents(mapNode));
            }


        }

    }



    private boolean noNeighbouringSpecialEvents(MapNode node){

        if(node.getSuccessors().size == 0) return true;

        for(MapNode child : node.getSuccessors()){
            if(child.getEventType() != MapEvent.EventType.BATTLE){
                if(child.getEventType() == node.getEventType()){
                    System.out.println("FAIL: Child:" + child.getEventType() + ", Node: " + node.getEventType());
                    return false;
                } else {
                    if(!noNeighbouringSpecialEvents(child)){
                        return false;
                    }
                }

            }
        }

        return true;

    }


}

