package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Switch(private val id: Int) : Entity() {

    companion object {
        val tag: String = Switch::class.java.simpleName
        lateinit var textureOn: Texture
        lateinit var textureOff: Texture
    }

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
        isActivated = true
        //TODO: Change texture
        val modelComponent = getComponent(ModelComponent::class.java)

        val materialOn = Material(ColorAttribute.createDiffuse(Color.WHITE))
        val textureAttributeOn = TextureAttribute(TextureAttribute.Diffuse, textureOn)
        /*textureAttributeOn.scaleU = 1f
        textureAttributeOn.scaleV = 1f
        materialOn.set(textureAttributeOn)*/

        modelComponent.instance.materials.get(0).set(textureAttributeOn)
    }

    fun update(delta: Float) {
        //Log.e(tag, "update($id)------------ $offsetOpened --------------- $delta  $isOpening")

        Log.e(tag, "update($id)------------ $isActivated")
        if(isActivated) {
            //TODO: Change texture!!!!
        }
    }
}