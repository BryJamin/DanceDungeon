package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by BB on 15/02/2018.
 */

public class StageUIRenderingSystem extends BaseSystem {

    public Stage stage;
    public Table container;

    public StageUIRenderingSystem(Stage stage){
        this.stage = stage;
        container = new Table();
        container.setDebug(true);
        container.setFillParent(true);
    }


    @Override
    protected void processSystem() {
        stage.act(world.delta);
        stage.draw();
    }


}
