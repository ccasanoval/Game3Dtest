package com.cesoft.cesdoom.managers

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.map.Point
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object WallMapFactory {
    private val tag: String = WallMapFactory::class.java.simpleName

    /*fun create(mapFactory: MapGraphFactory, pos: Vector3, angle: Float) {
        val t = ((WallFactory.THICK+mapFactory.scale/2) / mapFactory.scale).toInt()
        val l = ((WallFactory.LONG+mapFactory.scale/2) / mapFactory.scale).toInt()
        val td = ((WallFactory.THICK+mapFactory.scale/2) / mapFactory.scale).toInt()+0
        val ld = (0.7071*WallFactory.LONG / mapFactory.scale).toInt()+0
        val level = if(pos.y > 2*WallFactory.HIGH-1) 1 else 0//TODO: more levels ?
        val posMap = mapFactory.toMapGraphCoord(level, Vector2(pos.x, pos.z))

        when(angle) {
            +00f -> { //--- Vertical
                for(x_ in -t..t)
                    for(y_ in -l..l)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + y_))
            }
            +90f -> {//--- Horizontal
                for(y_ in -t..t)
                    for(x_ in -l..l)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + y_))
            }
            +45f -> {//--- Diagonal 1
                for(x_ in -ld..ld)
                    for(y_ in -td..td)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + x_ + y_))
            }
            -45f -> {//--- Diagonal 2
                for(x_ in -ld..ld)
                    for(y_ in -td..td)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y - x_ + y_))
            }
        }
    }*/

    fun createLongWall(mapFactory: MapGraphFactory, size: Vector3, pos: Vector3, angle: Float) {

        var cx = ((size.x/2) / mapFactory.scale).toInt() +1
        var cy = ((size.z/2) / mapFactory.scale).toInt() +1
        if(cx < 1)cx=1
        if(cy < 1)cy=1

        val length = (if(size.x > size.z) size.x else size.z)/2
        val thick = (if(size.x > size.z) size.z else size.x)
        val length45 = (0.7071*length / mapFactory.scale).toInt()
        var thick45 = (0.7071*thick / mapFactory.scale).toInt()
        if(thick45 < 1)thick45=1

        val level = if(pos.y > size.y-1) 1 else 0//TODO: more levels ?
        Log.e(tag, "---------------------------------------------------------level=$level")

        val posMap = mapFactory.toMapGraphCoord(level, Vector2(pos.x, pos.z))

        when(angle) {
            +0f, +180f -> {//--- Horizontal
                for(x_ in -cx..cx)
                    for(y_ in -cy..cy)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + y_))
            }
            +90f, -90f -> { //--- Vertical
                for(y_ in -cx..cx)
                    for(x_ in -cy..cy)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + y_))
            }
            +45f -> {//--- Diagonal 1
                Log.e(tag, "--------- DIAGONAL 45 ------------ thick45=$thick45 length45=$length45 ")
                for(t_ in -thick45..thick45)
                    for(xy_ in -length45..length45)
                        mapFactory.addCollider(level, Point(posMap.x + xy_ + t_, posMap.y + xy_))
            }
            -45f -> {//--- Diagonal 2
                for(t_ in -thick45..thick45)
                    for(xy_ in -length45..length45)
                        mapFactory.addCollider(level, Point(posMap.x + xy_ + t_, posMap.y - xy_))
            }
        }


        /*


        val t = ((WallFactory.THICK+mapFactory.scale) / mapFactory.scale).toInt()
        val l = ((size.x+mapFactory.scale)/2 / mapFactory.scale).toInt()
        val td = (WallFactory.THICK/2 / mapFactory.scale).toInt()
        val ld = (0.7071*size.x / mapFactory.scale).toInt()
        val level = if(pos.y > 2*size.y-1) 1 else 0//TODO: more levels ?
        val posMap = mapFactory.toMapGraphCoord(level, Vector2(pos.x, pos.z))

        when(angle) {
            +90f, -90f -> { //--- Vertical
                for(x_ in -t..t)
                    for(y_ in -l..l)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + y_))
            }
            +0f, +180f -> {//--- Horizontal
                for(y_ in -t..t)
                    for(x_ in -l..l)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + y_))
            }
            +45f -> {//--- Diagonal 1
                for(x_ in -ld..ld)
                    for(y_ in -td..td)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + x_ + y_))
            }
            -45f -> {//--- Diagonal 2
                for(x_ in -ld..ld)
                    for(y_ in -td..td)
                        mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y - x_ + y_))
            }
        }*/
    }
}