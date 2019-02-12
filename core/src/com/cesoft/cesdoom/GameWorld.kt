package com.cesoft.cesdoom

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.cesoft.cesdoom.components.GunComponent
import com.cesoft.cesdoom.entities.Gun
import com.cesoft.cesdoom.events.BulletEvent
import com.cesoft.cesdoom.events.EnemyEvent
import com.cesoft.cesdoom.events.GameEvent
import com.cesoft.cesdoom.events.RenderEvent
import com.cesoft.cesdoom.managers.*
import com.cesoft.cesdoom.systems.*


////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO:
//
//TODO: Joystick!!
//TODO: Mejorar FPS (Enemy consume mucho, es todo por pathfinding?)
//TODO: Levels+
//
//TODO: Constructor para laberinto & MapPathFinder
//TODO: Columnas en maze

//TODO: mandos de pantalla : Ampliar y cambiar por mitad pantalla, mirar+disparo unidos? .... añadir recarga (cargadores) y salto?

//TODO: Dependecy Injection? https://github.com/denisk20/libgdx-dagger2
//TODO: Shadows? Fog?

//VR
//TODO: VR Glasses !!!!!! https://github.com/LWJGL/lwjgl3/blob/master/modules/core/src/test/java/org/lwjgl/demo/openvr/HelloOpenVR.java
//https://github.com/yangweigbh/Libgdx-CardBoard-Extension
//https://github.com/Brummi/VRDemo
//https://github.com/nooone/gdx-vr


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameWorld(private val game: CesDoom) {

	private val debugCollision = false
	private var debugDrawer: DebugDrawer? = null

	private val gameEventSignal: Signal<GameEvent>
	private val enemyEventSignal: Signal<EnemyEvent>
	private val renderEventSignal: Signal<RenderEvent>
	private val bulletEventSignal: Signal<BulletEvent>

	private var bulletSystem: BulletSystem
	private var playerSystem: PlayerSystem
	private var enemySystem: EnemySystem
	//private var statusSystem: StatusSystem
	private var gateSystem: GateSystem
	private var ammoSystem: AmmoSystem
	private var healthSystem: HealthSystem
	var renderSystem: RenderSystem

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

		val lonMundo = 4000f

		///----
		gameEventSignal = Signal()
		enemyEventSignal = Signal()
		renderEventSignal = Signal()
		bulletEventSignal = Signal()

		///----
		bulletSystem = BulletSystem(bulletEventSignal, gameEventSignal)
		renderSystem = RenderSystem(game.assets, renderEventSignal, colorAmbiente)
		playerSystem = PlayerSystem(
				gameEventSignal, enemyEventSignal, renderEventSignal,
				colorAmbiente,
				renderSystem.perspectiveCamera,//TODO: quitar referencias? usar eventos?
				bulletSystem)//TODO: quitar referencias? usar eventos?
		enemySystem = EnemySystem(enemyEventSignal, gameEventSignal, bulletEventSignal, game)
		//statusSystem = StatusSystem(this)
		gateSystem = GateSystem()
		ammoSystem = AmmoSystem(gameEventSignal)
		healthSystem = HealthSystem(gameEventSignal)

		///----
		engine.addSystem(renderSystem)
		engine.addSystem(bulletSystem)
		engine.addSystem(playerSystem)
		engine.addSystem(enemySystem)
		//engine.addSystem(statusSystem)
		engine.addSystem(gateSystem)
		engine.addSystem(ammoSystem)
		engine.addSystem(healthSystem)

		///---
		if(debugCollision) {
			debugDrawer = DebugDrawer()
			debugDrawer!!.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE
			bulletSystem.collisionWorld.debugDrawer = debugDrawer
		}

		// TODO: Cargar desde constructor...
		/// SCENE
		engine.addEntity(SceneFactory.getDome(game.assets.getDome()))
		engine.addEntity(SceneFactory.getSuelo(game.assets.getSuelo(), lonMundo))
		SceneFactory.loadSkyline(game.assets.getSkyline(), engine, lonMundo/2f)
		SceneFactory.loadJunk(game.assets.getJunk(), engine, lonMundo/4f)

		/// MAZE
		MazeFactory.create(engine)

		/// PLAYER
		createPlayer(Vector3(0f,150f,0f))
	}

	//______________________________________________________________________________________________
	private fun createPlayer(pos: Vector3) {
		player = playerSystem.createPlayer(pos, engine)
		gun = GunFactory.create(
				game.assets.getRifle(),
				GunComponent.TYPE.CZ805,
				game.assets.getFireShot())
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
		//statusSystem.setProcessing( ! Status.paused)
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