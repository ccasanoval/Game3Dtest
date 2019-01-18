package com.cesoft.cesdoom.map

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.ai.pfa.*
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.ai.sched.LoadBalancingScheduler
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Pool
import com.cesoft.cesdoom.util.Log
import com.badlogic.gdx.ai.pfa.PathSmoother
import com.badlogic.gdx.ai.pfa.PathFinderQueue


class MapGraph(val width: Float, val height: Float, val scale: Int) : IndexedGraph<Node>, Telegraph {

    val cx: Int = (width / scale).toInt()
    val cy: Int = (height / scale).toInt()

    private val vertex = ArrayList<Vertex>()
    private val nodes = ArrayList<Node>()
    private val map = ObjectMap<Node, com.badlogic.gdx.utils.Array<Connection<Node>>>()

    fun toWorldCoord(point: Point) = Vector2(((point.x-cx/2) * scale).toFloat(), ((point.y-cy/2) * scale).toFloat())
    fun toMapGraphCoord(point: Vector2) = Point((point.x/scale + cx/2).toInt(), (point.y/scale + cy/2).toInt())//TODO:Usar width y height????

    fun calcIndex(x: Int, y: Int) = x + y*cx

    //fun getNode(int i) = Nodes[i]
    fun getNode(x: Int, y: Int): Node {
        val i = calcIndex(x,y)
        if(i > 0 && i < nodes.size)
            return nodes[i]
        else
            return Node(0,Point(0,0),false)
        //throw IndexOutOfBoundsException()
    }
    fun getNode(point: Point) = getNode(point.x, point.y)
    fun getNode(point: Vector2) = getNode(toMapGraphCoord(point))

    fun addNode(Node: Node) {
        nodes.add(Node)
    }

    fun clear() {
        nodes.clear()
        nodes.clear()
        map.clear()
    }

    fun connectNodes(orig: Node, dest: Node) {
        connectNode(orig, dest)
        connectNode(dest, orig)
    }

    private fun connectNode(orig: Node, dest: Node) {
        val vertice = Vertex(orig, dest)
        if (!map.containsKey(orig)) {
            map.put(orig, com.badlogic.gdx.utils.Array())
        }
        map.get(orig).add(vertice)
        vertex.add(vertice)
    }

    /*fun findPath(orig: Node, dest: Node): GraphPath<Node> {
        val path = DefaultGraphPath<Node>()
        val pf = IndexedAStarPathFinder<Node>(this)
        pf.searchNodePath(orig, dest, HeuristicDistance, path)
        return path
    }*/
    fun findPath(orig: Point, dest: Point): GraphPath<Node> {
        val path = DefaultGraphPath<Node>()
        val pf = IndexedAStarPathFinder<Node>(this)
        val nodeOrig = getNode(orig)
        val nodeDest = getNode(dest)
        pf.searchNodePath(nodeOrig, nodeDest, HeuristicDistance, path)
        return path
    }



