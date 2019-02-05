package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.components.AmmoComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.managers.GunFactory
import com.cesoft.cesdoom.systems.PlayerSystem


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Ammo(private val cuantity: Int) : Entity() {

    companion object {
        val tag: String = Ammo::class.java.simpleName
    }

    private var isPickedUp = false
    private var angle = 0f
    private lateinit var pos: Vector3
    lateinit var rigidBody: btRigidBody
    private lateinit var rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo
    private lateinit var model: ModelComponent

    fun init(
            model: ModelComponent,
            pos: Vector3,
            rigidBody: btRigidBody,
            rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo) {
        this.pos = pos
        this.rigidBody = rigidBody
        this.rigidBodyInfo = rigidBodyInfo
        this.model = model
    }

    fun update(delta: Float) {
        angle += delta * 100
        model.instance.transform.rotate(Vector3.Y, angle)
    }

    fun pickup() {
        if(isPickedUp)return
        isPickedUp = true

        //Sounds.play(Sounds.SoundType.AMMO)//TODO
        AmmoComponent.add(cuantity)
        AmmoComponent.reloading = true

        //TODO: delete component
        //val modelComponent = getComponent(ModelComponent::class.java)
        //engine.removeEntity(this)
    }
}