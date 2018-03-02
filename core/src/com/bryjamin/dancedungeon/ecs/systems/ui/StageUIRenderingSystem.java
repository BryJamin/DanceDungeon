package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by BB on 15/02/2018.
 */

public class StageUIRenderingSystem extends BaseSystem {

    public Stage stage;
    public Skin uiSkin;

    public StageUIRenderingSystem(Stage stage){
        this.stage = stage;
    }


    @Override
    protected void processSystem() {
        stage.act(world.delta);
        stage.draw();
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    public Table createContainerTable(){
        Table table = new Table();
        table.setWidth(stage.getWidth());
        table.setHeight(stage.getHeight());
        table.setDebug(true);
        return table;
    }









}
