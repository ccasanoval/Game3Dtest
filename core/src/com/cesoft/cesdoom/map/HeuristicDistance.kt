package com.cesoft.cesdoom.map

import com.badlogic.gdx.ai.pfa.Heuristic

////////////////////////////////////////////////////////////////////////////////////////////////////
//
object HeuristicDistance : Heuristic<Node> {
    override fun estimate(orig: Node, dest: Node): Float {
        return orig.point.dist2(dest.point).toFloat()
    }
}
