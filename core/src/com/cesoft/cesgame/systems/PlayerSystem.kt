package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesgame.Settings
import com.cesoft.cesgame.UI.GameUI
import com.cesoft.cesgame.components.*
import com.cesoft.cesgame.UI.ControllerWidget
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.components.PlayerComponent.ALTURA
import com.cesoft.cesgame.components.PlayerComponent.FUERZA_MOVIL
import com.cesoft.cesgame.managers.GunFactory


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PlayerSystem(
	private val gameUI: GameUI,
	private val camera: Camera,
	private val bulletSystem: BulletSystem
	)
	: EntitySystem(), EntityListener, InputProcessor
{
	private lateinit var playerComponent : PlayerComponent
	private lateinit var bulletComponent : BulletComponent

	private var rayTestCB: ClosestRayResultCallback = ClosestRayResultCallback(Vector3.Zero, Vector3.Z)
	private var altura = ALTURA
	lateinit var gun: Entity

	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine?) {
		engine!!.addEntityListener(Family.all(PlayerComponent::class.java).get(), this)
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
		val tmp = Vector3()

		if(CesGame.isMobile) {
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
		tmp.set(dir).crs(camera.up).nor()
		val v = dir.cpy()
		val pitch = (Math.atan2(Math.sqrt((v.x * v.x + v.z * v.z).toDouble()), v.y.toDouble()) * MathUtils.radiansToDegrees).toFloat()
		var pr = deltaY
		/// MOBILE
		if(CesGame.isMobile)
		{
			if(pitch - pr > 150)
				pr = -(150 - pitch)
			else if(pitch - pr < 30)
				pr = pitch - 30
		}
		/// DESKTOP
		else {
			if(pitch - pr > 150)
				pr = -(150 - pitch)
			else if(pitch - pr < 30)
				pr = pitch - 30
		}
		dir.rotate(tmp, pr)
		//
		camera.direction.set(dir)
		camera.update()
	}
	//______________________________________________________________________________________________
	private fun updateTraslacion(delta: Float)
	{
		val walkDirection = Vector3(0f, 0f, 0f)

		/// Esta muerto...
		if(PlayerComponent.health <= 0f)//TODO: idem en rotacion
		{
			altura -= delta*10f
			return
		}

		//TODO: no mover si esta saltando?
		//if(playerComponent!!.isSaltando)return
		val tmp = Vector3()
		if(CesGame.isMobile) {
			if(     ControllerWidget.movementVector.y > +0.20f) walkDirection.add(camera.direction)
			else if(ControllerWidget.movementVector.y < -0.20f) walkDirection.sub(camera.direction)
			if(     ControllerWidget.movementVector.x < -0.20f) tmp.set(camera.direction).crs(camera.up).scl(-1f)
			else if(ControllerWidget.movementVector.x > +0.20f) tmp.set(camera.direction).crs(camera.up)
			walkDirection.add(tmp)
			walkDirection.scl(FUERZA_MOVIL * delta)
		}
		else {
			if(Gdx.input.isKeyPressed(Input.Keys.UP)) walkDirection.add(camera.direction)
			else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) walkDirection.sub(camera.direction)
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) tmp.set(camera.direction).crs(camera.up).scl(-1f)
			else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) tmp.set(camera.direction).crs(camera.up)
			walkDirection.add(tmp)
			walkDirection.scl(PlayerComponent.FUERZA_PC * delta)
		}
		walkDirection.y = 0f
		walkDirection.y = bulletComponent.rigidBody.linearVelocity.y
		bulletComponent.rigidBody.linearVelocity = walkDirection

	}
	//______________________________________________________________________________________________
	private fun updateSaltando() {
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
		{
			System.err.println("------------------"+getPosition().y+"----- SALTANDO :"+playerComponent.isSaltando)
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
		val pos = Vector3()
		return transform.getTranslation(pos)
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
		//System.err.println("--------------------------------------------FIRE "+lastDelta)
		deltaFire += delta
		deltaReload += delta
		if(ControllerWidget.isFiring || Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			if(deltaFire > 0.15f) {
				deltaFire = 0f
				fire()
			}
		}
		//TODO: add ammo, que se gaste, mas contador
		else if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
			if(deltaReload > 5f) {
				System.err.println("------ RELOAD ! -------------------")
				deltaReload = 0f
				GunFactory.animate(gun, GunComponent.ACTION.RELOAD)
			}
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) {
			if(deltaReload > 5f) {
				System.err.println("------ IDLE ! -------------------")
				deltaReload = 0f
				GunFactory.animate(gun, GunComponent.ACTION.IDLE)
			}
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
			if(deltaReload > 5f) {
				System.err.println("------ DRAW ! -------------------")
				deltaReload = 0f
				GunFactory.animate(gun, GunComponent.ACTION.DRAW)
			}
		}
		gun.getComponent(AnimationComponent::class.java).update(delta)
	}
	//______________________________________________________________________________________________
	//
	fun fire()
	{
		GunFactory.animate(gun, GunComponent.ACTION.SHOOT)

		//-------------------
		/// COLLISION BY RAY
		val rayFrom = Vector3()
		val rayTo = Vector3()
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
			//Gdx.app.error("CESGAME", "-------------------------- DISPARO DIO ------------------------------")
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
	fun dispose()
	{
		GunFactory.dispose()
	}
}