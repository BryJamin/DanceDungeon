import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.bryjamin.dancedungeon.factories.map.event.EventManager;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.factories.spells.basic.HeavyStrike;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class JsonSkillTest  extends GameTest {


    @Test
    public void testEventCount() throws Exception {

        ObjectMap<String, Skill> map = new ObjectMap<String, Skill>();

        map.put("dadawdw", new Skill(new Skill.Builder()));
        map.put("dadawfesfesfdw", new Skill(new Skill.Builder()));

        Json json = new Json();

        System.out.println(json.prettyPrint(map));


        SkillLibrary skillLibrary = new SkillLibrary();

        System.out.println(skillLibrary.getItems().toString());

       // EventManager eventManager = new EventManager();

       // Assert.assertTrue("Number of events does not match the number of keys, Possible Duplicate Keys", eventManager.getKeys().size == eventManager.getEventCount());
    }

}
