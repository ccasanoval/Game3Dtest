package com.cesoft.cesdoom.map

import com.cesoft.cesdoom.util.Log

//http://tutorials.boondog.xyz/2016/12/13/pathfinding-with-jump-point-seach-2/
//https://happycoding.io/tutorials/libgdx/pathfinding
//https://www.javatips.net/api/gdx-ai-master/tests/src/com/badlogic/gdx/ai/tests/pfa/tests/InterruptibleFlatTiledAStarTest.java
//https://www.javatips.net/api/gdx-ai-master/tests/src/com/badlogic/gdx/ai/tests/pfa/tests/InterruptibleHierarchicalTiledAStarTest.java
object Test {

    private val cx = 10
    private val cy = 10
    private val mapData = intArrayOf(
            // 1  2  3  4  5  6  7  8  9
            0, 1, 0, 0, 0, 0, 0, 0, 0, 0, //0
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //1
            0, 0, 0, 1, 0, 0, 0, 1, 0, 0, //2
            0, 0, 0, 1, 1, 0, 1, 1, 0, 0, //3
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //4
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //5
            0, 0, 0, 1, 1, 1, 1, 0, 0, 0, //6
            0, 0, 0, 0, 0, 0, 1, 0, 0, 0, //7
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //8
            0, 0, 0, 0, 0, 1, 0, 0, 0, 0) //9


    @JvmStatic
    fun main(args: Array<String>) {
        val map = MapGraphFactory(cx, cy).compile(mapData)

        //var path = map.findPath(map.getNode(0, 0), map.getNode(cx - 1, cy - 1))
        var path = map.findPath(Point(0, 0), Point(cx - 1, cy - 1))
        Log.e("TEST", "\n--------- 0,0 to ${cx-1},${cy-1} ------------N=" + path.count + " ")
        for(i in 0 until path.count) {
            //Log.e("TEST", "$i---------------------")
            Log.e("TEST", "$i---------------------" + path.get(i).point)
        }

        //path = map.findPath(map.getNode(1, 1), map.getNode(2, 1))
        path = map.findPath(Point(0, 0), Point(2, 0))
        Log.e("TEST", "\n---------------------N=" + path.count + " ")
        for(i in 0 until path.count) {
            Log.e("TEST", i.toString() + "---------------------" + path.get(i).point)
        }
    }
}
