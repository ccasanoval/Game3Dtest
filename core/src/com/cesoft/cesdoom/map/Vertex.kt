package com.cesoft.cesdoom.map

import com.badlogic.gdx.ai.pfa.Connection

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Vertex(private val orig: Node, private val dest: Node) : Connection<Node> {

    private var coste: Float = 0f

    init {
        if(dest.isValido)
            this.coste = orig.point.dist2(dest.point).toFloat()
                //Vector2.dst(origen.point.x.toFloat(), origen.point.y.toFloat(), destino.point.x.toFloat(), destino.point.y.toFloat())
        else
            this.coste = java.lang.Float.MAX_VALUE / 2
    }

    override fun getCost(): Float {
        return coste
    }

    override fun getFromNode(): Node {
        return orig
    }

    override fun getToNode(): Node {
        return dest
    }
}
