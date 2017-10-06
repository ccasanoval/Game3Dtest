package com.cesoft.cesgame

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.cesoft.cesgame.UI.GameUI
import com.cesoft.cesgame.components.PlayerComponent
import com.cesoft.cesgame.managers.EntityFactory
import com.cesoft.cesgame.systems.*


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameWorld(gameUI: GameUI) {

	private val debug = true
	private var debugDrawer: DebugDrawer? = null

	private var bulletSystem: BulletSystem
	private var playerSystem: PlayerSystem
	private var renderSystem: RenderSystem
	private var enemySystem: EnemySystem
	private var statusSystem: StatusSystem
	private var shotSystem: ShotSystem

	private var engine: Engine = Engine()
	private lateinit var player: Entity
	private lateinit var gun: Entity

	init {
		Bullet.init()

		renderSystem = RenderSystem()
		bulletSystem = BulletSystem(this)
		enemySystem = EnemySystem()
		playerSystem = PlayerSystem(gameUI, renderSystem.perspectiveCamera)
		statusSystem = StatusSystem(this)
		shotSystem = ShotSystem(this)

		engine.addSystem(renderSystem)
		engine.addSystem(bulletSystem)
		engine.addSystem(playerSystem)
		engine.addSystem(enemySystem)
		engine.addSystem(statusSystem)
		engine.addSystem(shotSystem)
		if(debug) {
			debugDrawer = DebugDrawer()
			debugDrawer!!.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE
			bulletSystem.collisionWorld.debugDrawer = debugDrawer
		}

		loadLevel()
		createPlayer(0f, 20f, 0f)
		PlayerComponent.health = 100f
		PlayerComponent.score = 0
	}

	//______________________________________________________________________________________________
	private fun loadLevel() {
		engine.addEntity(EntityFactory.loadScene(0f, 0f, 0f))
		engine.addEntity(EntityFactory.loadDome(0f, 0f, 0f))
	}

	//______________________________________________________________________________________________
	private fun createPlayer(x: Float, y: Float, z: Float) {
		player = EntityFactory.createPlayer(x, y, z)
		engine.addEntity(player)
		//gun = EntityFactory.loadGun(x+6, y-9, z-5)
		gun = EntityFactory.loadGun(3.9f, -2.2f, -5f)
		engine.addEntity(gun)
		playerSystem.gun = gun
		renderSystem.gun = gun
	}

	//______________________________________________________________________________________________
	fun render(delta: Float) {
		renderWorld(delta)
		checkPause()
	}
	private fun renderWorld(delta: Float) {
		engine.update(delta)
		if(debug) {
			debugDrawer!!.begin(renderSystem.perspectiveCamera)
			bulletSystem.collisionWorld.debugDrawWorld()
			debugDrawer!!.end()
		}
	}
	private fun checkPause() {
		/*engine.getSystem(PlayerSystem::class.java).setProcessing( ! Settings.paused)
		engine.getSystem(EnemySystem::class.java).setProcessing( ! Settings.paused)
		engine.getSystem(StatusSystem::class.java).setProcessing( ! Settings.paused)
		engine.getSystem(BulletSystem::class.java).setProcessing( ! Settings.paused)
		engine.getSystem(ShotSystem::class.java).setProcessing( ! Settings.paused)
		engine.getSystem(ShotSystem::class.java).setProcessing( ! Settings.paused)*/
		//renderSystem.setProcessing( ! Settings.paused)
		bulletSystem.setProcessing( ! Settings.paused)
		enemySystem.setProcessing( ! Settings.paused)
		playerSystem.setProcessing( ! Settings.paused)
		statusSystem.setProcessing( ! Settings.paused)
		shotSystem.setProcessing( ! Settings.paused)
	}

	//______________________________________________________________________________________________
	fun resize(width: Int, height: Int) {
		renderSystem.resize(width, height)
	}

	//______________________________________________________________________________________________
	fun dispose() {
		enemySystem.dispose()
		bulletSystem.dispose()
		renderSystem.dispose()
	}

	//______________________________________________________________________________________________
	fun remove(entity: Entity) {
		engine.removeEntity(entity)
		bulletSystem.removeBody(entity)
	}

}