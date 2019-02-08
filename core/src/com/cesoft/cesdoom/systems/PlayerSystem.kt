package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers.addListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.ui.ControllerWidget
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.bullet.collision.AllHitsRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.entities.Player
import com.cesoft.cesdoom.events.EnemyEvent
import com.cesoft.cesdoom.events.GameQueue
import com.cesoft.cesdoom.events.GameEvent
import com.cesoft.cesdoom.events.RenderEvent
import com.cesoft.cesdoom.managers.GunFactory
import com.cesoft.cesdoom.util.Log



////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Joystick!!
class PlayerSystem(
		gameEventSignal: Signal<GameEvent>,
		private val enemyEventSignal: Signal<EnemyEvent>,
		private val renderEventSignal: Signal<RenderEvent>,
		private val colorAmbientConst: ColorAttribute,
		private val camera: Camera,
		private val bulletSystem: BulletSystem
	)
	: EntitySystem(), EntityListener, InputProcessor, ControllerListener
{
	companion object {
	    val tag: String = PlayerSystem::class.java.simpleName
	}

	private val eventQueue = GameQueue()
	init {
		gameEventSignal.add(eventQueue)
	}


	//----------------------------------------------------------------------------------------------
	///// ControllerListener
	// TODO: REFACTOR to class que agrupe todo input...
	enum class Direccion { NONE, ATRAS, ADELANTE, IZQUIERDA, DERECHA }
	private var xPad: Direccion = Direccion.NONE
	private var yPad: Direccion = Direccion.NONE
	private var fire1: Boolean = false
	private var fire2: Boolean = false
	private var btnA: Boolean = false
	private var btnB: Boolean = false
	private var btnC: Boolean = false
	private var btnD: Boolean = false
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





	lateinit var player: Player
	private lateinit var bulletComponent: BulletComponent

	private val rayTestAll = AllHitsRayResultCallback(Vector3.Zero, Vector3.Z)

	lateinit var gun: Entity

	private val posTemp = Vector3()
	private val posTemp2 = Vector3()

	/// Extends EntitySystem
	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine?) {
		engine!!.addEntityListener(Family.all(PlayerComponent::class.java).get(), this)
		addListener(this)
	}

	/// Implements EntityListener
	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) {
		player = entity as Player
		bulletComponent = BulletComponent.get(player)
		resetPlayerComponent()
	}
	override fun entityRemoved(entity: Entity) {}

	//______________________________________________________________________________________________
	override fun keyDown(keycode: Int): Boolean = false
	override fun keyUp(keycode: Int): Boolean = false
	override fun keyTyped(character: Char): Boolean = false
	override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
	override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
	override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false
	override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false
	override fun scrolled(amount: Int): Boolean = false


	//______________________________________________________________________________________________
	private var justBorn = true//TODO: when changing levels?? maintain health and ammo?
	override fun update(delta: Float) {
		updateMovement(delta)
		updateWeapon(delta)
		checkGameOver(delta)
		checkYouWin(delta)
		updateCamera()
		restoreAmbientColor()

		processEvents()
		if(justBorn) {
			justBorn = false
			PlayerComponent.resetHealth()
			ammoReset()
		}
	}

	//______________________________________________________________________________________________
	private fun processEvents() {
		for(event in eventQueue.events) {
			when(event.type) {
				GameEvent.Type.YOU_WIN -> {
					youWin()
				}
				GameEvent.Type.PLAYER_HURT -> {
					hurt(event.value)
				}
				GameEvent.Type.AMMO_PICKUP -> {
					ammoPickup(event.value)
				}
				GameEvent.Type.HEALTH_PICKUP -> {
					heal(event.value)
				}
				GameEvent.Type.ENEMY_DEAD -> {
					if(event.value > 0)
						addScore(event.value)
				}
				//else -> Unit
			}
		}
	}

	//______________________________________________________________________________________________
	private fun updateMovement(delta: Float) {
		updateRotation(delta)
		updateTranslation(delta)
		updateJumping()
	}
	//______________________________________________________________________________________________
	private fun updateRotation(delta: Float)
	{
		if(isDead())return

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
			if(pitch - pr > 120)//Angulo mirando abajo //TODO:put back 120 , TEST=160
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
	private fun updateTranslation(delta: Float)
	{
		posTemp.set(0f, 0f, 0f)

		if(isDead()) {
			PlayerComponent.tall -= delta*12f
			return
		}

		//TODO: no mover si esta saltando?
		//if(playerComponent!!.isSaltando)return
		if(CesDoom.isMobile) {
			updateTranslationMobile(delta)
		}
		else {
			updateTranslationDesktop(delta)
		}
		posTemp.y = 0f
		posTemp.y = bulletComponent.rigidBody.linearVelocity.y
		bulletComponent.rigidBody.linearVelocity = posTemp
	}
	//______________________________________________________________________________________________
	private fun updateTranslationMobile(delta: Float) {
		var hayMovimiento = false
		if(ControllerWidget.movementVector.y > +0.20f || yPad == Direccion.ADELANTE) {
			posTemp.add(camera.direction)
			hayMovimiento = true
		}
		else if(ControllerWidget.movementVector.y < -0.20f || yPad == Direccion.ATRAS) {
			posTemp.sub(camera.direction)
			hayMovimiento = true
		}
		if(ControllerWidget.movementVector.x < -0.25f || xPad == Direccion.IZQUIERDA) {
			posTemp2.set(camera.direction).crs(camera.up).scl(-1f)
			hayMovimiento = true
		}
		else if(ControllerWidget.movementVector.x > +0.25f || xPad == Direccion.DERECHA) {
			posTemp2.set(camera.direction).crs(camera.up)
			hayMovimiento = true
		}
		if( ! hayMovimiento)
			posTemp2.set(Vector3.Zero)
		else {
			animFootStep(delta)
			if(Settings.isSoundEnabled)// && ! Assets.getSoundFootSteps().isPlaying)
				Sounds.play(Sounds.SoundType.FOOT_STEPS)
		}
		posTemp.add(posTemp2)
		posTemp.scl(PlayerComponent.IMPULSE_MOBIL * delta)
	}
	//______________________________________________________________________________________________
	private fun updateTranslationDesktop(delta: Float) {
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) posTemp.add(camera.direction)
		else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) posTemp.sub(camera.direction)
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) posTemp2.set(camera.direction).crs(camera.up).scl(-1f)
		else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) posTemp2.set(camera.direction).crs(camera.up)
		else posTemp2.set(Vector3.Zero)
		if( ! posTemp2.isZero)
			Sounds.play(Sounds.SoundType.FOOT_STEPS)
		posTemp.add(posTemp2)
		posTemp.scl(PlayerComponent.IMPULSE_PC * delta)
	}
	//______________________________________________________________________________________________
	private fun updateJumping() {
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE) || btnA)
		{
			Log.e(tag, "------------------"+getPosition().y+"----- SALTANDO :"+PlayerComponent.isJumping)
			if( ! PlayerComponent.isJumping) {
				PlayerComponent.isJumping = true
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
	private fun updateCamera() {
		val pos = getPosition()
		pos.y += PlayerComponent.tall/1.5f +yFoot
		pos.x += camera.direction.x*PlayerComponent.RADIO/2 // camara adelantada a colision, para no disparar a self bullet body
		pos.z += camera.direction.z*PlayerComponent.RADIO/2
		camera.position.set(pos)
		camera.update()
	}


	//______________________________________________________________________________________________
	private fun discountAmmo() {
		if( ! PlayerComponent.isGodModeOn)
			PlayerComponent.ammo--
	}
	//______________________________________________________________________________________________
	//TODO: quiza depende de GunComponent?
	private val DELAY_FIRE = 0.15f
	private val DELAY_RELOAD = 0.35f
	private var deltaFire = 100f
	private fun updateWeapon(delta: Float) {
		// Gdx.input.isTouched

		val isFiring = (ControllerWidget.isFiring || Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || fire1)
		deltaFire += delta

		if(PlayerComponent.isReloading) {
			PlayerComponent.isReloading = false
			GunFactory.animate(gun, GunComponent.ACTION.RELOAD)
			Sounds.play(Sounds.SoundType.AMMO_RELOAD)
			deltaFire = -DELAY_RELOAD
			//return
		}
		else if(isFiring && deltaFire > DELAY_FIRE) {
			if(PlayerComponent.ammo > 0) {
				deltaFire = 0f

				GunFactory.playSound()
				GunFactory.animate(gun, GunComponent.ACTION.SHOOT)
				discountAmmo()
				checkBulletKillEnemy()
			}
			else {
				Sounds.play(Sounds.SoundType.NO_AMMO)
			}
		}

		/*else if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || btnB) {
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
		}*/
		//gun.getComponent(AnimationComponent::class.java).update(delta)
		AnimationComponent.get(gun).update(delta)
	}
	//______________________________________________________________________________________________
	//
	private val rayFrom = Vector3()
	private val rayTo = Vector3()
	private fun checkBulletKillEnemy() {

		//-------------------
		/// COLLISION BY RAY
		val ray = camera.getPickRay((Gdx.graphics.width / 2).toFloat(), (Gdx.graphics.height / 2).toFloat())
		rayFrom.set(ray.origin)
		rayTo.set(ray.direction).scl(250f).add(rayFrom)
		//
		rayTestAll.collisionObject = null
		rayTestAll.closestHitFraction = .8f//1
		rayTestAll.setRayFromWorld(rayFrom)
		rayTestAll.setRayToWorld(rayTo)
		if(rayTestAll.collisionObjects != null)
			rayTestAll.collisionObjects.clear()
		//rayTestAll.collisionFilterMask = rayTestAll.collisionFilterMask or BulletComponent.ENEMY_FLAG
		//rayTestAll.collisionFilterGroup = rayTestAll.collisionFilterGroup or BulletComponent.ENEMY_FLAG
		bulletSystem.collisionWorld.rayTest(rayFrom, rayTo, rayTestAll)

		if(rayTestAll.hasHit()) {
			for(i in rayTestAll.collisionObjects.size()-1 downTo 0 ) {
				val collider = rayTestAll.collisionObjects.atConst(i)
				if(collider is btRigidBody) {
					if(collider.userValue == BulletComponent.SCENE_FLAG
							|| collider.userValue == BulletComponent.GATE_FLAG
							|| collider.userValue == BulletComponent.SWITCH_FLAG
							//|| collider.userValue == BulletComponent.GROUND_FLAG
							|| collider.userValue == BulletComponent.AMMO_FLAG
							|| collider.userValue == BulletComponent.HEALTH_FLAG) {
						// Dio primero con una pared, bala muerta
						break
					}
					if(collider.userValue == BulletComponent.ENEMY_FLAG) {
						// Dio primero con enemigo, bala buena
						val entity = collider.userData as Enemy
						val currentAmmoPain = 50//TODO: Diferentes municiones con diferentes penetraciones, difrentes armas?
						val event = EnemyEvent(EnemyEvent.Type.HURT, entity, currentAmmoPain)
						enemyEventSignal.dispatch(event)
						break
					}
				}
			}
		}

	}


	private var isFootUp = false
	private var yFoot = 0f
	private fun animFootStep(delta: Float)
	{
		if(isFootUp) {
			yFoot += delta*7
			if(yFoot > 0.8f)
				isFootUp = false
		}
		else {
			yFoot -= delta*7
			if(yFoot < -0.8f)
				isFootUp = true
		}
	}

	//______________________________________________________________________________________________
	private var delayDeath = 0f
	private fun checkGameOver(delta: Float) {
		if(isDead() && !Status.paused) {
			if(delayDeath == 0f) {
				Sounds.play(Sounds.SoundType.PLAYER_DYING)
				changeAmbientColor(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.0f, 0.0f, 1f))
			}
			else if(delayDeath >= 2f) {
				Status.paused = true
				CesDoom.instance.gameUI.gameOverWidget.show()
				Sounds.play(Sounds.SoundType.GAME_OVER)
				changeAmbientColor(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.0f, 0.0f, 1f))
			}
			delayDeath += delta
		}
	}
	//______________________________________________________________________________________________
	private var delayYouWin = 0f
	private fun checkYouWin(delta: Float) {
		if(PlayerComponent.isWinning && !Status.paused) {
			if(delayYouWin == 0f) {
				Sounds.play(Sounds.SoundType.YOU_WIN)
				//TODO: Why can we delay this with delayYouWin, it crashes
				Status.paused = true
				CesDoom.instance.gameUI.gameWinWidget.show()
			}
			else if(delayYouWin >= 1f) {
				Status.paused = true
				CesDoom.instance.gameUI.gameWinWidget.show()
				//GameUI.gameOverWidget.show()
			}
			delayYouWin += delta
		}
	}



	////////////////////////////////////////////////////////////////////////////////////////////////

	fun createPlayer(pos: Vector3, engine: Engine): Player {

		/// Entity
		val entity = Player()

		/// Component
		entity.add(PlayerComponent())

		/// Position and Shape
		val posTemp = Vector3()
		val shape = btSphereShape(PlayerComponent.RADIO)//btCylinderShape(Vector3(3f,ALTURA/2,3f))//btCapsuleShape(6f, ALTURA)//
		shape.calculateLocalInertia(PlayerComponent.TALL, posTemp)

		/// Collision
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(PlayerComponent.MASE, null, shape, posTemp)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(Matrix4().translate(pos))
		// The onContactAdded callback will only be triggered if at least one of the two colliding bodies has the CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter =
				BulletComponent.SWITCH_FLAG or
						BulletComponent.GATE_FLAG or
						BulletComponent.YOU_WIN_FLAG or
						BulletComponent.AMMO_FLAG or
						BulletComponent.HEALTH_FLAG
		//BulletComponent.GROUND_FLAG or BulletComponent.ENEMY_FLAG or BulletComponent.SCENE_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.PLAYER_FLAG
		rigidBody.userValue = BulletComponent.PLAYER_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 0f
		rigidBody.rollingFriction = 1000000000f
		entity.add(BulletComponent(rigidBody, bodyInfo))
		//
		engine.addEntity(entity)
		return entity
	}


	////////////////////////////////////////////////////////////////////////////////////////////////


	private val COLOR_DELAY = 100
	private val COLOR_LOOP = 800
	private fun hurt(pain: Int) {
		if(PlayerComponent.isGodModeOn)return
		val now = System.currentTimeMillis()
		if(now > lastColorChange+COLOR_LOOP) {
			PlayerComponent.hurt(pain)
			changeAmbientColor(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.0f, 0.0f, 1f))
			if(PlayerComponent.health > 5)
				Sounds.play(Sounds.SoundType.PLAYER_HURT)
			Log.e(tag, "hurt----------------------------------")
		}
	}
	private fun heal(value: Int) {
		PlayerComponent.heal(value)
		changeAmbientColor(ColorAttribute(ColorAttribute.AmbientLight, 0f, .8f, 0f, 1f))
		Sounds.play(Sounds.SoundType.PLAYER_HEAL)
		Log.e(tag, "heal----------------------------------")
	}
	private var isChangingColor = false
	private var lastColorChange = 0L
	private fun changeAmbientColor(color: ColorAttribute) {
		isChangingColor = true
		lastColorChange = System.currentTimeMillis()
		renderEventSignal.dispatch(RenderEvent(RenderEvent.Type.SET_AMBIENT_COLOR, color))
	}
	private fun restoreAmbientColor() {
		val now = System.currentTimeMillis()
		if(isChangingColor && now > lastColorChange+COLOR_DELAY) {
			isChangingColor = false
			renderEventSignal.dispatch(RenderEvent(RenderEvent.Type.SET_AMBIENT_COLOR, colorAmbientConst))
			Log.e(tag, "restoreColor----------------------------------")
		}
	}

	//______________________________________________________________________________________________
	private fun getPosition() : Vector3 {
		val transform = Matrix4()
		BulletComponent.get(player).rigidBody.motionState.getWorldTransform(transform)
		val posTemp = Vector3()
		return transform.getTranslation(posTemp)
	}




	private fun resetPlayerComponent() {
		PlayerComponent.isWinning = false
		PlayerComponent.isJumping = false
		PlayerComponent.isReloading = false
		PlayerComponent.score = 0
		PlayerComponent.tall = PlayerComponent.TALL
		PlayerComponent.resetHealth()
		//playerComponent.ammo = AmmoComponent.MAGAZINE_CAPACITY
		//playerComponent.colorAmbiente = colorAmbiente
	}

	private fun isDead() = PlayerComponent.health < 1
	private fun addScore(pts: Int) { PlayerComponent.score += pts }
	//private fun jump(v: Boolean) { isJumping = v }



	private fun youWin() {
		PlayerComponent.isWinning = true
	}
	private fun ammoPickup(value: Int) {
		PlayerComponent.isReloading = true
		PlayerComponent.ammo += value//AmmoComponent.MAGAZINE_CAPACITY
	}
	private fun healthPickup(value: Int) {
		PlayerComponent.isReloadingHealth = true
		PlayerComponent.heal(value)//HealthComponent.DRUG_CAPACITY)
	}

	private fun ammoReset() {
		PlayerComponent.isReloading = true
		PlayerComponent.ammo = AmmoComponent.MAGAZINE_CAPACITY
	}

}