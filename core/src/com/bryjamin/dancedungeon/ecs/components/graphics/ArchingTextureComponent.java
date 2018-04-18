package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Used for skills that go over objects. To give the illusion up and then back down.
 *
 * Takes in a starting point and endpoint and uses the distance from the stand the end
 *
 * to change the Texture
 *
 */
public class ArchingTextureComponent extends Component {

    public Vector3 startPos;
    public Vector3 endPos;

    public float mixScaleY;
    public float maxScaleY;

    public float minScaleX;
    public float maxScaleX;

    public ArchingTextureComponent(){}


    public ArchingTextureComponent(Vector3 startPos, Vector3 endPos, float mixScaleY, float maxScaleY, float minScaleX, float maxScaleX) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.mixScaleY = mixScaleY;
        this.maxScaleY = maxScaleY;
        this.minScaleX = minScaleX;
        this.maxScaleX = maxScaleX;
    }


    public ArchingTextureComponent(Vector3 startPos, Vector3 endPos, float minScale, float maxScale) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.mixScaleY = minScale;
        this.maxScaleY = maxScale;
        this.minScaleX = minScale;
        this.maxScaleX = maxScale;
    }

}
