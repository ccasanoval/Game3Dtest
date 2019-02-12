package com.cesoft.cesdoom.map

import com.badlogic.gdx.math.Vector2
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MapGraphFactory(val width: Float, val height: Float, scale: Int) {

    val map: MapGraph = MapGraph(width, height, scale)
    private val mapData: IntArray = IntArray(map.cx * map.cy)

    init {
        for(i in 0 until mapData.size)
            mapData[i]=0
    }

    fun addCollider(x: Float, y: Float) {
        //Log.e("MapGraphFactory", "1--- ${map.cx},${map.cy} ---------------------------- $x, $y")
        val point = map.toMapGraphCoord(Vector2(x, y))
        //Log.e("MapGraphFactory", "2--- ${point.x},${point.y} ----------------------------")
        val index = map.calcIndex(point.x, point.y)
        //Log.e("MapGraphFactory", "3------------------- $index ------------ "+map.toMapGraphCoord(Vector2(x, y)))
        if(index > 0 && index < mapData.size)
            mapData[index] = 1
        else
            Log.e("MapGraphFactory", "addCollider:e: Negative map graph coordinates--------- ($x,$y) => ($point) ---------- $index ------------ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }

    /*fun clear() {
        //map.clear()
        for(i in 0 until mapData.size)
            mapData[i]=0
    }*/

    fun compile() {
        for(y in 0 until map.cy) {
            for(x in 0 until map.cx) {
                val index = map.calcIndex(x, y)
                val node = Node(index, Point(x, y), mapData[index] == 0)
                map.addNode(node)
                //if(mapData[x + y*cx] == 0) {
                if(x - 1 >= 0 && y - 1 >= 0)
                    map.connectNodes(node, map.getNode(x - 1, y - 1))
                if(x - 1 >= 0)
                    map.connectNodes(node, map.getNode(x - 1, y))
                if(y - 1 >= 0)
                    map.connectNodes(node, map.getNode(x, y - 1))
                //}
            }
        }
    }


    fun print() {
        var col = " \t\t\t"
        for(x in 0 until map.cx)
            col += " "+(x%10)
        Log.e("MAP", col)
        for(y in 0 until map.cy) {
            var row = "$y \t\t\t"
            for(x in 0 until map.cx) {
                row += if(map.getNode(x,y).isValido) ". " else "1 "
            }
            Log.e("MAP", row)
        }
    }
    fun print2() {
        var col = "  \t\t\t"
        for(x in 0 until map.cx)
            col += " "+(x%10)
        Log.e("MAP", col)
        for(y in 0 until map.cy) {
            var row = "$y \t\t\t"
            for(x in 0 until map.cx) {
                row += mapData[map.calcIndex(x, y)]
            }
            Log.e("MAP", row)
        }
    }



    /// TEST ---------------------------------------------------------------------------------------

    constructor(cx: Int, cy: Int) : this(cx.toFloat(), cy.toFloat(), 1)

    fun compile(mapData: IntArray) : MapGraph {
        //map.clear()
        for(y in 0 until map.cy) {
            for(x in 0 until map.cx) {
                val index = x + y * map.cx
                val node = Node(index, Point(x, y), mapData[x + y*map.cx] == 0)
                map.addNode(node)
                //if(mapData[x + y*cx] == 0) {
                if(x - 1 >= 0 && y - 1 >= 0)
                    map.connectNodes(node, map.getNode(x - 1, y - 1))
                if(x - 1 >= 0)
                    map.connectNodes(node, map.getNode(x - 1, y))
                if(y - 1 >= 0)
                    map.connectNodes(node, map.getNode(x, y - 1))
                //}
            }
        }
        return map
    }

}