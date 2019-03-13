package com.cesoft.cesdoom.managers


import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.map.Point
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object RampMapFactory {
    private val tag: String = WallMapFactory::class.java.simpleName

    fun create(mapFactory: MapGraphFactory, pos: Vector3,
                          angleX: Float=0f, angleY: Float=0f, angleZ: Float=0f) {

        val l = (RampFactory.LONG  / mapFactory.scale).toInt()
        val h = (RampFactory.HIGH  / mapFactory.scale).toInt() +1
        val level = if(pos.y > 2*WallFactory.HIGH) 1 else 0//TODO: more levels ?
        val posMap = mapFactory.toMapGraphCoord(level, Vector2(pos.x, pos.z))

        // Sube hacia la derecha  (+X)
        if(angleX == 90f && angleY == -45f && angleZ == 0f) {
            mapFactory.addFloorAccess(level, pos.x+RampFactory.LONG-mapFactory.scale, pos.z)
            mapFactory.addFloorAccess(level+1, pos.x, pos.z)
            for(x_ in -l..+l) {
                for(y_ in 0..1) {
                    mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + h-y_))
                    mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y - h+y_))

                    //TODO:Enhance 3D pathfinding !!!
                    mapFactory.addCollider(level+1, Point(posMap.x + x_, posMap.y + h-y_))
                    mapFactory.addCollider(level+1, Point(posMap.x + x_, posMap.y - h+y_))
                }
            }
            for(y_ in -h..+h) {
                mapFactory.addCollider(level, Point(posMap.x + l, posMap.y + y_))
                //TODO:Enhance 3D pathfinding !!!
                //if(y_ < -1 || y_ > +1)
                //    mapFactory.addCollider(level + 1, Point(posMap.x + l, posMap.y + y_))
                mapFactory.addCollider(level + 1, Point(posMap.x - l, posMap.y + y_))
            }
        }
        // Sube hacia la izquierda (-X)
        else if(angleX == 90f && angleY == +45f && angleZ == 0f) {
            mapFactory.addFloorAccess(level, pos.x-RampFactory.LONG+mapFactory.scale, pos.z)
            mapFactory.addFloorAccess(level+1, pos.x, pos.z)
            for(x_ in -l..+l) {
                for(y_ in 0..1) {
                    mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + h-y_))
                    mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y - h+y_))

                    //TODO:Enhance 3D pathfinding !!!
                    mapFactory.addCollider(level+1, Point(posMap.x + x_, posMap.y + h-y_))
                    mapFactory.addCollider(level+1, Point(posMap.x + x_, posMap.y - h+y_))
                }
            }
            for(y_ in -h..+h) {
                mapFactory.addCollider(level, Point(posMap.x - l, posMap.y + y_))
                //TODO:Enhance 3D pathfinding !!!
                //if(y_ != 0)
                //    mapFactory.addCollider(level + 1, Point(posMap.x - l, posMap.y + y_))
                mapFactory.addCollider(level + 1, Point(posMap.x + l, posMap.y + y_))
            }
        }
        // Sube hacia la adelante (-Z)
        else if(angleX == +45f && angleY == 0f && angleZ == 90f) {
            mapFactory.addFloorAccess(level, pos.x, pos.z-RampFactory.LONG+mapFactory.scale)
            mapFactory.addFloorAccess(level+1, pos.x, pos.z)
            for(y_ in -l..+l) {
                for(x_ in 0..1) {
                    mapFactory.addCollider(level, Point(posMap.x + h-x_, posMap.y + y_))
                    mapFactory.addCollider(level, Point(posMap.x - h+x_, posMap.y + y_))
                }
            }
            for(x_ in -h..+h) {
                mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y - l))
                mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y - l-1))
            }
        }
        // Sube hacia la atras (+Z)
        else if(angleX == -45f && angleY == 0f && angleZ == 90f) {
            mapFactory.addFloorAccess(level, pos.x, pos.z+RampFactory.LONG-mapFactory.scale)
            mapFactory.addFloorAccess(level+1, pos.x, pos.z)
            for(y_ in -l..+l) {
                for(x_ in 0..1) {
                    mapFactory.addCollider(level, Point(posMap.x + h-x_, posMap.y + y_))
                    mapFactory.addCollider(level, Point(posMap.x - h+x_, posMap.y + y_))
                }
            }
            for(x_ in -h..+h) {
                mapFactory.addCollider(level, Point(posMap.x + x_, posMap.y + l))
            }
        }
    }
}