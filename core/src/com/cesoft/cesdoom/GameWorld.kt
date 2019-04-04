package com.cesoft.cesdoom

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.GunComponent
import com.cesoft.cesdoom.entities.Gun
import com.cesoft.cesdoom.events.BulletEvent
import com.cesoft.cesdoom.events.EnemyEvent
import com.cesoft.cesdoom.events.GameEvent
import com.cesoft.cesdoom.events.RenderEvent
import com.cesoft.cesdoom.input.InputMapper
import com.cesoft.cesdoom.managers.*
import com.cesoft.cesdoom.systems.*
import com.cesoft.cesdoom.ui.GameOverWidget
import com.cesoft.cesdoom.ui.GameWinWidget


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameWorld(gameWinWidget: GameWinWidget,
				gameOverWidget: GameOverWidget,
				mapper: InputMapper,
				private val assets: Assets) {

	private val debugCollision = false
	private var debugDrawer: DebugDrawer? = null

	private val gameEventSignal: Signal<GameEvent>
	private val enemyEventSignal: Signal<EnemyEvent>
	private val renderEventSignal: Signal<RenderEvent>
	private val bulletEventSignal: Signal<BulletEvent>

	private var bulletSystem: BulletSystem
	private var playerSystem: PlayerSystem
	private var enemySystem: EnemySystem
	private var gateSystem: GateSystem
	private var ammoSystem: AmmoSystem
	private var healthSystem: HealthSystem
	private var renderSystem: RenderSystem

	private var engine: Engine = Engine()
	private lateinit var player: Entity
	private lateinit var gun: Gun

	private val colorAmbiente = ColorAttribute(ColorAttribute.AmbientLight, 0.9f, 0.7f, 0.7f, 1f)

	companion object {
	    val tag: String = GameWorld::class.java.simpleName
	}

	//______________________________________________________________________________________________
	init {
		Bullet.init()

		val longWorld = 4000f

		///----
		gameEventSignal = Signal()
		enemyEventSignal = Signal()
		renderEventSignal = Signal()
		bulletEventSignal = Signal()

		///----
		bulletSystem = BulletSystem(bulletEventSignal, gameEventSignal)
		renderSystem = RenderSystem(renderEventSignal, colorAmbiente, assets)
		playerSystem = PlayerSystem(
				gameEventSignal, enemyEventSignal, renderEventSignal,
				colorAmbiente,
				renderSystem.perspectiveCamera,//TODO: quitar referencias? usar eventos?
				bulletSystem,
				gameWinWidget,
				gameOverWidget,
				mapper
				)
		enemySystem = EnemySystem(enemyEventSignal, gameEventSignal, bulletEventSignal, renderEventSignal, assets)
		gateSystem = GateSystem()
		ammoSystem = AmmoSystem(gameEventSignal)
		healthSystem = HealthSystem(gameEventSignal)

		///----
		engine.addSystem(renderSystem)
		engine.addSystem(bulletSystem)
		engine.addSystem(playerSystem)
		engine.addSystem(enemySystem)
		engine.addSystem(gateSystem)
		engine.addSystem(ammoSystem)
		engine.addSystem(healthSystem)

		///---
		if(debugCollision) {
			debugDrawer = DebugDrawer()
			debugDrawer!!.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE
			bulletSystem.collisionWorld.debugDrawer = debugDrawer
		}

		/// SCENE
		SceneFactory.addDome(engine, assets.getDome())
		SceneFactory.addGround(engine, assets.getSuelo(), longWorld)
		SceneFactory.addSkyline(engine, assets.getSkyline(), longWorld/2f)
		SceneFactory.addJunkAntenna(engine, assets.getJunkAntenna(), longWorld/5f)
		SceneFactory.addJunkWall(engine, assets.getJunkWall(), longWorld/8f)
		SceneFactory.addJunkBuilding(engine, assets.getJunkBuilding(), longWorld/8f)

		/// MAZE
		MazeFactory.create(engine, assets)

		/// PLAYER
		createPlayer(Vector3(0f,200f,0f))
	}

	//______________________________________________________________________________________________
	private fun createPlayer(pos: Vector3) {
		player = playerSystem.createPlayer(pos, engine)
		gun = GunFactory.create(
				assets.getRifle(),
				GunComponent.TYPE.CZ805,
				assets.getFireShot())
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
		bulletSystem.setProcessing( ! Status.paused)
		enemySystem.setProcessing( ! Status.paused)
		playerSystem.setProcessing( ! Status.paused)
		gateSystem.setProcessing( ! Status.paused)
	}

	fun pause() {
		//TODO: evento a enemies etc para que paren temporizadores
		//enemySystem.pause()
	}
	fun resume() {
		//TODO: evento a enemies etc para que inicien temporizadores
		//enemySystem.resume()
	}

	//______________________________________________________________________________________________
	fun resize(width: Int, height: Int) {
		renderSystem.resize(width, height)
	}

	//______________________________________________________________________________________________
	fun dispose() {
		bulletSystem.dispose()
		renderSystem.dispose()
	}

}