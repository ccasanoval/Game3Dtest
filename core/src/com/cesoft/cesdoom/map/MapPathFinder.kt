package com.cesoft.cesdoom.map

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.BinaryHeap
import com.badlogic.gdx.utils.BinaryHeap.Node
import com.badlogic.gdx.utils.IntArray
import com.cesoft.cesdoom.util.Log

//TODO: import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
//      https://github.com/conquest/conquest
//TODO: es.usc.citius.hipster.algorithm.ADStarForward;


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MapPathFinder(val width: Int, val height: Int, val scale: Int) {

    companion object {
        private val tag = MapPathFinder::class.java.simpleName
    }

    private var aStar : Astar? = null
    private val data = BooleanArray(width*height)
    fun addCollider(pos: Vector2) {
        val x = pos.x.toInt()/scale + width/2
        val y = pos.y.toInt()/scale + height/2
        data[x + y * width] = true
    }
    fun compile(walkerSize: Float) {
        val walker = (walkerSize/scale).toInt()
        Log.e(tag, "----------------------------- WALKER SIZE: $walker  $walkerSize")
        aStar = object : Astar(width, height) {
            override fun isValid(x: Int, y: Int): Boolean {
                if(walker <= 1) {
                    return !data[x + y*width]
                }
                else {
                    if(data[x + y*width])return false
                    for(i in 1..walker) {
                        if(x+i < width  && data[x+i + y*width]) return false
                        if(x-i >= 0     && data[x-i + y*width]) return false
                        if(y+i < height && data[x + (y+i)*width]) return false
                        if(y-i >= 0     && data[x + (y-i)*width]) return false
                    }
                    return true
                }
            }
        }
        print()
    }
    private fun print() {
        var s: String = "  "
        Log.e("MapPathFinder3", "\n\n\n")
        for(x in 0 until width)
            s+=x%10
        Log.e("MapPathFinder3", s)
        for (y in 0 until height) {
            s = "$y\t"
            for(x in 0 until width) {
                s += if(data[y * width + x]) "X" else "·"
            }
            Log.e("MapPathFinder3", s)
        }
        Log.e("MapPathFinder3", "\n\n\n")
    }


    private var onceOnly = true
    private var xOldCalc = -99999999
    private var yOldCalc = -99999999
    fun getNextSteep(agent: Vector2, target: Vector2) : Vector2 {
        aStar?.let { aStar ->

            //Log.e("MAP", "----------------------- $agent to $target ")

            val xIni = (agent.x/scale + width/2f).toInt()
            val yIni = (agent.y/scale + height/2f).toInt()
            val xEnd = (target.x/scale + width/2f).toInt()
            val yEnd = (target.y/scale + height/2f).toInt()
            //Log.e(tag, "0------ xEnd=$xEnd------- $target------- ${target.x/scale}---- ${width/2f}-------")


            //TODO: si player esta dentro de zona prohibida, buscar punto mas cercano

            val path = aStar.getPath(xIni, yIni, xEnd, yEnd)
            //Log.e(tag, "A------xIni=$xIni--yIni=$yIni------------xEnd=$xEnd, yEnd=$yEnd------------  $agent    pathSize=${path.size} ******************************************")
            val n = path.size
            var i = 0
            var x = 0
            var y = 0

            while(i < n) {
                if(path.get(i) != xOldCalc || path.get(i + 1) != yOldCalc) {
                    x = path.get(i)
                    y = path.get(i + 1)
                }
                //if(onceOnly)Log.e(tag, "B $i----------------------- X=$x, Y=$y  /  X=${scale * (x - width / 2)}, Y=${scale * (y - height / 2)} ")
                i += 2
            }
            onceOnly = false

            xOldCalc = x
            yOldCalc = y

            Log.e(tag, "CALC--- -- -- -- -- -- -- -- -- --- - -- -- - - -  - - - - -  -- ------- $xIni/$x  $yIni/$y-------")

            //Log.e(tag, "C------xIni=$xIni--yIni=$yIni---------------NEXT STEP X=$x, Y=$y  /  X=${scale * (x - width / 2)}, Y=${scale * (y - height / 2)} ")
            Log.e(tag, "CALC---------------------------------------------------"+Vector2(x.toFloat() - width/2, y.toFloat() - height/2).scl(scale.toFloat(), scale.toFloat()))
            return Vector2(x.toFloat() - width/2, y.toFloat() - height/2).scl(scale.toFloat(), scale.toFloat())
        }
        throw Exception("MapPathFinder3 not initialized")
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
                //if(i > 100)return path//TODO:CES:TEST-----------------find how to search parcial..

                var node: PathNode = open.pop()!!
                if (node.x == targetX && node.y == targetY) {
                    while (node !== root) {
                        path.add(node.x)
                        path.add(node.y)
                        node = node.parent!!
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

        // To be overriden when we have the complete map
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

