package com.bryjamin.dancedungeon.factories;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.factories.spells.basic.DodgeUp;
import com.bryjamin.dancedungeon.factories.spells.basic.FireWeapon;
import com.bryjamin.dancedungeon.factories.spells.basic.StunStrike;
import com.bryjamin.dancedungeon.factories.spells.restorative.Heal;
import com.bryjamin.dancedungeon.utils.BaseStatStatics;

/**
 * Created by BB on 15/02/2018.
 */

public class CharacterGenerator {


    public UnitData createWarrior(){
        UnitData warrior = new UnitData(UnitMap.UNIT_WARRIOR);
        warrior.icon = TextureStrings.CLASS_WARRIOR;
        warrior.setStatComponent(new StatComponent.StatBuilder()
                .movementRange(BaseStatStatics.BASE_MOVEMENT)
                .attackRange(3)
                .attack(5)
                .healthAndMax(15).build());

        warrior.setSkillsComponent(new SkillsComponent(
                new FireWeapon()));

        return warrior;
    }

    public UnitData createMage(){
        UnitData mage = new UnitData(UnitMap.UNIT_MAGE);
        mage.icon = TextureStrings.CLASS_MAGE;
        mage.setStatComponent(
                new StatComponent.StatBuilder()
                        .movementRange(BaseStatStatics.BASE_MOVEMENT )
                        .healthAndMax(20)
                        .attackRange(6)
                        .attack(7).build());

        mage.setSkillsComponent(
                new SkillsComponent(
                        new FireWeapon(),
                        new DodgeUp(),
                        new Heal(),
                        new StunStrike()
                ));

        return mage;
    }


}
