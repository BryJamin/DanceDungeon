import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.factories.unit.UnitLibrary;
import com.bryjamin.dancedungeon.factories.unit.UnitData;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;

import org.junit.Assert;
import org.junit.Test;

public class UnitLibraryTest  extends GameTest {

    @Test
    public void testLoadingLibraryData() {
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

        TextureAtlas textureAtlas = new TextureAtlas(FileStrings.SPRITE_ATLAS_FILE);

        Array<String> errors = new Array<>();


        for(String s : UnitLibrary.getUnitIdList()){

            UnitData unitData = UnitLibrary.getUnitData(s);

            if(textureAtlas.findRegion(unitData.icon) == null){
                errors.add("Icon: " + unitData.icon + " not found. \nUnit name: " + unitData.getName()  + "\nUnit ID: " + unitData.getId());
            };

        }

        for(String s : errors){
            System.out.println(s);
        }

        Assert.assertTrue(errors.size == 0);

    }



}

