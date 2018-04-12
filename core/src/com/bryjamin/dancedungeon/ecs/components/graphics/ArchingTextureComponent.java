package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
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

    public float minHeight;
    public float maxHeight;

    public float minWidth;
    public float maxWidth;

    public ArchingTextureComponent(){}


    public ArchingTextureComponent(Vector3 startPos, Vector3 endPos, float minHeight, float maxHeight, float minWidth, float maxWidth) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
    }


    public ArchingTextureComponent(Vector3 startPos, Vector3 endPos, float minSize, float maxSize) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.minHeight = minSize;
        this.maxHeight = maxSize;
        this.minWidth = minSize;
        this.maxWidth = maxSize;
    }

}
