import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.factories.map.event.EventManager;

import org.junit.Assert;
import org.junit.Test;

public class EventManagerTest extends GameTest {


    @Test
    public void testEventCount() throws Exception {

        EventManager eventManager = new EventManager();
        Assert.assertTrue("Number of events does not match the number of keys, Possible Duplicate Keys", eventManager.getKeys().size == eventManager.getEventCount());
    }

}
