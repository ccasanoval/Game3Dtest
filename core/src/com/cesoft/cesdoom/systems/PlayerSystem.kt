package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers.addListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.UI.GameUI
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.UI.ControllerWidget
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.components.PlayerComponent.ALTURA
import com.cesoft.cesdoom.components.PlayerComponent.FUERZA_MOVIL
import com.cesoft.cesdoom.managers.GunFactory
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PlayerSystem(
	private val gameUI: GameUI,
	private val camera: Camera,
	private val bulletSystem: BulletSystem
	)
	: EntitySystem(), EntityListener, InputProcessor, ControllerListener
{

	companion object {
	    val tag: String = PlayerSystem::class.java.simpleName
	}

	///// ControllerListener
	// TODO: REFACTOR to class que agrupe todo input...
	enum class Direccion { NONE, ATRAS, ADELANTE, IZQUIERDA, DERECHA }
	var xPad: Direccion = Direccion.NONE
	var yPad: Direccion = Direccion.NONE
	var fire1: Boolean = false
	var fire2: Boolean = false
	var btnA: Boolean = false
	var btnB: Boolean = false
	var btnC: Boolean = false
	var btnD: Boolean = false
	/////-------------------------------------------------------------------------------------------
	override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
		//Log.e(tag, "axisMoved:------------"+controller?.name+" : "+axisCode+" : "+value)
		if(axisCode == com.badlogic.gdx.controllers.mappings.Ouya.AXIS_LEFT_X) {
			xPad = when {
				value > 0 -> Direccion.DERECHA
				value < 0 -> Direccion.IZQUIERDA
				else -> Direccion.NONE
			}
		}
		else if(axisCode == com.badlogic.gdx.controllers.mappings.Ouya.AXIS_LEFT_Y) {
			yPad = when {
				value > 0 -> Direccion.ATRAS
				value < 0 -> Direccion.ADELANTE
				else -> Direccion.NONE
			}
		}
		return false
	}
	override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
		when(buttonCode) {
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_R2 -> fire1 = false
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_L2 -> fire2 = false
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_A -> btnA = false
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_U -> btnB = false
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_O -> btnC = false
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_Y -> btnD = false
			else -> Log.e(tag, "buttonUp:----------------"+controller?.name+" : "+buttonCode)
		}
		//else if(buttonCode == com.badlogic.gdx.controllers.mappings.Xbox.A)
		return false
	}
	override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
		when(buttonCode) {
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_R2 -> fire1 = true
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_L2 -> fire2 = true
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_A -> btnA = true
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_U -> btnB = true
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_O -> btnC = true
			com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_Y -> btnD = true
			else -> Log.e(tag, "buttonDown:---------------"+controller?.name+" : "+buttonCode)
		}
		return false
	}
	////
	override fun connected(controller: Controller?) {
		Log.e(tag, "connected:------------"+controller?.name)
	}
	override fun disconnected(controller: Controller?) {
		Log.e(tag, "disconnected:------------"+controller?.name)
	}
	////
	override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
		Log.e(tag, "accelerometerMoved:------------"+controller?.name+" : "+accelerometerCode+" : "+value)
		return false
	}
	override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
		Log.e(tag, "ySliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
		return false
	}
	override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
		Log.e(tag, "xSliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
		return false
	}
	override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
		Log.e(tag, "povMoved:------------"+controller?.name+" : "+povCode+" : "+value)
		return false
	}
	//////////

	private lateinit var playerComponent : PlayerComponent
	private lateinit var bulletComponent : BulletComponent

	private var rayTestCB: ClosestRayResultCallback = ClosestRayResultCallback(Vector3.Zero, Vector3.Z)
	private var altura = ALTURA
	lateinit var gun: Entity

	private val posTemp = Vector3()
	private val posTemp2 = Vector3()

	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine?) {
		engine!!.addEntityListener(Family.all(PlayerComponent::class.java).get(), this)
		addListener(this)
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		updateMovement(delta)
		updateDisparo(delta)
		updateStatus()
		checkGameOver(delta)
		updateCamara()
		PlayerComponent.update()
	}

	//______________________________________________________________________________________________
	private fun updateMovement(delta: Float) {
		updateRotacion(delta)
		updateTraslacion(delta)
		updateSaltando()
	}
	//______________________________________________________________________________________________
	private fun updateRotacion(delta: Float)
	{
		if(PlayerComponent.health <= 0f)return

		val deltaX: Float
		val deltaY: Float

		if(CesDoom.isMobile) {
			deltaX = -ControllerWidget.watchVector.x * 85f * delta
			deltaY = ControllerWidget.watchVector.y * 85f * delta
		}
		else {
			deltaX = -Gdx.input.deltaX * 5f * delta
			deltaY = -Gdx.input.deltaY * 2f * delta
		}

		// Y
		val dir = camera.direction.cpy()
		dir.rotate(camera.up, deltaX)
		// X Z
		posTemp.set(dir).crs(camera.up).nor()
		val v = dir.cpy()
		val pitch = (Math.atan2(Math.sqrt((v.x * v.x + v.z * v.z).toDouble()), v.y.toDouble()) * MathUtils.radiansToDegrees).toFloat()
		var pr = deltaY
		/// MOBILE
		if(CesDoom.isMobile)
		{
			if(pitch - pr > 120)//Angulo mirando abajo
				pr = pitch - 120
			else if(pitch - pr < 40)//Angulo mirando arriba
				pr = pitch - 40
			//Log.e(tag, "-------------------------------- PITCH: "+pitch)
		}
		/// DESKTOP
		else {
			if(pitch - pr > 150)
				pr = -(150 - pitch)
			else if(pitch - pr < 30)
				pr = pitch - 30
		}
		dir.rotate(posTemp, pr)
		//
		camera.direction.set(dir)
		camera.update()
	}
	//______________________________________________________________________________________________
	private fun updateTraslacion(delta: Float)
	{
		posTemp.set(0f, 0f, 0f)

		/// Esta muerto...
		if(PlayerComponent.health <= 0f)//TODO: idem en rotacion
		{
			altura -= delta*10f
			return
		}

		//TODO: no mover si esta saltando?
		//if(playerComponent!!.isSaltando)return
		if(CesDoom.isMobile) {
			var hayMovimiento = false
			if(ControllerWidget.movementVector.y > +0.20f || yPad == Direccion.ADELANTE) {
				posTemp.add(camera.direction)
				hayMovimiento = true
			}
			else if(ControllerWidget.movementVector.y < -0.20f || yPad == Direccion.ATRAS) {
				posTemp.sub(camera.direction)
				hayMovimiento = true
			}
			if(     ControllerWidget.movementVector.x < -0.25f || xPad == Direccion.IZQUIERDA) {
				posTemp2.set(camera.direction).crs(camera.up).scl(-1f)
				hayMovimiento = true
			}
			else if(ControllerWidget.movementVector.x > +0.25f || xPad == Direccion.DERECHA) {
				posTemp2.set(camera.direction).crs(camera.up)
				hayMovimiento = true
			}
			if( ! hayMovimiento)
				posTemp2.set(0f,0f,0f)
			posTemp.add(posTemp2)
			posTemp.scl(FUERZA_MOVIL * delta)
		}
		else {
			if(Gdx.input.isKeyPressed(Input.Keys.UP)) posTemp.add(camera.direction)
			else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) posTemp.sub(camera.direction)
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) posTemp2.set(camera.direction).crs(camera.up).scl(-1f)
			else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) posTemp2.set(camera.direction).crs(camera.up)
			else posTemp2.set(0f,0f,0f)
			posTemp.add(posTemp2)
			posTemp.scl(PlayerComponent.FUERZA_PC * delta)
		}
		posTemp.y = 0f
		posTemp.y = bulletComponent.rigidBody.linearVelocity.y
		bulletComponent.rigidBody.linearVelocity = posTemp

	}
	//______________________________________________________________________________________________
	private fun updateSaltando() {
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE) || btnA)
		{
			Log.e(tag, "------------------"+getPosition().y+"----- SALTANDO :"+playerComponent.isSaltando)
			if( ! playerComponent.isSaltando) {
				playerComponent.isSaltando = true
				val fuerza = 40f
				val vel = bulletComponent.rigidBody.linearVelocity.cpy()
				vel.y += fuerza
				bulletComponent.rigidBody.linearVelocity = vel
				//bulletComponent.rigidBody.applyCentralImpulse(Vector3.Y.scl(fuerza))
			}
		}
		//TODO: utilizar Ray para saltando?
		//playerComponent.isSaltando = getPosition().y > ALTURA/6 ==> No vale con rampas!!!
	}
	//______________________________________________________________________________________________
	private fun updateCamara()
	{
		val pos = getPosition()
		pos.y += altura/1.5f
		pos.x += camera.direction.x*PlayerComponent.RADIO/2 // camara adelantada a colision, para no disparar a self bullet body
		pos.z += camera.direction.z*PlayerComponent.RADIO/2
		camera.position.set(pos)
		camera.update()
	}
	//______________________________________________________________________________________________
	private fun getPosition() : Vector3
	{
		val transform = Matrix4()
		bulletComponent.rigidBody.motionState.getWorldTransform(transform)
		//val pos = Vector3()
		return transform.getTranslation(posTemp)
	}

	//______________________________________________________________________________________________
	private fun updateStatus() {
		gameUI.healthWidget.setValue(PlayerComponent.health)
	}

	//______________________________________________________________________________________________
	//TODO: quiza depende de GunComponent?
	private var deltaFire = 100f
	private var deltaReload = 100f
	private fun updateDisparo(delta: Float)
	{
		// Gdx.input.isTouched
		deltaFire += delta
		deltaReload += delta
		if(ControllerWidget.isFiring || Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || fire1) {
			if(deltaFire > 0.15f) {
				deltaFire = 0f
				fire()
			}
		}
		//TODO: add ammo, que se gaste, mas contador
		else if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || btnB) {
			if(deltaReload > 5f) {
				deltaReload = 0f
				GunFactory.animate(gun, GunComponent.ACTION.RELOAD)
			}
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) {
			if(deltaReload > 5f) {
				deltaReload = 0f
				GunFactory.animate(gun, GunComponent.ACTION.IDLE)
			}
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
			if(deltaReload > 5f) {
				deltaReload = 0f
				GunFactory.animate(gun, GunComponent.ACTION.DRAW)
			}
		}
		gun.getComponent(AnimationComponent::class.java).update(delta)
	}
	//______________________________________________________________________________________________
	//
	private val rayFrom = Vector3()
	private val rayTo = Vector3()
	private fun fire()
	{
		GunFactory.animate(gun, GunComponent.ACTION.SHOOT)

		//-------------------
		/// COLLISION BY RAY
		val ray = camera.getPickRay((Gdx.graphics.width / 2).toFloat(), (Gdx.graphics.height / 2).toFloat())
		rayFrom.set(ray.origin)
		rayTo.set(ray.direction).scl(250f).add(rayFrom)
		rayTestCB.collisionObject = null
		rayTestCB.closestHitFraction = 1f
		rayTestCB.setRayFromWorld(rayFrom)
		rayTestCB.setRayToWorld(rayTo)
		bulletSystem.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB)
		if(rayTestCB.hasHit())
		{
			//Gdx.app.error("CesDoom", "-------------------------- DISPARO DIO ------------------------------")
			val entity = rayTestCB.collisionObject.userData as Entity
			/// Enemy
			entity.getComponent(StatusComponent::class.java)?.hurt()
			/// Draw shot on Wall or Enemy
			//TODO: draw nubecilla de humo !!!!!!!!!!!!!!!! con particles?
			/*val pos = Vector3()
			rayTestCB.getHitPointWorld(pos)

			val mb = ModelBuilder()
			val material = Material(ColorAttribute.createDiffuse(Color.RED))
			val flags = VertexAttributes.Usage.ColorUnpacked or VertexAttributes.Usage.Position
			val model : Model = mb.createBox(.5f, .5f, .5f, material, flags.toLong())
			val modelComponent = ModelComponent(model, pos)
			val entityDest = Entity()
			entityDest.add(modelComponent)
			engine!!.addEntity(entityDest)*/
		}
	}

	//______________________________________________________________________________________________
	private var delayDeath = 0f
	private fun checkGameOver(delta: Float) {
		if(PlayerComponent.health <= 0 && !Settings.paused) {
			if(delayDeath > 2f)
			{
				Settings.paused = true
				gameUI.gameOverWidget.gameOver()
			}
			delayDeath += delta
		}
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) {
		playerComponent = entity.getComponent(PlayerComponent::class.java)
		bulletComponent = entity.getComponent(BulletComponent::class.java)
	}

	//______________________________________________________________________________________________
	override fun entityRemoved(entity: Entity) {}
	override fun keyDown(keycode: Int): Boolean = false
	override fun keyUp(keycode: Int): Boolean = false
	override fun keyTyped(character: Char): Boolean = false
	override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
	override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
	override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false
	override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false
	override fun scrolled(amount: Int): Boolean = false

	//______________________________________________________________________________________________
	fun dispose() { }
}