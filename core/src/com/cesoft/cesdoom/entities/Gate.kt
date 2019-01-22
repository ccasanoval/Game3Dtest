package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Gate : Entity() {

    companion object {
        val tag: String = Gate::class.java.simpleName
        const val LONG = 25f
        const val HIGH = 25f
        const val THICK = 4f
        const val MAX_OFFSET_OPEN = LONG - 2f
    }

    private var stepOpen = 0.2f
    private var offsetOpened = 0f
    var isOpening = false

    private var angle = 0f
    private lateinit var pos: Vector3
    lateinit var rigidBody: btRigidBody
    private lateinit var rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo


    fun init(
            angle: Float,
            pos: Vector3,
            rigidBody: btRigidBody,
            rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo) {
        this.angle = angle
        this.pos = pos
        this.rigidBody = rigidBody
        this.rigidBodyInfo = rigidBodyInfo
    }


    fun update(delta: Float) {
        Log.e(tag, "update------------ $offsetOpened --------------- $delta  $isOpening")

        if(isOpening) {
            offsetOpened += delta * stepOpen
            if(offsetOpened > MAX_OFFSET_OPEN) {
                isOpening = false
                offsetOpened = MAX_OFFSET_OPEN
            }
            when(angle) {
                00f -> {
                    //TODO:Get position and traslate
                    rigidBody.translate(Vector3(pos.x,pos.y, pos.z + offsetOpened))
                    Log.e(tag, "update--A------------------------- "+Vector3(0f,0f, offsetOpened))
                }
                90f -> {
                    rigidBody.translate(Vector3(pos.x + offsetOpened, pos.y, pos.z))
                    Log.e(tag, "update--B------------------------- "+Vector3(0f,0f, offsetOpened))
                }
            }
        }
    }
}