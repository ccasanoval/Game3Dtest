package com.cesoft.cesdoom.managers

import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.map.MapGraphFactory


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object WallMapFactory {

    private const val thick = (WallFactory.THICK.toInt() * 3.0).toInt()	// Debe ser mayor para que no haga colision con enemigo, que no es un punto sino un objeto 3D / o cambiar scale
    private const val long =  (WallFactory.LONG.toInt()  * 3.0).toInt() //TODO: Check, 3 works, 2.7 too

    fun create(mapFactory: MapGraphFactory, pos: Vector3, angle: Float, ignore: Int) {

        when(angle) {//TODO: change by sin + cos of angle...
            +00f -> //--- Vertical
                for(x_ in -thick/2..thick/2)
                    for(z_ in -long/2..long/2)
                        mapFactory.addCollider(pos.x + x_, pos.z + z_)
            +90f -> //--- Horizontal
                for(z_ in -thick/2..thick/2)
                    for(x_ in -long/2..long/2)
                        mapFactory.addCollider(pos.x + x_, pos.z + z_)
            +45f ->
                for(z_ in 0..thick)
                    for(x_ in z_..z_+(long*0.7971f).toInt())
                        mapFactory.addCollider(pos.x + z_, pos.z + z_)
            -45f ->
                for(z_ in 0..thick)
                    for(x_ in z_..z_+(long*0.7971f).toInt())
                        mapFactory.addCollider(pos.x + x_, pos.z + x_)
        }
     }
}