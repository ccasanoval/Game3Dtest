package com.cesoft.cesdoom.map

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectMap
import com.cesoft.cesdoom.util.Log
import com.badlogic.gdx.ai.pfa.PathSmoother



class MapGraph(val width: Float, val height: Float, val scale: Int)
    : IndexedGraph<Node> {
    //: MapGraphPathSmooth() {


    companion object {
        val tag: String = MapGraph::class.java.simpleName
    }

    val cx: Int = (width / scale).toInt()
    val cy: Int = (height / scale).toInt()

    private val vertexs = ArrayList<Vertex>()
    private val nodes = ArrayList<Node>()
    private val map = ObjectMap<Node, com.badlogic.gdx.utils.Array<Connection<Node>>>()

    fun toWorldCoord(point: Point) = Vector2(((point.x-cx/2) * scale).toFloat(), ((point.y-cy/2) * scale).toFloat())
    fun toMapGraphCoord(point: Vector2) = Point((point.x/scale + cx/2).toInt(), (point.y/scale + cy/2).toInt())//TODO:Usar width y height????

    fun calcIndex(x: Int, y: Int) = x + y*cx

    //fun getNode(int i) = Nodes[i]
    fun getNode(x: Int, y: Int): Node {
        val i = calcIndex(x,y)
        if(i >= 0 && i < nodes.size) {
            return nodes[i]
        }
        else {
            Log.e(tag, "getNode: coordinates out of boundaries: ($x, $y)  ${nodes.size} <> $i ")
            //return Node(0, Point(0, 0), false)
            throw IndexOutOfBoundsException()
        }

    }
    private fun getNode(point: Point) = getNode(point.x, point.y)
    //fun getNode(point: Vector2) = getNode(toMapGraphCoord(point))
    private fun getNode(point: Vector2) : Node {
        val pos = toMapGraphCoord(point)
        var node = getNode(pos)
        while( ! node.isValido) {
            for(y in 0..15) {
                for(x in 0..15) {
                    node = getNode(Point(pos.x + x, pos.y + y))
                    if(node.isValido)
                        break
                    node = getNode(Point(pos.x - x, pos.y + y))
                    if(node.isValido)
                        break
                    node = getNode(Point(pos.x + x, pos.y - y))
                    if(node.isValido)
                        break
                    node = getNode(Point(pos.x - x, pos.y - y))
                    if(node.isValido)
                        break
                }
                if(node.isValido)
                    break
            }
        }
        return node
    }

    fun addNode(Node: Node) {
        nodes.add(Node)
    }

    /*fun clear() {
        nodes.clear()
        nodes.clear()
        map.clear()
    }*/

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
        vertexs.add(vertex)
    }

    fun findPath(orig: Point, dest: Point): GraphPath<Node> {
        val path = MapGraphSmooth()//DefaultGraphPath<Node>()
        val pathFinder = IndexedAStarPathFinder<Node>(this)
        val pathSmoother = PathSmoother<Node, Vector2>(NodeCollisionDetector(this))
        val nodeOrig = getNode(orig)
        val nodeDest = getNode(dest)
        pathFinder.searchNodePath(nodeOrig, nodeDest, HeuristicDistance, path)
        pathSmoother.smoothPath(path)
        return path
    }

    fun findPath(orig: Vector2, dest: Vector2): ArrayList<Vector2> {
        val path = MapGraphSmooth()//DefaultGraphPath<Node>()
        val nodeOrig = getNode(orig)
        val nodeDest = getNode(dest)
        val pathFinder = IndexedAStarPathFinder<Node>(this)
        val pathSmoother = PathSmoother<Node, Vector2>(NodeCollisionDetector(this))

        try {
            val t0 = System.currentTimeMillis()
            //Log.e(tag, "findPath a------:----- $nodeOrig $nodeDest  $path")
            val b2 = pathFinder.searchNodePath(nodeOrig, nodeDest, HeuristicDistance, path)
            val t1 = System.currentTimeMillis() - t0
            Log.e(tag, "findPath:----- $b2 ${path.count}  delay= $t1 ms")
            pathSmoother.smoothPath(path)
            val t2 = System.currentTimeMillis() - t0 - t1
            Log.e(tag, "smoothPath:----- $b2 ${path.count}  delay= $t2 ms")

            val res = ArrayList<Vector2>()
            for(step in path) {
                res.add(toWorldCoord(step.point))
            }
            return res
        }
        catch(e: Exception) {
            Log.e(tag, "findPath:e:-------------------- $e ")
            e.printStackTrace()
        }

        return ArrayList()
    }

    override fun getIndex(Node: Node): Int {
        return Node.index
    }

    override fun getNodeCount(): Int {
        return nodes.size
    }

    override fun getConnections(origen: Node): com.badlogic.gdx.utils.Array<Connection<Node>> {
        return if(map.containsKey(origen)) map.get(origen)
            else com.badlogic.gdx.utils.Array(0)
    }

}