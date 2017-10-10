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
			deltaX = -ControllerWidget.watchVector.x * 85f * delta
			deltaY = ControllerWidget.watchVector.y * 45f * delta
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
		if(Gdx.app.type == Application.ApplicationType.Android)
		{
			if(pitch - pr > 100)
				pr = -(100 - pitch)
			else if(pitch - pr < 70)
				pr = pitch - 70
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
			if(     ControllerWidget.movementVector.y > +0.1) walkDirection.add(camera.direction)
			else if(ControllerWidget.movementVector.y < -0.1) walkDirection.sub(camera.direction)
			if(     ControllerWidget.movementVector.x < -0.1) tmp.set(camera.direction).crs(camera.up).scl(-1f)
			else if(ControllerWidget.movementVector.x > +0.1) tmp.set(camera.direction).crs(camera.up)
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
				val fuerza = 1.028f
				bulletComponent.rigidBody.applyCentralImpulse(Vector3.Y.scl(fuerza))
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
	//TODO: crear pelotilla de fuego? mostrar fuego en cañon?
	private fun fire() {
		val dir = camera.direction.cpy()
		val pos = camera.position.cpy()
		val vel = bulletComponent.rigidBody.linearVelocity.cpy()
		vel.y = 0f
		pos.add(vel.nor().scl(5f))

		val y = bulletComponent.rigidBody.linearVelocity.y *0.035f
		if(y != 0f) pos.add(Vector3(0f, y, 0f))

		val shot = ShotComponent.createShot(pos, dir)
		engine!!.addEntity(shot)

		//Animacion
		GunFactory.animate(gun, GunComponent.ACTION.SHOOT)
	}

	//______________________________________________________________________________________________
	private fun reload() {
		//TODO: add ammo, que se gaste, mas contador
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

	//______________________________________________________________________________________________
	fun dispose()
	{
		GunFactory.dispose()
	}
}