import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SkillLibraryTest extends GameTest {


    @Test
    public void testLoadingLibraryData() throws Exception {
        SkillLibrary.loadFromJSON();
    }



    @Test
    public void getSkillsFromLoadedData() throws Exception {

        SkillLibrary.loadFromJSON();

        for(String s : SkillLibrary.getSkillIDList()){
            SkillLibrary.getSkill(s);
        }
    }


    @Test
    public void verifySkillIconsExist() throws Exception {

        SkillLibrary.loadFromJSON();

        TextureAtlas textureAtlas = new TextureAtlas(FileStrings.SPRITE_ATLAS_FILE);

        Array<String> errors = new Array<>();

        for(String s : SkillLibrary.getSkillIDList()){

            Skill skill = SkillLibrary.getSkill(s);

            if(textureAtlas.findRegion(skill.getIcon()) == null){
                errors.add("Icon: " + skill.getIcon() + "not found. \nSkill name: " + skill.getName());
            };

        }

        for(String s : errors){
            System.out.println(s);
        }

        Assert.assertTrue(errors.size == 0);

    }

}
