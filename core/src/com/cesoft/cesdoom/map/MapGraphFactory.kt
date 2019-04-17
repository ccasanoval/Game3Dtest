package com.cesoft.cesdoom.map

import com.badlogic.gdx.math.Vector2
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
//TODO: si monstruo en planta distinta que player, monstruo ha de buscar en mapa de cambios de nivel para encontrar la entrada al otro mapa? (saltos or rampas)
class MapGraphFactory(val width: Float, val height: Float, val scale: Int) {

    val map = arrayListOf(
            MapGraph(0, width, height, scale),
            MapGraph(1, width, height, scale))
    private val mapData = arrayListOf(
            IntArray(map[0].cx * map[0].cy),
            IntArray(map[1].cx * map[1].cy))

    fun clear() {
        for(floor in 0 until mapData.size) {
            map[floor].clear()
            for(i in 0 until mapData[floor].size) {
                mapData[floor][i] = 0
            }
        }
    }

    //TODO: Anora solo hay accesos de 0 a 1 y de 1 a 0 (y si hay segunda planta?)
    fun addFloorAccess(floor: Int, x: Float, y: Float) {
        if(floor < map.size) {
            map[floor].addFloorAccess(Vector2(x, y))
            val pos = toMapGraphCoord(floor, Vector2(x, y))
            addWay(floor, Point(pos.x, pos.y))
            addWay(floor, Point(pos.x+1, pos.y))
            addWay(floor, Point(pos.x-1, pos.y))
            addWay(floor, Point(pos.x, pos.y+1))
            addWay(floor, Point(pos.x, pos.y-1))
        }
    }
    //
    fun toMapGraphCoord(floor: Int, x: Float, y: Float) : Point = toMapGraphCoord(floor, Vector2(x, y))
    fun toMapGraphCoord(floor: Int, pos: Vector2) : Point
            = if(floor < map.size) map[floor].toMapGraphCoord(pos)
                else Point(0,0)
    fun addCollider(floor: Int, point: Point) {
        if(floor >= map.size)return
        val index = map[floor].calcIndex(point.x, point.y)
        if(index > 0 && index < mapData[floor].size)
            mapData[floor][index] = 1
        else
            Log.e("MapGraphFactory", "addCollider:e: Negative map graph coordinates--------- ($floor, $point) => ---------- $index ------------ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }
    fun collideAll(floor: Int) {
        if(floor >= mapData.size)return
        for(index in 0 until mapData[floor].size)
            mapData[floor][index] = 1
    }
    fun addWay(floor: Int, point: Point) {
        if(floor >= map.size)return
        val index = map[floor].calcIndex(point.x, point.y)
        if(index > 0 && index < mapData[floor].size)
            mapData[floor][index] = 0
        else
            Log.e("MapGraphFactory", "addCollider:e: Negative map graph coordinates--------- ($floor, $point) => ---------- $index ------------ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }

    fun compile() {
        Log.e("MAP", "\n------------------------------------------------ compile ------------------------------------------------\n")
        for(floor in 0 until map.size) {
            for(y in 0 until map[floor].cy) {
                for(x in 0 until map[floor].cx) {
                    val index = map[floor].calcIndex(x, y)
                    val node = Node(index, Point(x, y), mapData[floor][index] == 0)
                    map[floor].addNode(node)
                    if(x - 1 >= 0 && y - 1 >= 0)
                        map[floor].connectNodes(node, map[floor].getNode(x - 1, y - 1))
                    if(x - 1 >= 0)
                        map[floor].connectNodes(node, map[floor].getNode(x - 1, y))
                    if(y - 1 >= 0)
                        map[floor].connectNodes(node, map[floor].getNode(x, y - 1))
                }
            }
        }
    }

    /*fun addFloorAccess(level: Int, point: Point) {
        map[level].addFloorAccess(Vector2(x,y))
    }*/

    /*fun clear() {
        //map.clear()
        for(i in 0 until mapData.size)
            mapData[i]=0
    }*/

    fun print() {
        for(floor in 0 until map.size) {
            val map0 = map[floor]
            Log.e("MAP", "\n------------------------------------------------ LEVEL $floor ------------------------------------------------\n")
            var col = " \t\t\t"
            for(x in 0 until map0.cx)
                col += " " + (x % 10)
            Log.e("MAP", col)
            for(y in 0 until map0.cy) {
                var row = "$y    ".substring(0,5)
                for(x in 0 until map0.cx) {
                    var isAccess = false
                    for(la in map0.floorAccess) {
                        val c = map0.toMapGraphCoord(la)
                        if(c.x == x && c.y == y) {
                            row += "A "
                            isAccess = true
                            break
                        }
                    }
                    if(!isAccess)
                        row += if(map0.getNode(x, y).isValid) ". " else "1 "
                }
                Log.e("MAP", row)
            }
        }
    }
    fun printMap() {
        for (floor in 0 until map.size) {
            val map0 = map[floor]
            Log.e("MAP", "\n------------------------------------------------ FLOOR $floor ------------------------------------------------\n")

            var col = "$     ".substring(0,5)
            for(x in 0 until map0.cx / 10)
                col += "${(x % 10)}         "
            Log.e("MAP", col)

            col = "$     ".substring(0,5)
            for(x in 0 until map0.cx)
                col += (x % 10)
            Log.e("MAP", col)

            for(y in 0 until map0.cy) {
                var row = "$y    ".substring(0,5)
                for(x in 0 until map0.cx) {
                    var isAccess = false
                    for(la in map0.floorAccess) {
                        val c = map0.toMapGraphCoord(la)
                        if(c.x == x && c.y == y) {
                            row += "A"
                            isAccess = true
                            break
                        }
                    }
                    if(!isAccess)
                        row += mapData[floor][map0.calcIndex(x, y)]
                }
                Log.e("MAP", row)
            }
        }
    }


    /// TEST ---------------------------------------------------------------------------------------
/*
    constructor(cx: Int, cy: Int) : this(cx.toFloat(), cy.toFloat(), 1)

    fun compile(mapData: IntArray) : MapGraph {
        //map.clear()
        for(y in 0 until map[0].cy) {
            for(x in 0 until map[0].cx) {
                val index = x + y * map[0].cx
                val node = Node(index, Point(x, y), mapData[x + y*map[0].cx] == 0)
                map[0].addNode(node)
                //if(mapData[x + y*cx] == 0) {
                if(x - 1 >= 0 && y - 1 >= 0)
                    map[0].connectNodes(node, map[0].getNode(x - 1, y - 1))
                if(x - 1 >= 0)
                    map[0].connectNodes(node, map[0].getNode(x - 1, y))
                if(y - 1 >= 0)
                    map[0].connectNodes(node, map[0].getNode(x, y - 1))
                //}
            }
        }
        return map[0]
    }*/

}