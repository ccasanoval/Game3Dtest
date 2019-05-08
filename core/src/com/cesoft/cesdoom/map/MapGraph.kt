package com.cesoft.cesdoom.map

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectMap
import com.cesoft.cesdoom.util.Log
import com.badlogic.gdx.ai.pfa.PathSmoother
import com.cesoft.cesdoom.components.EnemyComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MapGraph(val id: Int, val width: Float, val height: Float, private val scale: Int)
    : IndexedGraph<Node> {

    companion object {
        val tag: String = MapGraph::class.java.simpleName
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
    private fun getNode(point: Vector2, enemy: EnemyComponent?) : Node {
        val pos = toMapGraphCoord(point)
        var node = getNode(pos)
        if( ! node.isValid) {
            val nodeOld = node
            for(i in 1..3) {
                for(y in -i..+i) {
                    for(x in -i..+i) {
                        if(Math.abs(x) != i && Math.abs(y) != i) continue
                        node = getNode(Point(pos.x + x, pos.y + y))
                        if(node.isValid) {
                            Log.e(tag, "id=$id:getNode: cuidado, esto podria retardar mucho la busqueda -----Enemy=${enemy?.id}--------------- nodeIni=${nodeOld.point} <> nodeFin=${node.point}")
                            enemy?.incRepeatedPos(node.index)
                            return node
                        }
                    }
                }
            }
        }
        enemy?.incRepeatedPos(node.index)
        return node
    }

    fun addNode(node: Node) {
        nodes.add(node)
    }

    fun clear() {
        floorAccess.clear()
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


    private val path = MapGraphSmooth()
    private val pathSmoother = PathSmoother<Node, Vector2>(NodeCollisionDetector(this))
    fun findPath(orig: Vector2, dest: Vector2, enemy: EnemyComponent): ArrayList<Vector2> {

        path.clear()

        val nodeOrig = getNode(orig, enemy)
        val nodeDest = getNode(dest, null)
        if(enemy.isTrapped()) {
            //Log.e(tag, "Enemy: ${enemy.id} IS TRAPPED !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            return ArrayList()
        }
        val pathFinder = IndexedAStarPathFinder<Node>(this)

        try {
            pathFinder.searchNodePath(nodeOrig, nodeDest, HeuristicDistance, path)
            pathSmoother.smoothPath(path)
            val res = ArrayList<Vector2>()
            for(step in path) {
                res.add(toWorldCoord(step.point))
            }
            return res
        }
        catch(e: Exception) {
            Log.e(tag, "id=$id:findPath:e:------- $nodeOrig / $nodeDest ------------- $e")
        }

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
