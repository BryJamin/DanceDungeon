package com.bryjamin.dancedungeon.utils.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Used To Quickly Make An Are You Sure Frame.
 */
public class AreYouSureFrame extends Table{

    private ChangeListener yesListener;
    private ChangeListener noListener;

    private String[] text;
    private Skin uiSkin;


    public AreYouSureFrame(ChangeListener yesListener, ChangeListener noListener, Skin uiSkin, String... text){
        super(uiSkin);
        this.yesListener = yesListener;
        this.noListener = noListener;
        this.text = text;
        this.uiSkin = uiSkin;
    }



    public void update(){


        if(this.hasChildren()){
            this.reset();
        }

        this.setVisible(true);

        for(String s : text) {
            this.add(new Label(s, uiSkin)).colspan(2).padBottom(Padding.MEDIUM);
            this.row();
        }

        TextButton yes = new TextButton(TextResource.CONFIRM_YES, uiSkin);
        yes.addListener(yesListener);

        TextButton no = new TextButton(TextResource.CONFIRM_NO, uiSkin);
        no.addListener(noListener);

        this.add(yes).width(Measure.units(20f)).height(Measure.units(5f)).expandX();
        this.add(no).width(Measure.units(20f)).height(Measure.units(5f)).expandX();

    }





}
