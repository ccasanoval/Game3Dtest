package com.cesoft.cesgame

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.cesoft.cesgame.UI.GameUI
import com.cesoft.cesgame.components.EnemyComponent
import com.cesoft.cesgame.components.GunComponent
import com.cesoft.cesgame.components.PlayerComponent
import com.cesoft.cesgame.managers.EnemyFactory
import com.cesoft.cesgame.managers.EntityFactory
import com.cesoft.cesgame.managers.GunFactory
import com.cesoft.cesgame.managers.WarehouseFactory
import com.cesoft.cesgame.systems.*


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameWorld(gameUI: GameUI) {

	private val debugCollision = true
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
		System.err.println("GameWorld: INIT: -------------------------")
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
		if(debugCollision) {
			debugDrawer = DebugDrawer()
			debugDrawer!!.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE
			bulletSystem.collisionWorld.debugDrawer = debugDrawer
		}

		engine.addEntity(EntityFactory.loadSuelo(Vector3(0f, 0f, 0f)))
		engine.addEntity(EntityFactory.loadDome(Vector3(0f, 0f, 0f)))
//		engine.addEntity(WarehouseFactory.create(Vector3(  0f, 0f, -250f), 0f))
//		engine.addEntity(WarehouseFactory.create(Vector3(+250f, 0f, -150f), -45f))
//		engine.addEntity(WarehouseFactory.create(Vector3(-250f, 0f, -150f), +45f))

		engine.addEntity(EnemyFactory.create(EnemyComponent.TYPE.MONSTER1, Vector3(50f, 50f, -150f)))
		engine.addEntity(EnemyFactory.create(EnemyComponent.TYPE.ZOMBIE1, Vector3(-50f, 50f, -150f)))

		//engine.addEntity(EntityFactory.load1(Vector3(0f, 100f, -200f)))
		//engine.addEntity(EntityFactory.load2(Vector3(0f, 0f, 0f)))


		createPlayer(Vector3(0f,150f,0f))
		PlayerComponent.health = 100f
		PlayerComponent.score = 0
	}


	//______________________________________________________________________________________________
	private fun createPlayer(pos: Vector3) {
		player = PlayerComponent.create(pos)
		engine.addEntity(player)
		gun = GunFactory.create(GunComponent.TYPE.CZ805)//Dentro de playerSystem??
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
		if(debugCollision) {
			debugDrawer!!.begin(renderSystem.perspectiveCamera)
			bulletSystem.collisionWorld.debugDrawWorld()
			debugDrawer!!.end()
		}
	}
	private fun checkPause() {
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
		bulletSystem.dispose()
		renderSystem.dispose()
		enemySystem.dispose()
		playerSystem.dispose()
	}

	//______________________________________________________________________________________________
	fun remove(entity: Entity) {
		engine.removeEntity(entity)
		bulletSystem.removeBody(entity)
	}

}