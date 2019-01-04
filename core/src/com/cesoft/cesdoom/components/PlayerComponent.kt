package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object PlayerComponent : Component
{
	var isSaltando = false
	var health: Float = 100f
	var score: Int = 0
	const val MASA = .65f
	const val ALTURA = 15f
	const val RADIO = 12f
	const val FUERZA_MOVIL = 2000f
	const val FUERZA_PC = 5000f

	lateinit var colorAmbiente : ColorAttribute

	//______________________________________________________________________________________________
	private var lastHurt = 0L
	fun hurt(pain: Float) {
		if(System.currentTimeMillis() > lastHurt+800) {
			health -= pain
			colorAmbiente.color.set(.8f, 0f, 0f, 1f)//Pasar RenderObject y llamar a CamaraRoja(true)...
			lastHurt = System.currentTimeMillis()
		}
	}

	//______________________________________________________________________________________________
	fun update() {
		if(lastHurt+50 < System.currentTimeMillis())
			colorAmbiente.color.set(.8f, .8f, .8f, 1f)//Pasar RenderObject y llamar a CamaraRoja(false)...
	}
}
