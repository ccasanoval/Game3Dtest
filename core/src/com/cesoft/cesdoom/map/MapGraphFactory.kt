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

    init {
        for(level in 0 until mapData.size) {
            map[0].clear()
            for(i in 0 until mapData[level].size) {
                mapData[level][i] = 0
            }
        }
    }

    /*fun addCollider(level: Int, x: Float, y: Float) {
        val point = map[level].toMapGraphCoord(Vector2(x, y))
        val index = map[level].calcIndex(point.x, point.y)
        if(index > 0 && index < mapData[level].size)
            mapData[level][index] = 1
        else
            Log.e("MapGraphFactory", "addCollider:e: Negative map graph coordinates--------- ($level, $x,$y) => ($point) ---------- $index ------------ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }*/
    fun addLevelAccess(level: Int, x: Float, y: Float) {
        //DEL or enhance
//        val point = map[level].toMapGraphCoord(Vector2(x, y))
//        val index = map[level].calcIndex(point.x, point.y)
//        mapData[level][index] = 0
        //
        map[level].addLevelAccess(Vector2(x,y))
    }
    //
    fun toMapGraphCoord(level: Int, pos: Vector2) : Point = map[level].toMapGraphCoord(pos)
    fun addCollider(level: Int, point: Point) {
        val index = map[level].calcIndex(point.x, point.y)
        if(index > 0 && index < mapData[level].size)
            mapData[level][index] = 1
        else
            Log.e("MapGraphFactory", "addCollider:e: Negative map graph coordinates--------- ($level, $point) => ---------- $index ------------ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }
    /*fun addLevelAccess(level: Int, point: Point) {
        map[level].addLevelAccess(Vector2(x,y))
    }*/


    /*fun clear() {
        //map.clear()
        for(i in 0 until mapData.size)
            mapData[i]=0
    }*/

    fun compile() {
        for(level in 0 until map.size) {
            for(y in 0 until map[level].cy) {
                for(x in 0 until map[level].cx) {
                    val index = map[level].calcIndex(x, y)
                    val node = Node(index, Point(x, y), mapData[level][index] == 0)
                    map[level].addNode(node)
                    //if(mapData[level][x + y*cx] == 0) {
                    if(x - 1 >= 0 && y - 1 >= 0)
                        map[level].connectNodes(node, map[level].getNode(x - 1, y - 1))
                    if(x - 1 >= 0)
                        map[level].connectNodes(node, map[level].getNode(x - 1, y))
                    if(y - 1 >= 0)
                        map[level].connectNodes(node, map[level].getNode(x, y - 1))
                    //}
                }
            }
        }
    }


    fun print() {
        for(level in 0 until map.size) {
            val map0 = map[level]
            Log.e("MAP", "\n------------------------------------------------ LEVEL $level ------------------------------------------------\n")
            var col = " \t\t\t"
            for(x in 0 until map0.cx)
                col += " " + (x % 10)
            Log.e("MAP", col)
            for(y in 0 until map0.cy) {
                var row = "$y    ".substring(0,5)
                for(x in 0 until map0.cx) {
                    var isAccess = false
                    for(la in map0.levelAccess) {
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
    fun print2() {
        for (level in 0 until map.size) {
            val map0 = map[level]
            Log.e("MAP", "\n------------------------------------------------ LEVEL $level ------------------------------------------------\n")

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
                    for(la in map0.levelAccess) {
                        val c = map0.toMapGraphCoord(la)
                        if(c.x == x && c.y == y) {
                            row += "A"
                            isAccess = true
                            break
                        }
                    }
                    if(!isAccess)
                        row += mapData[level][map0.calcIndex(x, y)]
                }
                Log.e("MAP", row)
            }
        }
    }


    /// TEST ---------------------------------------------------------------------------------------

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
    }

}