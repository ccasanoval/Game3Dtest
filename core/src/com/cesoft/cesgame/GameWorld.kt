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
import com.cesoft.cesgame.managers.*
import com.cesoft.cesgame.systems.*


////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: al reiniciar en MOBILE peta !!!!!!!!!!!!!!!!!!!
// TODO: Steering AI !!!!!!
// TODO: Constructor
// TODO: Velocidad carga
class GameWorld(gameUI: GameUI) {

	private val debugCollision = false
	private var debugDrawer: DebugDrawer? = null

	private var bulletSystem: BulletSystem
	private var playerSystem: PlayerSystem

	private var renderSystem: RenderSystem
	private var enemySystem: EnemySystem
	private var statusSystem: StatusSystem
	//private var shotSystem: ShotSystem

	private var engine: Engine = Engine()
	private lateinit var player: Entity
	private lateinit var gun: Entity

	init {
		Bullet.init()

		renderSystem = RenderSystem()
		bulletSystem = BulletSystem(this)
		enemySystem = EnemySystem()
		playerSystem = PlayerSystem(gameUI, renderSystem.perspectiveCamera, bulletSystem)
		statusSystem = StatusSystem(this)
		//shotSystem = ShotSystem(this)

		engine.addSystem(renderSystem)
		engine.addSystem(bulletSystem)
		engine.addSystem(playerSystem)
		engine.addSystem(enemySystem)
		engine.addSystem(statusSystem)
		//engine.addSystem(shotSystem)
		if(debugCollision) {
			debugDrawer = DebugDrawer()
			debugDrawer!!.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE
			bulletSystem.collisionWorld.debugDrawer = debugDrawer
		}

		// TODO: Cargar desde constructor...
		/// SCENE
		engine.addEntity(EntityFactory.loadSuelo(Vector3(0f, 0f, 0f), 10000f))
		engine.addEntity(EntityFactory.loadDome(Vector3(0f, 0f, 0f)))


		/// MAZE
		engine.addEntity(WallFactory.create(Vector3(-WallFactory.HIGH-5.5f, 0f, 4*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(+WallFactory.HIGH+5.5f, 0f, 4*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(-WallFactory.HIGH-5.5f, 0f, 2*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(+WallFactory.HIGH+5.5f, 0f, 2*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(-2.1f*WallFactory.HIGH-10, 0f, 10f), +40f))
		engine.addEntity(WallFactory.create(Vector3(+2.1f*WallFactory.HIGH+10, 0f, 10f), -40f))

		engine.addEntity(WallFactory.create(Vector3(-WallFactory.HIGH-5.5f, 0f, -6f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(+WallFactory.HIGH+5.5f, 0f, -6f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(-WallFactory.HIGH-5.5f, 0f, -4f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(+WallFactory.HIGH+5.5f, 0f, -4f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(-2*WallFactory.HIGH-10, 0f, -2f*WallFactory.LONG-10), -40f))
		engine.addEntity(WallFactory.create(Vector3(+2*WallFactory.HIGH+10, 0f, -2f*WallFactory.LONG-10), +40f))

		/// RAMPAS
		engine.addEntity(RampFactory.create(Vector3(-RampFactory.LONG+2f, 2f*WallFactory.HIGH, 3*RampFactory.LONG), angleZ=90f))
		engine.addEntity(RampFactory.create(Vector3(+RampFactory.LONG-2f, 2f*WallFactory.HIGH, 3*RampFactory.LONG), angleZ=90f))
		engine.addEntity(RampFactory.create(Vector3(-RampFactory.LONG+2f, 2f*WallFactory.HIGH, 4*RampFactory.LONG), angleZ=90f))
		engine.addEntity(RampFactory.create(Vector3(+RampFactory.LONG-2f, 2f*WallFactory.HIGH, 4*RampFactory.LONG), angleZ=90f))
		engine.addEntity(RampFactory.create(Vector3(-RampFactory.LONG+2f, 2f*WallFactory.HIGH, 5*RampFactory.LONG+14f), angleZ=90f))
		engine.addEntity(RampFactory.create(Vector3(+RampFactory.LONG-2f, 2f*WallFactory.HIGH, 5*RampFactory.LONG+14f), angleZ=90f))

		engine.addEntity(RampFactory.create(Vector3(-3.5f*RampFactory.LONG, .5f*WallFactory.HIGH, 4*RampFactory.LONG+7f), angleZ=-40f))
		engine.addEntity(RampFactory.create(Vector3(-2.4f*RampFactory.LONG, 1.5f*WallFactory.HIGH, 4*RampFactory.LONG+7f), angleZ=-40f))
		engine.addEntity(RampFactory.create(Vector3(+3.5f*RampFactory.LONG, .5f*WallFactory.HIGH, 4*RampFactory.LONG+7f), angleZ=+40f))
		engine.addEntity(RampFactory.create(Vector3(+2.4f*RampFactory.LONG, 1.5f*WallFactory.HIGH, 4*RampFactory.LONG+7f), angleZ=+40f))


		/// ENEMIES
		engine.addEntity(EnemyFactory.create(EnemyComponent.TYPE.MONSTER1, Vector3(0f, 150f, -300f)))


		/// PLAYER
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
		//shotSystem.setProcessing( ! Settings.paused)
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

	//______________________________________________________________________________________________
	fun enemyDied(entity: Entity)
	{
		remove(entity)
		PlayerComponent.score += 20
	}
}