import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.enemy.EnemyLibrary;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import org.junit.Assert;
import org.junit.Test;

public class EnemyLibraryTest extends GameTest {

    @Test
    public void testLoadingLibraryData() throws Exception {
        EnemyLibrary.loadFromJSON();
    }



    @Test
    public void getEnemiesFromLoadedData() throws Exception {

        EnemyLibrary.loadFromJSON();

        String[] enemyIds = {
                EnemyLibrary.RANGED_BLASTER,
                EnemyLibrary.MELEE_BLOB
        };

        for(String s : enemyIds){
            EnemyLibrary.getUnitData(s);
        }
    }



}

