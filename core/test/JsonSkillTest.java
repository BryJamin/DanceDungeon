import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;

import org.junit.Test;

public class JsonSkillTest  extends GameTest {


    @Test
    public void testEventCount() throws Exception {

        ObjectMap<String, Skill> map = new ObjectMap<String, Skill>();


        UnitMap unitMap = new UnitMap();

        CharacterGenerator cg = new CharacterGenerator();

        UnitData ud = cg.createArcher();


        Array<String> s = new Array<>();
        s.addAll("1", "2", "3");

        //ud.setSkillIds(s);

        Json json = new Json();

        System.out.println(json.prettyPrint(ud));


        SkillLibrary skillLibrary = new SkillLibrary();

        System.out.println(skillLibrary.getItems().toString());

       // EventManager eventManager = new EventManager();

       // Assert.assertTrue("Number of events does not match the number of keys, Possible Duplicate Keys", eventManager.getKeys().size == eventManager.getEventCount());
    }

}
