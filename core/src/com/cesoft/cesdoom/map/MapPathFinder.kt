package com.cesoft.cesdoom.map

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.BinaryHeap
import com.badlogic.gdx.utils.BinaryHeap.Node
import com.badlogic.gdx.utils.IntArray
import com.cesoft.cesdoom.util.Log

//TODO: import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder


////////////////////////////////////////////////////////////////////////////////////////////////////
//
//TODO: traducir de coordenadas: 0,0 central a 0,0 en TopLeft en construcor... mejor funcion que añada los boolean uno a uno...
class MapPathFinder(val width: Int, val height: Int, val scale: Int, private val data: BooleanArray) {

    private var aStar : Astar = object : Astar(width, height) {
        override fun isValid(x: Int, y: Int): Boolean {
            return !data[x + y * width]
        }
    }

    private val data2 = BooleanArray(width*height)
    fun addCollider(pos: Vector2) {
        val x = pos.x.toInt() + width/2
        val y = pos.y.toInt() + height/2
        data2[x + y * width] = true
    }
    fun compile() {
        aStar = object : Astar(width, height) {
            override fun isValid(x: Int, y: Int): Boolean {
                return !data2[x + y * width]
            }
        }
    }


    fun getNextSteep(agent: Vector2, target: Vector2) : Vector2 {

        Log.e("MAP", "----------------------- $agent to $target ")
        //TODO: traducir de coordenadas: 0,0 central a 0,0 en TopLeft y viceversa...

        val xIni = agent.x.toInt() + width/2
        val yIni = agent.y.toInt() + height/2
        val xEnd = target.x.toInt() + width/2
        val yEnd = target.y.toInt() + height/2

        val path = aStar.getPath(xIni, yIni, xEnd, yEnd)
        val n = path.size
        var i = 0
        var x = 0
        var y = 0
        while(i < n) {
            x = path.get(i)
            y = path.get(i + 1)
            Log.e("MAP", "$i----------------------- X=$x, Y=$y  /  X=${x-width/2}, Y=${y-height/2} ")
            i += 2
        }
        return Vector2(x.toFloat() -width/2 , y.toFloat() -height/2)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //From https://gist.github.com/NathanSweet/7587981
    private open class Astar(val width: Int, val height: Int) {
        private val open: BinaryHeap<PathNode> = BinaryHeap(width * 4, false)
        private val nodes: Array<PathNode> = Array(width * height)
        private var runID: Int = 0
        private val path = IntArray()
        private var targetX: Int = 0
        private var targetY: Int = 0

        init {
            for (i in 0 until width * height)
                nodes.add(null)
        }

        /** Returns x,y pairs that are the path from the target to the start.  */
        fun getPath(startX: Int, startY: Int, targetX: Int, targetY: Int): IntArray {
            this.targetX = targetX
            this.targetY = targetY

            path.clear()
            open.clear()

            runID++
            if (runID < 0) runID = 1
            val index = startY * width + startX
            var root: PathNode? = nodes[index]
            if (root == null) {
                root = PathNode(0f)
                root.x = startX
                root.y = startY
                nodes[index] = root
            }
            root.parent = null
            root.pathCost = 0
            open.add(root, 0f)

            val lastColumn = width - 1
            val lastRow = height - 1
            var i = 0
            while (open.size > 0) {
                var node: PathNode? = open.pop()
                if (node!!.x == targetX && node.y == targetY) {
                    while (node !== root) {
                        path.add(node!!.x)
                        path.add(node.y)
                        node = node.parent
                    }
                    break
                }
                node.closedID = runID
                val x = node.x
                val y = node.y
                if (x < lastColumn) {
                    addNode(node, x + 1, y, 10)
                    if (y < lastRow) addNode(node, x + 1, y + 1, 14) // Diagonals cost more, roughly equivalent to sqrt(2).
                    if (y > 0) addNode(node, x + 1, y - 1, 14)
                }
                if (x > 0) {
                    addNode(node, x - 1, y, 10)
                    if (y < lastRow) addNode(node, x - 1, y + 1, 14)
                    if (y > 0) addNode(node, x - 1, y - 1, 14)
                }
                if (y < lastRow) addNode(node, x, y + 1, 10)
                if (y > 0) addNode(node, x, y - 1, 10)
                i++
            }
            return path
        }

        private fun addNode(parent: PathNode?, x: Int, y: Int, cost: Int) {
            if (!isValid(x, y)) return

            val pathCost = parent!!.pathCost + cost
            val score = (pathCost + Math.abs(x - targetX) + Math.abs(y - targetY)).toFloat()

            val index = y * width + x
            var node: PathNode? = nodes[index]
            if (node != null && node.runID == runID) { // Node already encountered for this run.
                if (node.closedID != runID && pathCost < node.pathCost) { // Node isn't closed and new cost is lower.
                    // Update the existing node.
                    open.setValue(node, score)
                    node.parent = parent
                    node.pathCost = pathCost
                }
            } else {
                // Use node from the cache or create a new one.
                if (node == null) {
                    node = PathNode(0f)
                    node.x = x
                    node.y = y
                    nodes[index] = node
                }
                open.add(node, score)
                node.runID = runID
                node.parent = parent
                node.pathCost = pathCost
            }
        }

        protected open fun isValid(x: Int, y: Int): Boolean {
            return true
        }

        private class PathNode(value: Float) : Node(value) {
            internal var runID: Int = 0
            internal var closedID: Int = 0
            internal var x: Int = 0
            internal var y: Int = 0
            internal var pathCost: Int = 0
            internal var parent: PathNode? = null
        }
    }
}



/*

import com.cesoft.cesdoom.MapPathFinder.MapPathFinder
import com.badlogic.gdx.math.Interpolation.circle

import sun.font.LayoutPathImpl.getPath

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph


class CesIndexGraph : IndexedGraph<BinaryHeap.Node> {
    private val graph = ArrayList<BinaryHeap.Node>()

    init {
        for(i in 0..99)
            graph.add(i, BinaryHeap.Node(1f))
    }

    /// implement: IndexedGraph
    override fun getConnections(fromNode: BinaryHeap.Node?): Array<Connection<BinaryHeap.Node>> {
        val conn = Array<Connection<BinaryHeap.Node>>()
        fromNode
        return conn
    }
    override fun getIndex(node: BinaryHeap.Node?): Int {
        return 0
    }
    override fun getNodeCount(): Int {
        return 100
    }

    ///
    fun getNodeByXY(x: Float, y: Float) {

    }
}

fun a(agent: Vector2, target: Vector2) {
    val graph = CesIndexGraph()
    val pathFinder = IndexedAStarPathFinder<BinaryHeap.Node>(graph, false)

    val startX = agent.x
    val startY = agent.y

    val endX = target.x
    val endY = target.y

    val startNode = graph.getNodeByXY(startX, startY)
    val endNode = graph.getNodeByXY(endX, endY)

    val resultPath = null
    pathFinder.searchNodePath(startNode, endNode, HeuristicImp(), resultPath)
    Gdx.app.log("Path", ""+ resultPath.getCount())

}*/