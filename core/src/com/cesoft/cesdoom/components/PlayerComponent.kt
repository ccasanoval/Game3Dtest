package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object PlayerComponent : Component
{
	const val MASA = .65f
	const val ALTURA = 22f
	const val RADIO = 12f
	const val FUERZA_MOVIL = 2000f
	const val FUERZA_PC = 5000f
	const val MESSAGE_DURATION = 5000f

	var isWinning = false
		private set
	var health: Float = 100f
		private set
	var score: Int = 0
		private set
	var isJumping = false
		//private set
	lateinit var colorAmbiente : ColorAttribute

	private var lastMessage = 0L
	var message: String = ""
		set(value) {
			field = value
			lastMessage = System.currentTimeMillis()
			Log.e("PlayerComponent", "message:set:----------------------------------- $lastMessage")
		}

	//TODO: pasar funciones a entidad Player ?
	fun ini(colorAmbiente: ColorAttribute) {
		this.isWinning = false
		this.isJumping = false
		this.health = 100f
		this.score = 0
		this.colorAmbiente = colorAmbiente
	}

	fun isDead() = health < 1
	fun winning() { isWinning = true }
	fun addScore(pts: Int) { score += pts }
	//fun jump(v: Boolean) { isJumping = v }

	private var lastHurt = 0L
	fun hurt(pain: Float) {
		if(System.currentTimeMillis() > lastHurt+800) {
			health -= pain
			colorAmbiente.color.set(.8f, 0f, 0f, 1f)//Pasar RenderObject y llamar a CamaraRoja(true)...
			lastHurt = System.currentTimeMillis()
		}
	}

	fun update() {
		if(!message.isEmpty() && lastMessage+MESSAGE_DURATION < System.currentTimeMillis()) {
			message = ""
		}
		if(lastHurt+50 < System.currentTimeMillis())
			colorAmbiente.color.set(.8f, .8f, .8f, 1f)//Pasar RenderObject y llamar a CamaraRoja(false)...
	}
}
