package com.bryjamin.dancedungeon.ecs.components.identifiers;

import com.artemis.Component;
import com.bryjamin.dancedungeon.ecs.systems.battle.DisplayEnemyIntentUISystem;

/**
 * Created by BB on 06/03/2018.
 *
 *
 *
 * Used as an identifier to show which highlighted squares on the map are being used to highlight
 * an 'intent' of the unit.
 *
 *
 * This is mainly used to easily clear out these UI entities from the map in the {@link DisplayEnemyIntentUISystem}.
 */

public class EnemyIntentUIComponent extends Component {
}
