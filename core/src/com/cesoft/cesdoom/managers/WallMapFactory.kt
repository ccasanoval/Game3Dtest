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

    fun create(mapFactory: MapGraphFactory, pos: Vector3, angle: Float, ignore: Int) {
        val t = (WallFactory.THICK / mapFactory.scale).toInt()+1
        val l = (WallFactory.LONG  / mapFactory.scale).toInt()+2
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
        }
    }
}