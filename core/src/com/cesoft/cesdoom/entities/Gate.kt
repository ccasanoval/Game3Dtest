package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.util.Log
import com.cesoft.cesdoom.components.GateComponent.MAX_OFFSET_OPEN

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Gate(private val id: Int) : Entity() {

    companion object {
        val tag: String = Gate::class.java.simpleName
        private const val STEP_OPEN = 6f
    }

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
        //Log.e(tag, "update($id)------------ $offsetOpened --------------- $delta  $isOpening")

        if(isOpening) {
            offsetOpened += delta * STEP_OPEN
            if(offsetOpened > MAX_OFFSET_OPEN) {
                isOpening = false
                offsetOpened = MAX_OFFSET_OPEN
            }
            val model = getComponent(ModelComponent::class.java)
            when(angle) {
                00f -> {
                    //TODO:Get position and traslate
                    val posTemp = Vector3(pos.x, pos.y, pos.z + offsetOpened)
                    model.instance.transform.setTranslation(posTemp)
                    //Log.e(tag, "update($id)-00-------- $offsetOpened $delta  $isOpening ---------------- $posTemp ")
                }
                90f -> {
                    val posTemp = Vector3(pos.x + offsetOpened, pos.y, pos.z)
                    model.instance.transform.setTranslation(posTemp)
                    //Log.e(tag, "update($id)-90--------offset=$offsetOpened delta=$delta open=$isOpening ---------------- pos=$posTemp ")
                }
            }
        }
    }
}