package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.HealthComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.components.PlayerComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Health(private val cuantity: Int) : Entity() {

    companion object {
        val tag: String = Health::class.java.simpleName
    }

    var isPickedUp = false
        private set
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

    fun update() {
        model.instance.transform.rotate(Vector3.Y, 5f)
    }

    fun pickup() {
        if(isPickedUp)return
        isPickedUp = true

        Sounds.play(Sounds.SoundType.HEALTH_RELOAD)
		PlayerComponent.add(cuantity)
        //HealthComponent.add(cuantity)
        HealthComponent.reloading = true
    }
}