    fun findPath(orig: Vector2, dest: Vector2): ArrayList<Vector2> {
        val path = DefaultGraphPath<Node>()
        val nodeOrig = getNode(orig)
        val nodeDest = getNode(dest)
        val pf = IndexedAStarPathFinder<Node>(this)

        // Interruptible Search
        /*try {
            val timeToRun = 50 * 1000L //ns
            //val timeToRun = (sliderMillisAvailablePerFrame.getValue() * 1000000f).toLong()
            //val request = PathFinderRequest<Node>(nodeOrig, nodeDest, HeuristicDistance, path)

            val pathFinderQueue = PathFinderQueue<Node>(pf)
            MessageManager.getInstance().addListener(pathFinderQueue, PF_REQUEST)

            val scheduler = LoadBalancingScheduler(100)
            scheduler.add(pathFinderQueue, 1, 0)
            scheduler.run(timeToRun)

            val pfRequest = requestPool.obtain()
            pfRequest.startNode = nodeOrig
            pfRequest.endNode = nodeDest
            pfRequest.heuristic = HeuristicDistance
            pfRequest.responseMessageCode = PF_RESPONSE
            MessageManager.getInstance().dispatchMessage(this, PF_REQUEST, pfRequest)

            //val b1 = pf.search(request, time)
            //Log.e("MapGraph", "1----- $b1 ${path.count} ")
        }
        catch(e: Exception) {
            Log.e("MapGraph", "1:e:----- $e ")
        }*/

        // No Interruptible Search
        val t0 = System.currentTimeMillis()
        //Log.e("MapGraph", "2----- $t0") // 55ms
        val b2 = pf.searchNodePath(nodeOrig, nodeDest, HeuristicDistance, path)
        val t = System.currentTimeMillis() - t0
        Log.e("MapGraph", "3----- $b2 ${path.count}  delay= $t ms")

        val res = ArrayList<Vector2>()
        for(step in path) {
            res.add(toWorldCoord(step.point))
        }
        return res
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








    ///------








    private var requestPool: Pool<MyPathFinderRequest> = object : Pool<MyPathFinderRequest>() {
        override fun newObject(): MyPathFinderRequest {
            return MyPathFinderRequest()
        }
    }
    var activePath: TiledSmoothableGraphPath = TiledSmoothableGraphPath()
    var workPath: TiledSmoothableGraphPath = TiledSmoothableGraphPath()
    var pathSmoother: PathSmoother<Node, Vector2> = PathSmoother(NodeCollisionDetector(this))

    internal inner class MyPathFinderRequest : PathFinderRequest<Node>(), Pool.Poolable {
        var pathSmootherRequest: PathSmootherRequest<Node, Vector2>
        var smoothEnabled: Boolean = false
        var smoothFinished: Boolean = false

        init {
            this.resultPath = TiledSmoothableGraphPath()
            pathSmootherRequest = PathSmootherRequest()
        }

        override fun initializeSearch(timeToRun: Long): Boolean {
            resultPath = workPath
            resultPath.clear()
            smoothEnabled = true
            pathSmootherRequest.refresh(resultPath as TiledSmoothableGraphPath)
            smoothFinished = false
            //worldMap.startNode = startNode
            return true
        }

        override fun finalizeSearch(timeToRun: Long): Boolean {
            if (pathFound && smoothEnabled && !smoothFinished) {
                smoothFinished = pathSmoother.smoothPath(pathSmootherRequest, timeToRun)
                if (!smoothFinished) return false
            }
            return true
        }

        override fun reset() {
            this.startNode = null
            this.endNode = null
            this.heuristic = null
            this.client = null
        }
    }


    inner class TiledSmoothableGraphPath : DefaultGraphPath<Node>(), SmoothableGraphPath<Node, Vector2> {

        private val tmpPosition = Vector2()

        override fun getNodePosition(index: Int): Vector2 {
            val node = nodes.get(index)
            return tmpPosition.set(node.point.x.toFloat(), node.point.y.toFloat())
        }

        override fun swapNodes(index1: Int, index2: Int) {
            // x.swap(index1, index2);
            // y.swap(index1, index2);
            nodes.set(index1, nodes.get(index2))
        }

        override fun truncatePath(newLength: Int) {
            nodes.truncate(newLength)
        }
    }

    private val PF_REQUEST = 1
    private val PF_RESPONSE = 2
    override fun handleMessage(telegram: Telegram): Boolean {
        Log.e("MAP", "handleMessage---------------------------------------------------------------------"+telegram.message)
        when (telegram.message) {
            PF_RESPONSE // PathFinderQueue will call us directly, no need to register for this message
            -> {
                val pfr = telegram.extraInfo as MyPathFinderRequest
                /*if (PathFinderRequestControl.DEBUG) {
                    val pfQueue = telegram.sender as PathFinderQueue<Node>
                    println("pfQueue.size = " + pfQueue.size() + " executionFrames = " + pfr.executionFrames)
                }*/

                // Swap double buffer
                workPath = activePath
                activePath = pfr.resultPath as TiledSmoothableGraphPath

                //pfr.smoothEnabled

                // Release the request
                requestPool.free(pfr)
            }
        }
        return true
    }
}
