package com.cesoft.cesdoom.managers


import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.map.Point


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object RampMapFactory {
    private val tag: String = WallMapFactory::class.java.simpleName

    //TODO:Enhance 3D pathfinding (no acces A, but with direction A0-1 A1-0 A1-2 A2-1 A2-0 ...) !!! Constructor!!!!
    fun create(mapFactory: MapGraphFactory, pos: Vector3,
                          angleX: Float=0f, angleY: Float=0f, angleZ: Float=0f) {

        val l = (RampFactory.LONG  / mapFactory.scale).toInt()
        val h = (RampFactory.HIGH  / mapFactory.scale).toInt() +1
        val floor = when {
            pos.y < 2*WallFactory.HIGH -> 0
            pos.y < 4*WallFactory.HIGH -> 1
            else -> 2
        }
        val posMap = mapFactory.toMapGraphCoord(floor, Vector2(pos.x, pos.z))

        // Sube hacia la derecha  (+X)
        if(angleX == 90f && angleY == -45f && angleZ == 0f) {

            if(floor < 1)
            mapFactory.addFloorAccess(floor, pos.x+RampFactory.LONG-mapFactory.scale, pos.z)
            mapFactory.addFloorAccess(floor+1, pos.x-l, pos.z)
            val mapPos = mapFactory.toMapGraphCoord(floor+1,pos.x-l, pos.z)
            for(i in 1..7) mapFactory.addWay(floor+1, Point(mapPos.x+i, mapPos.y))

            for(x_ in -l..+l) {
                for(y_ in 0..1) {
                    mapFactory.addCollider(floor, Point(posMap.x+x_, posMap.y+h-y_))
                    mapFactory.addCollider(floor, Point(posMap.x+x_, posMap.y-h+y_))
                    mapFactory.addCollider(floor+1, Point(posMap.x+x_, posMap.y+h-y_))
                    mapFactory.addCollider(floor+1, Point(posMap.x+x_, posMap.y-h+y_))
                }
            }
            for(y_ in -h..+h) {
                mapFactory.addCollider(floor, Point(posMap.x+l, posMap.y+y_))
                //mapFactory.addCollider(floor, Point(posMap.x+l+1, posMap.y+y_))
                mapFactory.addCollider(floor+1, Point(posMap.x-l, posMap.y+y_))
            }
        }
        // Sube hacia la izquierda (-X)
        else if(angleX == 90f && angleY == +45f && angleZ == 0f) {

            if(floor < 1)
            mapFactory.addFloorAccess(floor, pos.x-RampFactory.LONG+mapFactory.scale, pos.z)
            mapFactory.addFloorAccess(floor+1, pos.x+l, pos.z)
            val mapPos = mapFactory.toMapGraphCoord(floor+1,pos.x+l, pos.z)
            for(i in 1..7) mapFactory.addWay(floor+1, Point(mapPos.x-i, mapPos.y))

            for(x_ in -l..+l) {
                for(y_ in 0..1) {
                    mapFactory.addCollider(floor, Point(posMap.x+x_, posMap.y+h-y_))
                    mapFactory.addCollider(floor, Point(posMap.x+x_, posMap.y-h+y_))
                    mapFactory.addCollider(floor+1, Point(posMap.x+x_, posMap.y+h-y_))
                    mapFactory.addCollider(floor+1, Point(posMap.x+x_, posMap.y-h+y_))
                }
            }
            for(y_ in -h..+h) {
                mapFactory.addCollider(floor, Point(posMap.x-l, posMap.y+y_))
                //mapFactory.addCollider(floor, Point(posMap.x-l-1, posMap.y+y_))
                mapFactory.addCollider(floor+1, Point(posMap.x+l, posMap.y+y_))
            }
        }
        // Sube hacia la adelante (-Z)
        else if(angleX == +45f && angleY == 0f && angleZ == 90f) {

            if(floor < 1)
            mapFactory.addFloorAccess(floor, pos.x, pos.z-RampFactory.LONG+mapFactory.scale)
            mapFactory.addFloorAccess(floor+1, pos.x, pos.z+l)
            val mapPos = mapFactory.toMapGraphCoord(floor+1, pos.x, pos.z+l)
            for(i in 1..7) mapFactory.addWay(floor+1, Point(mapPos.x, mapPos.y-i))

            for(y_ in -l..+l) {
                for(x_ in 0..1) {
                    mapFactory.addCollider(floor, Point(posMap.x + h-x_, posMap.y + y_))
                    mapFactory.addCollider(floor, Point(posMap.x - h+x_, posMap.y + y_))
                }
            }
            for(x_ in -h..+h) {
                mapFactory.addCollider(floor, Point(posMap.x + x_, posMap.y - l))
                mapFactory.addCollider(floor, Point(posMap.x + x_, posMap.y - l-1))
            }
        }
        // Sube hacia la atras (+Z)
        else if(angleX == -45f && angleY == 0f && angleZ == 90f) {

            if(floor < 1)
            mapFactory.addFloorAccess(floor, pos.x, pos.z+RampFactory.LONG-mapFactory.scale)
            mapFactory.addFloorAccess(floor+1, pos.x, pos.z-l)
            val mapPos = mapFactory.toMapGraphCoord(floor+1, pos.x, pos.z-l)
            for(i in 1..7) mapFactory.addWay(floor+1, Point(mapPos.x, mapPos.y+i))

            for(y_ in -l..+l) {
                for(x_ in 0..1) {
                    mapFactory.addCollider(floor, Point(posMap.x + h-x_, posMap.y + y_))
                    mapFactory.addCollider(floor, Point(posMap.x - h+x_, posMap.y + y_))
                }
            }
            for(x_ in -h..+h) {
                mapFactory.addCollider(floor, Point(posMap.x + x_, posMap.y + l))
            }
        }
    }

    fun addWays(mapFactory: MapGraphFactory, size: Vector2, pos: Vector3) {
        val floor = (pos.y / (2*WallFactory.HIGH)).toInt()
        val offset = 0//1
        val cx = (size.x / mapFactory.scale / 2).toInt() + offset
        val cy = (size.y / mapFactory.scale / 2).toInt() + offset
        val posMap = mapFactory.toMapGraphCoord(floor, Vector2(pos.x, pos.z))
        for(x in -cx..+cx) {
            for(y in -cy..+cy) {
                val point = Point(posMap.x + x, posMap.y + y)
                mapFactory.addWay(floor, point)
            }
        }
    }
}