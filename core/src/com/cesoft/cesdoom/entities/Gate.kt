package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.components.GateComponent.MAX_OFFSET_OPEN
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Gate(val id: String, private val assets: Assets) : Entity() {

    companion object {
        val tag: String = Gate::class.java.simpleName
        private const val STEP_OPEN = 12f
    }

    private var angle = 0f
    private lateinit var pos: Vector3
    lateinit var rigidBody: btRigidBody
    private lateinit var rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo
    private lateinit var model: ModelComponent

    private var isLocked = true
        fun unlock() {
            isLocked = false
        }
    private var offsetOpened = 0f
    private var isOpening = false
    private var isOpen = false
    private var lockedOnce = 0L

    fun init(
            model: ModelComponent,
            angle: Float,
            pos: Vector3,
            rigidBody: btRigidBody,
            rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo) {
        this.angle = angle
        this.pos = pos
        this.rigidBody = rigidBody
        this.rigidBodyInfo = rigidBodyInfo
        this.model = model
    }

    fun update(delta: Float) {
        if(isOpening) {
            offsetOpened += delta * STEP_OPEN
            if(offsetOpened > MAX_OFFSET_OPEN) {
                isOpening = false
                isOpen = true
                offsetOpened = MAX_OFFSET_OPEN
            }
            when(angle) {
                00f -> {
                    val posTemp = Vector3(pos.x, pos.y, pos.z + offsetOpened)
                    model.instance.transform.setTranslation(posTemp)
                }
                90f -> {
                    val posTemp = Vector3(pos.x + offsetOpened, pos.y, pos.z)
                    model.instance.transform.setTranslation(posTemp)
                }
            }
        }
    }

    fun tryToOpen() {
        val now = System.currentTimeMillis()
        if(isLocked) {
            if(now > lockedOnce+2000) {
                lockedOnce = now
                PlayerComponent.message = assets.formatString(Assets.GATE_LOCKED, id)//TODO send signal
                Sounds.play(Sounds.SoundType.GATE_LOCKED)
            }
        }
        else if(!isOpening && !isOpen) {
            isOpening = true
            PlayerComponent.message = assets.formatString(Assets.GATE_OPENS, id)//TODO send signal
            Sounds.play(Sounds.SoundType.GATE_OPENS)
        }
    }
}