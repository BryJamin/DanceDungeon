import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;

import org.junit.Test;

public class UnitLibraryTest extends GameTest {

    @Test
    public void testLoadingLibraryData() throws Exception {
        UnitLibrary.loadFromJSON();
    }



    @Test
    public void getEnemiesFromLoadedData() throws Exception {

        UnitLibrary.loadFromJSON();

        String[] enemyIds = {
                UnitLibrary.RANGED_BLASTER,
                UnitLibrary.MELEE_BLOB
        };

        for(String s : enemyIds){
            UnitLibrary.getUnitData(s);
        }
    }



}

