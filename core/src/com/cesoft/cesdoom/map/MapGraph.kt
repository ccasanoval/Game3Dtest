package com.cesoft.cesdoom.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectMap
import com.cesoft.cesdoom.util.Log
import com.badlogic.gdx.ai.pfa.PathSmoother


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MapGraph(val id: Int, val width: Float, val height: Float, private val scale: Int)
    : IndexedGraph<Node> {

    companion object {
        val tag: String = MapGraph::class.java.simpleName
    }

    init {
        Log.e(tag, "$id------------------------------------------------ INIT ------------------------------------------------\n")
    }

    val cx: Int = (width / scale).toInt()
    val cy: Int = (height / scale).toInt()

    private val vertexes = ArrayList<Vertex>()
    private val nodes = ArrayList<Node>()
    private val map = ObjectMap<Node, com.badlogic.gdx.utils.Array<Connection<Node>>>()

    private fun toWorldCoord(point: Point) = Vector2(((point.x-cx/2) * scale).toFloat(), ((point.y-cy/2) * scale).toFloat())
    fun toMapGraphCoord(point: Vector2) = Point((point.x/scale + cx/2).toInt(), (point.y/scale + cy/2).toInt())//TODO:Usar width y height????

    fun calcIndex(x: Int, y: Int) = x + y*cx

    //fun getNode(int i) = Nodes[i]
    fun getNode(x: Int, y: Int): Node {
        val i = calcIndex(x,y)
        if(i >= 0 && i < nodes.size) {
            return nodes[i]
        }
        else {
            Log.e(tag, "id=$id:getNode: coordinates out of boundaries: ($x, $y)  ${nodes.size} <> $i ---------------------------")
            return nodes[0] // Para que los pathFinders no cojan toda la cpu buscando un path imposible
            //return Node(9999999, Point(0, 0), false)
            //throw IndexOutOfBoundsException()
        }
    }
    private fun getNode(point: Point) = getNode(point.x, point.y)
    private fun getNode(point: Vector2) : Node {
        val pos = toMapGraphCoord(point)
        var node = getNode(pos)
        if( ! node.isValid) {
            val nodeOld = node
            for(i in 1..3) {
                for(y in -i..+i) {
                    for(x in -i..+i) {
                        if(Math.abs(x) != i && Math.abs(y) != i) continue
                        //Log.e(tag, "id=$id:getNode: i=$i ---------------- TESTING NODE ($x, $y) ----------------")
                        node = getNode(Point(pos.x + x, pos.y + y))
                        if(node.isValid) {
                            Log.e(tag, "id=$id:getNode: cuidado, esto podria retardar mucho la busqueda -------------------- nodeIni=${nodeOld.point} <> nodeFin=${node.point}")
                            return node
                        }
                    }
                }
            }
        }
        return node
    }

    fun addNode(node: Node) {
        nodes.add(node)
    }

    fun clear() {
        //Log.e(tag, "$id------------------------------------------------ clear ------------------------------------------------\n")
        floorAccess.clear()
//        nodes.clear()
//        nodes.clear()
//        map.clear()
    }

    fun connectNodes(orig: Node, dest: Node) {
        connectNode(orig, dest)
        connectNode(dest, orig)
    }

    private fun connectNode(orig: Node, dest: Node) {
        val vertex = Vertex(orig, dest)
        if (!map.containsKey(orig)) {
            map.put(orig, com.badlogic.gdx.utils.Array())
        }
        map.get(orig).add(vertex)
        vertexes.add(vertex)
    }

    /// Find path in tests
    /*fun findPath(orig: Point, dest: Point): GraphPath<Node> {
        val path = MapGraphSmooth()
        val pathFinder = IndexedAStarPathFinder<Node>(this)
        val pathSmoother = PathSmoother<Node, Vector2>(NodeCollisionDetector(this))
        val nodeOrig = getNode(orig)
        val nodeDest = getNode(dest)
//Log.e(tag, "findPath a------:----- $nodeOrig $nodeDest")
        pathFinder.searchNodePath(nodeOrig, nodeDest, HeuristicDistance, path)
        pathSmoother.smoothPath(path)
        return path
    }*/

    /// Find path in game
private var maxDelay=0L
private var callsPerSecond=0L
private var callsPerSecondMedia=0L
private var timeCallsPerSecond=0L

    private val path = MapGraphSmooth()
    private val pathSmoother = PathSmoother<Node, Vector2>(NodeCollisionDetector(this))
    fun findPath(orig: Vector2, dest: Vector2, smooth: Boolean=true): ArrayList<Vector2> {

        path.clear()

        val nodeOrig = getNode(orig)
        val nodeDest = getNode(dest)
        val pathFinder = IndexedAStarPathFinder<Node>(this)

/*
        val map0 = this
        Log.e("MAP", "\n------------------------------------------------ $id ------------------------------------------------\n")
        var col = " \t\t\t"
        for(x in 0 until map0.cx)
            col += " " + (x % 10)
        Log.e("MAP", col)
        for(y in 0 until map0.cy) {
            var row = "$y    ".substring(0, 5)
            for (x in 0 until map0.cx) {
                var isAccess = false
                for (la in map0.floorAccess) {
                    val c = map0.toMapGraphCoord(la)
                    if (c.x == x && c.y == y) {
                        row += "A"
                        isAccess = true
                        break
                    }
                }
                if (!isAccess)
                    row += if (map0.getNode(x, y).isValid) "O" else "1"
            }
            Log.e("MAP", row)
        }
        Log.e(tag, "$id path----------------from $nodeOrig to $nodeDest")*/


        callsPerSecond++
        val time = System.currentTimeMillis()
        if(time >  timeCallsPerSecond + 999) {
            timeCallsPerSecond = time
            callsPerSecondMedia = (callsPerSecondMedia+callsPerSecond)/2
            //callsPerSecondMedia = callsPerSecond
            callsPerSecond = 0
        }


        try {
            val t0 = System.currentTimeMillis()
            pathFinder.searchNodePath(nodeOrig, nodeDest, HeuristicDistance, path)
            val t1 = System.currentTimeMillis()
            //if(smooth)
                pathSmoother.smoothPath(path)
            val now = System.currentTimeMillis()
//if()
//if(now - t0 > maxDelay)maxDelay=now - t0
//Log.e(tag, "smoothPath:----- #steps=${path.count} ->  delay0=${t1 - t0}  delay2=${now - t0} ms  (MAX=$maxDelay)      (CPS=$callsPerSecondMedia / $callsPerSecond) (FPS=${Gdx.graphics.framesPerSecond}) ")

            val res = ArrayList<Vector2>()
            for(step in path) {
                //Log.e(tag, "$id path----------------$step")
                res.add(toWorldCoord(step.point))
            }
            return res
        }
        catch(e: Exception) {
            Log.e(tag, "id=$id:findPath:e:-------------------- $e")
            e.printStackTrace()
        }

//Log.e(tag, "smoothPath:----------------------------- fin")
        return ArrayList()
    }

    override fun getIndex(node: Node): Int {
        return node.index
    }

    override fun getNodeCount(): Int {
        return nodes.size
    }

    override fun getConnections(origen: Node): com.badlogic.gdx.utils.Array<Connection<Node>> {
        return if(map.containsKey(origen)) map.get(origen)
            else com.badlogic.gdx.utils.Array(0)
    }


    /// Mapa de accesos entre plantas
    val floorAccess = ArrayList<Vector2>()
    fun addFloorAccess(access: Vector2) = floorAccess.add(access.cpy())
    fun getNearerFloorAccess(pos: Vector2): Vector2 {
        var curDist = Float.MAX_VALUE
        var access = Vector2.Zero
        for(i in 0 until floorAccess.size) {
            val dist = floorAccess[i].dst2(pos)
            if(dist < curDist) {
                curDist = dist
                access = floorAccess[i]
            }
        }
//Log.e(tag, "getNearerFloorFloorAccess:----------------------------- $access")
        return access.cpy()
    }

}
