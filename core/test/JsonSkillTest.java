import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import org.junit.Test;

public class JsonSkillTest  extends GameTest {


    @Test
    public void testEventCount() throws Exception {

        ObjectMap<String, Skill> map = new ObjectMap<String, Skill>();
/*

        map.put("dadawdw", new Skill(new Skill.Builder()
                .name("Foredsdssight")
                .icon("skills/Fire")
                .description("Increase the Dodge chance of an self by 20% for three turns")
                .targeting(Skill.Targeting.Self)
                .actionType(Skill.ActionType.UsesMoveAndAttackAction)
                .spellAnimation(Skill.SpellAnimation.Glitter)
                .spellEffects(Skill.SpellEffect.Dodge.value(0.2f).duration(3))));

        Skill s = new Skill();
        s.affectedAreas = new Coordinates[]{new Coordinates(0, 1), new Coordinates(1, 0), new Coordinates(0, -1), new Coordinates(-1, 0)};
        s.affectedAreaSkill = new Skill(new Skill.Builder()
                .name("Fodsdsresight")
                .icon("skills/Fire")
                .description("Increase the Dodge chance of an self by 20% for three turns")
                .targeting(Skill.Targeting.Self)
                .actionType(Skill.ActionType.UsesMoveAndAttackAction));
*/

        //map.put("ODOADOAWDAWDAW", s);

                Json json = new Json();

        System.out.println(json.prettyPrint(map));


        SkillLibrary skillLibrary = new SkillLibrary();

        System.out.println(skillLibrary.getItems().toString());

       // EventManager eventManager = new EventManager();

       // Assert.assertTrue("Number of events does not match the number of keys, Possible Duplicate Keys", eventManager.getKeys().size == eventManager.getEventCount());
    }

}
