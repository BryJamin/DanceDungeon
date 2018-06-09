import com.bryjamin.dancedungeon.factories.map.event.EventLibrary;

import org.junit.Assert;
import org.junit.Test;

public class EventLibraryTest extends GameTest {


    @Test
    public void testEventCount() throws Exception {

        EventLibrary eventLibrary = new EventLibrary();
        Assert.assertTrue("Number of events does not match the number of keys, Possible Duplicate Keys", eventLibrary.getKeys().size == eventLibrary.getEventCount());
    }

}
