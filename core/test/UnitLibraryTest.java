import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;

import org.junit.Test;

public class UnitLibraryTest extends GameTest {

    @Test
    public void testLoadingLibraryData() throws Exception {
        SkillLibrary.loadFromJSON();
        UnitLibrary.loadFromJSON();
    }



    @Test
    public void getEnemiesFromLoadedData() throws Exception {

        SkillLibrary.loadFromJSON();
        UnitLibrary.loadFromJSON();

        for(String s : UnitLibrary.getUnitIdList()){
            UnitLibrary.getUnitData(s);
        }
    }


    @Test
    public void verifyUnitIconsExist() throws Exception {

        SkillLibrary.loadFromJSON();
        UnitLibrary.loadFromJSON();


        for(String s : UnitLibrary.getUnitIdList()){
            UnitLibrary.getUnitData(s);
        }

    }



}

