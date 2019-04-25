package com.cesoft.cesdoom.map

import com.badlogic.gdx.ai.pfa.Connection

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Vertex(private val orig: Node, private val dest: Node) : Connection<Node> {

    private var cost: Float = 0f

    init {
        if(dest.isValid)
            this.cost = orig.point.dist2(dest.point).toFloat()
        else
            this.cost = java.lang.Float.MAX_VALUE / 2
    }

    override fun getCost(): Float {
        return cost
    }

    override fun getFromNode(): Node {
        return orig
    }

    override fun getToNode(): Node {
        return dest
    }
}
