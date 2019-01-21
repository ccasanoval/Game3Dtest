package com.cesoft.cesdoom.map


import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath
import com.badlogic.gdx.math.Vector2


open class MapGraphSmooth : DefaultGraphPath<Node>(), SmoothableGraphPath<Node, Vector2> {

    private val tmpPosition = Vector2()

    override fun add(node: Node?) {
        super.add(node)
    }

    override fun clear() {
        super.clear()
    }

    override fun reverse() {
        super.reverse()
    }

    override fun iterator(): MutableIterator<Node> {
        return super.iterator()
    }

    override fun get(index: Int): Node {
        return super.get(index)
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getNodePosition(index: Int): Vector2 {
        val node = nodes.get(index)
        return tmpPosition.set(node.point.x.toFloat(), node.point.y.toFloat())
    }

    override fun truncatePath(newLength: Int) {
        nodes.truncate(newLength)
    }

    override fun swapNodes(index1: Int, index2: Int) {
        nodes.set(index1, nodes.get(index2))
    }

}