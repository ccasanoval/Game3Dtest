package com.cesoft.cesdoom.map

import com.badlogic.gdx.ai.pfa.Heuristic
import com.badlogic.gdx.math.Vector2

////////////////////////////////////////////////////////////////////////////////////////////////////
//
object HeuristicDistance : Heuristic<Node> {
    override fun estimate(orig: Node, dest: Node): Float {
        return orig.point.dist2(dest.point).toFloat()
        //return Vector2.dst(orig.point.x.toFloat(), orig.y.toFloat(), dest.x.toFloat(), dest.y.toFloat()) * 10f
    }
}
