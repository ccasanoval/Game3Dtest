package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.components.PlayerComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Switch(private val id: String) : Entity() {

    companion object {
        val tag: String = Switch::class.java.simpleName
    }

    private val textureOn: Texture = CesDoom.instance.assets.getSwitchOn()
    private var isActivated = false
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

    fun activate() {
        if(isActivated)return
        isActivated = true

        Sounds.play(Sounds.SoundType.SWITCH)

        PlayerComponent.message = CesDoom.instance.assets.formatString(Assets.GATE_UNLOCKED, id)//TODO: MessageSystem + dispatch signal

        val modelComponent = ModelComponent.get(this)
        val textureAttributeOn = TextureAttribute(TextureAttribute.Diffuse, textureOn)
        modelComponent.instance.materials.get(0).set(textureAttributeOn)
    }
}