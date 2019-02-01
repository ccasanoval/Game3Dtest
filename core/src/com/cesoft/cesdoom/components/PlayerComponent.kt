package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object PlayerComponent : Component {

	private val tag = PlayerComponent::class.java.simpleName

	private const val MESSAGE_DURATION = 5000L
	const val MASA = .65f
	const val ALTURA = 22f
	const val RADIO = 16f
	const val FUERZA_MOVIL = 2000f
	const val FUERZA_PC = 5000f

	var isGodModeOn = false

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
		if(isGodModeOn)return
		if(System.currentTimeMillis() > lastHurt+800) {
			health -= pain
			colorAmbiente.color.set(.8f, 0f, 0f, 1f)//Pasar RenderObject y llamar a CamaraRoja(true)...
			if(health > 5)Sounds.play(Sounds.SoundType.PLAYER_HURT)
			lastHurt = System.currentTimeMillis()
		}
	}

	fun update() {
		val now = System.currentTimeMillis()
		if( ! message.isEmpty() && now > lastMessage + MESSAGE_DURATION) {
			message = ""
		}
		if(now > lastHurt+80) {
			colorAmbiente.color.set(.8f, .8f, .8f, 1f)//Pasar RenderObject y llamar a CamaraRoja(false)...
		}
	}
}
