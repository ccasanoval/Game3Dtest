package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesgame.Settings
import com.cesoft.cesgame.UI.GameUI
import com.cesoft.cesgame.components.*
import com.cesoft.cesgame.managers.ControllerWidget
import com.badlogic.gdx.math.MathUtils
import com.cesoft.cesgame.components.PlayerComponent.ALTURA
import com.cesoft.cesgame.components.PlayerComponent.FUERZA_MOVIL
import com.cesoft.cesgame.managers.EntityFactory
import com.cesoft.cesgame.managers.GunFactory


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PlayerSystem(private val gameUI: GameUI, private val camera: Camera)
	: EntitySystem(), EntityListener, InputProcessor
{
	private lateinit var playerComponent : PlayerComponent
	private lateinit var bulletComponent : BulletComponent

	//private var rayTestCB: ClosestRayResultCallback = ClosestRayResultCallback(Vector3.Zero, Vector3.Z)
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
		checkGameOver()
		updateCamara()
	}

	//______________________________________________________________________________________________
	private fun updateMovement(delta: Float) {
		updateRotacion(delta)
		updateTraslacion(delta)
		updateSalto()
	}
	//______________________________________________________________________________________________
	private fun updateRotacion(delta: Float)
	{
		val deltaX: Float
		val deltaY: Float
		val tmp = Vector3()

		if(Gdx.app.type == Application.ApplicationType.Android) {
			deltaX = -ControllerWidget.watchVector.x * 80f * delta
			deltaY = ControllerWidget.watchVector.y * 40f * delta
		}
		else {
			deltaX = -Gdx.input.deltaX * 8f * delta
			deltaY = -Gdx.input.deltaY * 5f * delta
		}

		// Y
		val dir = camera.direction.cpy()
		dir.rotate(camera.up, deltaX)
		// X Z
		tmp.set(dir).crs(camera.up).nor()
		val v = dir.cpy()
		val pitch = (Math.atan2(Math.sqrt((v.x * v.x + v.z * v.z).toDouble()), v.y.toDouble()) * MathUtils.radiansToDegrees).toFloat()
		var pr = deltaY
		if(Gdx.app.type == Application.ApplicationType.Android)
		{
			if(pitch - pr > 110)
				pr = -(110 - pitch)
			else if(pitch - pr < 60)
				pr = pitch - 60
		}
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
		//TODO: no mover si esta saltando?
		//if(playerComponent!!.isSaltando)return
		val tmp = Vector3()
		val walkDirection = Vector3(0f, 0f, 0f)
		if(Gdx.app.type == Application.ApplicationType.Android) {
			if(ControllerWidget.movementVector.y > 0) walkDirection.add(camera.direction)
			if(ControllerWidget.movementVector.y < 0) walkDirection.sub(camera.direction)
			if(ControllerWidget.movementVector.x < 0) tmp.set(camera.direction).crs(camera.up).scl(-1f)
			if(ControllerWidget.movementVector.x > 0) tmp.set(camera.direction).crs(camera.up)
			walkDirection.add(tmp)
			walkDirection.scl(FUERZA_MOVIL * delta)
		}
		else {
			if(Gdx.input.isKeyPressed(Input.Keys.UP)) walkDirection.add(camera.direction)
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) walkDirection.sub(camera.direction)
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) tmp.set(camera.direction).crs(camera.up).scl(-1f)
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) tmp.set(camera.direction).crs(camera.up)
			walkDirection.add(tmp)
			walkDirection.scl(PlayerComponent.FUERZA_PC * delta)
		}
		walkDirection.y = 0f
		//walkDirection.nor()

		//bulletComponent.rigidBody.applyCentralForce(walkDirection)
		//bulletComponent.rigidBody.applyCentralImpulse(walkDirection)
		walkDirection.y = bulletComponent.rigidBody.linearVelocity.y
		bulletComponent.rigidBody.linearVelocity = walkDirection

	}
	//______________________________________________________________________________________________
	private fun updateSalto() {
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
		{
			System.err.println("------------------"+getPosition().y+"----- SALTANDO :"+playerComponent.isSaltando)
			if( ! playerComponent.isSaltando) {
				val fuerza = 1.025f
				bulletComponent.rigidBody.applyCentralImpulse(Vector3.Y.scl(fuerza))
				//updateCamara()
			}
		}
		playerComponent.isSaltando = getPosition().y > 3*ALTURA/4
	}
	//______________________________________________________________________________________________
	private fun updateCamara()
	{
		val pos = getPosition()
		pos.y += 2*ALTURA
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
	private fun getDirection() = camera.direction

	//______________________________________________________________________________________________
	private fun updateStatus() {
		gameUI.healthWidget.setValue(PlayerComponent.health)
	}

	//______________________________________________________________________________________________
	private var deltaFire = 100f
	private var deltaReload = 100f
	private fun updateDisparo(delta: Float)
	{
		//System.err.println("--------------------------------------------FIRE "+lastDelta)
		deltaFire += delta
		deltaReload += delta
		if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			if(deltaFire > 0.15f) {
				System.err.println("------ FIRE ! -------------------")
				deltaFire = 0f
				fire()
			}
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
			if(deltaReload > 5f) {
				System.err.println("------ RELOAD ! -------------------")
				deltaReload = 0f
				reload()
			}
		}
		gun.getComponent(AnimationComponent::class.java).update(delta)
	}
	//______________________________________________________________________________________________
	//TODO: crear pelotilla de fuego
	//TODO: cambiar de arma y poner animacion
	private fun fire() {
		//updateCamara()
		val dir = camera.direction.cpy()
		val pos = camera.position.cpy()
		if(bulletComponent.rigidBody.linearVelocity.x != 0f || bulletComponent.rigidBody.linearVelocity.z != 0f)
		pos.add(bulletComponent.rigidBody.linearVelocity.nor().scl(5f))
		val shot = EntityFactory.createShot(pos, dir)
		engine!!.addEntity(shot)

		//Animacion
		GunFactory.animate(gun, GunComponent.ACTION.SHOOT)

		/*val rayFrom = Vector3()
		val rayTo = Vector3()
		val ray = camera.getPickRay((Gdx.graphics.width / 2).toFloat(), (Gdx.graphics.height / 2).toFloat())
		rayFrom.set(ray.origin)
		rayTo.set(ray.direction).scl(50f).add(rayFrom)
		rayTestCB.collisionObject = null
		rayTestCB.closestHitFraction = 1f
		rayTestCB.setRayFromWorld(rayFrom)
		rayTestCB.setRayToWorld(rayTo)
		gameWorld.bulletSystem.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB)
		if(rayTestCB.hasHit()) {
			Gdx.app.error("CESGAME", "-------------------------- DISPARO DIO ------------------------------")

			val obj = rayTestCB.collisionObject
			if((obj.userData as Entity).getComponent(EnemyComponent::class.java) != null) {
				if((obj.userData as Entity).getComponent(StatusComponent::class.java).alive) {
					(obj.userData as Entity).getComponent(StatusComponent::class.java).alive = false
					PlayerComponent.score += 100
				}
			}
		}*/
	}

	//______________________________________________________________________________________________
	private fun reload() {
		//TODO: add ammo
		//Animacion
		GunFactory.animate(gun, GunComponent.ACTION.RELOAD, 1, 1)
	}

	//______________________________________________________________________________________________
	private fun checkGameOver() {
		if(PlayerComponent.health <= 0 && !Settings.paused) {
			Settings.paused = true
			gameUI.gameOverWidget.gameOver()
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
}