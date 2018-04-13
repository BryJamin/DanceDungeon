package com.bryjamin.dancedungeon.utils.mapping;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.math.Coordinates;


/**
 * Classes used to the store the co-ordinates of all tiles featured within a maps I've created.
 */
public class MapReader {


    private Array<Coordinates> wallTiles = new Array<Coordinates>();
    private Array<Coordinates> allyTiles = new Array<Coordinates>();

    private Array<Coordinates> allyDeploymentTiles = new Array<Coordinates>();
    private Array<Coordinates> enemyDeploymentTiles = new Array<Coordinates>();


    public MapReader(TiledMap map){

    }



}
