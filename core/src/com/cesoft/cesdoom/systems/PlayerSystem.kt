package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers.addListener
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.ui.ControllerWidget
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.bullet.collision.AllHitsRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.entities.Player
import com.cesoft.cesdoom.events.EnemyEvent
import com.cesoft.cesdoom.events.GameQueue
import com.cesoft.cesdoom.events.GameEvent
import com.cesoft.cesdoom.events.RenderEvent
import com.cesoft.cesdoom.input.InputMapperFactory
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.managers.GunFactory
import com.cesoft.cesdoom.input.PlayerInput
import com.cesoft.cesdoom.ui.GameOverWidget
import com.cesoft.cesdoom.ui.GameWinWidget
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PlayerSystem(
		gameEventSignal: Signal<GameEvent>,
		private val enemyEventSignal: Signal<EnemyEvent>,
		private val renderEventSignal: Signal<RenderEvent>,
		private val colorAmbientConst: ColorAttribute,
		private val camera: Camera,
		private val bulletSystem: BulletSystem,
		private val gameWinWidget: GameWinWidget,
		private val gameOverWidget: GameOverWidget
	)
	: EntitySystem(), EntityListener
{
	companion object {
	    val tag: String = PlayerSystem::class.java.simpleName
		private const val COLOR_DELAY = 150
		private const val COLOR_LOOP = 800
	}

	private val eventQueue = GameQueue()
	init {
		gameEventSignal.add(eventQueue)
	}


	private lateinit var player: Player
	private lateinit var bulletComponent: BulletComponent
	private val rayTestAll = AllHitsRayResultCallback(Vector3.Zero, Vector3.Z)
	lateinit var gun: Entity
	private val posTemp = Vector3()
	private val posTemp2 = Vector3()

    private val inputMap = InputMapperFactory.getCes()//TODO: Global

	/// Extends EntitySystem
	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine?) {
		engine!!.addEntityListener(Family.all(PlayerComponent::class.java).get(), this)
		addListener(PlayerInput(inputMap, Gdx.input.inputProcessor))
	}

	/// Implements EntityListener
	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) {
		player = entity as Player
		bulletComponent = BulletComponent.get(player)
		resetPlayerComponent()
	}
	override fun entityRemoved(entity: Entity) = Unit

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		checkCtrl()
		checkGameOver(delta)
		checkYouWin(delta)
		updateMovement(delta)
		if( ! PlayerComponent.isDead()) {
			updateWeapon(delta)
		}
		updateCamera()
		restoreAmbientColor()

		processEvents()
	}

	private fun checkCtrl() {
        /*
		if(inputMapCtr?.isButtonPressed(CtrMappings.BTN_MENU) == true) {

		}
		else if(inputMapCtr?.isButtonPressed(CtrMappings.BTN_BACK) == true) {
			Log.e(tag, "checkCtrl:BACK:---------------------------------------------------------")
		}*/
        if(inputMap.isButtonPressed(Inputs.BACK)) {
            Log.e(tag, "checkCtrl:BACK:---------------------------------------------------------")
            //TODO: Send signal?
            //gameUi.pauseWidget.handleUpdates()
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
					healthPickup(event.value)
				}
				GameEvent.Type.ENEMY_DEAD -> {
					if(event.value > 0)
						PlayerComponent.addScore(event.value)
				}
				//else -> Unit
			}
		}
	}

	//______________________________________________________________________________________________
	private fun updateMovement(delta: Float) {
		updateRotation(delta)
		updateTranslation(delta)
		updateJumping(delta)
	}
	//______________________________________________________________________________________________
	private fun updateRotation(delta: Float) {
		if(PlayerComponent.isDead())return

		val inputVals = getInputRotation(delta)
        val deltaX = inputVals.x
        val deltaY = inputVals.y

		// Y
		val dir = camera.direction.cpy()
		dir.rotate(camera.up, deltaX)
		// X Z
		posTemp.set(dir).crs(camera.up).nor()
		val v = dir.cpy()
		val pitch = (Math.atan2(Math.sqrt((v.x*v.x + v.z*v.z).toDouble()), v.y.toDouble()) * MathUtils.radiansToDegrees).toFloat()
		var pr = deltaY

		// Cuando player no rote mirada en Y, centrar poco a poco mirada al frente
		if(deltaY == 0f) {
			pr = (pitch-90)*delta/2f
		}

		// Limitar pitch
		/// MOBILE
		//if(CesDoom.isMobile) {
			if(pitch - pr > 140)		//Angulo mirando abajo (GOOD: 120)
				pr = pitch - 140
			else if(pitch - pr < 30)	//Angulo mirando arriba (GOOD: 40)
				pr = pitch - 30
//		}
//		/// DESKTOP
//		else {
//			if(pitch - pr > 150)
//				pr = -(150 - pitch)
//			else if(pitch - pr < 30)
//				pr = pitch - 30
//		}
		dir.rotate(posTemp, pr)
		//
		camera.direction.set(dir)
		camera.update()
	}
    private fun getInputRotation(delta: Float) : Vector2 {
        val xWeight=90f
        val yWeight=50f
        var deltaX = 0f
        var deltaY = 0f

        /// SCREEN CONTROLS
        deltaX += -ControllerWidget.watchVector.x * xWeight*delta		// Velocidad angulo horizontal (GOOD: 90)
        deltaY += +ControllerWidget.watchVector.y * yWeight*delta		// Velocidad angulo vertical (GOOD: 60)

        /// GAME PAD
        val mxPad = inputMap.getAxisValue(Inputs.LOOK_X)
        val myPad = inputMap.getAxisValue(Inputs.LOOK_Y)
        if(mxPad != Inputs.Value.ZERO)
            deltaX += mxPad.value * xWeight * delta
        if(myPad != Inputs.Value.ZERO)
            deltaY += myPad.value * yWeight * delta

        /// KEYBOARD
        if(Gdx.input.deltaX != 0)
            deltaX += -Gdx.input.deltaX * 5*delta
        if(Gdx.input.deltaY != 0)
            deltaY += -Gdx.input.deltaY * 2*delta

        return Vector2(deltaX, deltaY)
    }
	//______________________________________________________________________________________________
	private fun updateTranslation(delta: Float)
	{
		posTemp.set(0f, 0f, 0f)

		if(PlayerComponent.isDead()) {
			PlayerComponent.tall -= delta*12f
			return
		}
		//TODO: no mover si esta saltando?
		//if(playerComponent!!.isSaltando)return

		updateTranslationMobile(delta)

//		if(CesDoom.isMobile) {
//			updateTranslationMobile(delta)
//		}
//		else {
//			updateTranslationDesktop(delta)
//		}
		posTemp.y = 0f
		posTemp.y = bulletComponent.rigidBody.linearVelocity.y
		bulletComponent.rigidBody.linearVelocity = posTemp
	}
	//______________________________________________________________________________________________
	private fun updateTranslationMobile(delta: Float) {
		var isMoving = false
		val offsetHorizontal = +0.60f
		val offsetVertical = +0.20f

		if(ControllerWidget.movementVector.y > +offsetVertical || inputMap.isAxisValuePositive(Inputs.MOVE_Y)) {
			posTemp.add(camera.direction)
			isMoving = true
		}
		else if(ControllerWidget.movementVector.y < -offsetVertical || inputMap.isAxisValueNegative(Inputs.MOVE_Y)) {
			posTemp.sub(camera.direction)
			isMoving = true
		}
		if(ControllerWidget.movementVector.x < -offsetHorizontal || inputMap.isAxisValuePositive(Inputs.MOVE_X)) {
			posTemp2.set(camera.direction).crs(camera.up).scl(-1f)
			isMoving = true
		}
		else if(ControllerWidget.movementVector.x > +offsetHorizontal || inputMap.isAxisValueNegative(Inputs.MOVE_X)) {
			posTemp2.set(camera.direction).crs(camera.up)
			isMoving = true
		}
		if( ! isMoving) {
			posTemp2.set(Vector3.Zero)
		}
		else {
			PlayerComponent.animFootStep(delta)
			Sounds.play(Sounds.SoundType.FOOT_STEPS)
		}
		posTemp.add(posTemp2)
		posTemp.scl(PlayerComponent.IMPULSE_MOBIL * delta)
	}
	//______________________________________________________________________________________________
	/*private fun updateTranslationDesktop(delta: Float) {
		var hayMovimiento = false
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			posTemp.add(camera.direction)
			hayMovimiento = true
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			posTemp.sub(camera.direction)
			hayMovimiento = true
		}
		when {
			Gdx.input.isKeyPressed(Input.Keys.LEFT) -> {
				posTemp2.set(camera.direction).crs(camera.up).scl(-1f)
				hayMovimiento = true
			}
			Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> {
				posTemp2.set(camera.direction).crs(camera.up)
				hayMovimiento = true
			}
			else ->
				posTemp2.set(Vector3.Zero)
		}
		if(hayMovimiento) {
			Sounds.play(Sounds.SoundType.FOOT_STEPS)
			posTemp.add(posTemp2)
		}
		posTemp.scl(PlayerComponent.IMPULSE_PC * delta)
	}*/
	//______________________________________________________________________________________________
	private fun updateJumping(delta: Float) {
		val jumpPad = inputMap.isButtonPressed(Inputs.JUMP)

		if(Gdx.input.isKeyPressed(Input.Keys.SPACE) || jumpPad) {
			Log.e(tag, "------------------"+getPosition().y+"----- SALTANDO :"+PlayerComponent.isJumping)
			if( ! PlayerComponent.isJumping) {
				PlayerComponent.isJumping = true
				val force = 40 * delta
				val vel = bulletComponent.rigidBody.linearVelocity.cpy()
				vel.y += force
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
		pos.y += PlayerComponent.tall/1.5f +PlayerComponent.yFoot
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
	private val DELAY_FIRE = 0.15f
	private val DELAY_RELOAD = 0.55f
	private var deltaFire = 100f
	private fun updateWeapon(delta: Float) {

		/*inputMapCtr?.let {
			isFiringPad = it.isButtonPressed(CtrMappings.BTN_FIRE1)
					|| it.isButtonPressed(CtrMappings.BTN_FIRE2)
					|| it.isButtonPressed(CtrMappings.BTN_FIRE3)
		}*/

		val isFiring = (ControllerWidget.isFiring
				|| Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)
				|| inputMap.isButtonPressed(Inputs.FIRE))
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
				Sounds.play(Sounds.SoundType.RIFLE)
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


	//______________________________________________________________________________________________
	private var delayDeath = 0f
	private fun checkGameOver(delta: Float) {
		if(PlayerComponent.isDead() && !Status.paused) {
			if(delayDeath == 0f) {
				Sounds.play(Sounds.SoundType.PLAYER_DYING)
				changeAmbientColor(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.0f, 0.0f, 1f))
			}
			else if(delayDeath > 2.5f) {
				Status.paused = true
				gameOverWidget.show()//TODO: clean
				Sounds.play(Sounds.SoundType.GAME_OVER)
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
				gameWinWidget.show()
			}
			else if(delayYouWin >= 1f) {
				Status.paused = true
				gameWinWidget.show()
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
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(PlayerComponent.MASS, null, shape, posTemp)
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


	private fun hurt(pain: Int) {
		if(PlayerComponent.isGodModeOn)return
		val now = System.currentTimeMillis()
		if(now > lastColorChange+COLOR_LOOP) {
			PlayerComponent.hurt(pain)
			changeAmbientColor(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.0f, 0.0f, 1f))
			if(Settings.isVibrationEnabled)
				Gdx.input.vibrate(200)//Gdx.input.vibrate(new long[] { 0, 200, 200, 200}, -1) //Needs VIBRATE permission
			if(PlayerComponent.health > 5)
				Sounds.play(Sounds.SoundType.PLAYER_HURT)
		}
	}
	private fun heal(value: Int) {
		PlayerComponent.heal(value)
		changeAmbientColor(ColorAttribute(ColorAttribute.AmbientLight, 0f, .8f, 0f, 1f))
		Sounds.play(Sounds.SoundType.HEALTH_RELOAD)
	}
	private var isChangingColor = false
	private var lastColorChange = 0L
	private fun changeAmbientColor(color: ColorAttribute) {
		isChangingColor = true
		lastColorChange = System.currentTimeMillis()
		renderEventSignal.dispatch(RenderEvent(RenderEvent.Type.SET_AMBIENT_COLOR, color))
	}
	private fun restoreAmbientColor() {
		if(PlayerComponent.isDead())return
		val now = System.currentTimeMillis()
		if(isChangingColor && now > lastColorChange+COLOR_DELAY) {
			isChangingColor = false
			renderEventSignal.dispatch(RenderEvent(RenderEvent.Type.SET_AMBIENT_COLOR, colorAmbientConst))
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
		Log.e(tag, "resetPlayerComponent------------------------------------- ${PlayerComponent.currentLevel}")
		PlayerComponent.isWinning = false
		PlayerComponent.isJumping = false
		PlayerComponent.isReloading = false
		PlayerComponent.tall = PlayerComponent.TALL
		if(PlayerComponent.currentLevel == 0) {
			ammoReset()
			PlayerComponent.score = 0
			PlayerComponent.resetHealth()
		}
	}

	private fun youWin() {
		PlayerComponent.isWinning = true
	}
	private fun ammoPickup(value: Int) {
		PlayerComponent.isReloading = true
		PlayerComponent.ammo += value
	}
	private fun healthPickup(value: Int) {
		heal(value)
	}

	private fun ammoReset() {
		PlayerComponent.isReloading = true
		PlayerComponent.ammo = AmmoComponent.MAGAZINE_CAPACITY
	}

}