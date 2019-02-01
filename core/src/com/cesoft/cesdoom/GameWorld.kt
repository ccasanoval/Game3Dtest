package com.cesoft.cesdoom

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.GunComponent
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.entities.Gun
import com.cesoft.cesdoom.entities.Player
import com.cesoft.cesdoom.managers.*
import com.cesoft.cesdoom.systems.*


////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO:
//
//TODO: Mejorar FPS (Enemy consume mucho, es todo por pathfinding?)
//TODO: Modo dios como huevo
//TODO: HEALTH objects+
//TODO: AMMO limit and objects+
//TODO: Levels+
//
//TODO: Constructor para laberinto & MapPathFinder
//TODO: Columnas en maze

//TODO: Configurar joystick Android -> Ampliar y cambiar por mitad pantalla, mirar+disparo unidos? .... añadir recarga y salto?

//TODO: Dependecy Injection? https://github.com/denisk20/libgdx-dagger2
//TODO: Shadows? Fog?

//VR
//TODO: VR Glasses !!!!!! https://github.com/LWJGL/lwjgl3/blob/master/modules/core/src/test/java/org/lwjgl/demo/openvr/HelloOpenVR.java
//https://github.com/yangweigbh/Libgdx-CardBoard-Extension
//https://github.com/Brummi/VRDemo
//https://github.com/nooone/gdx-vr


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameWorld(val game: CesDoom) {

	private val debugCollision = false
	private var debugDrawer: DebugDrawer? = null

	private var bulletSystem: BulletSystem
	private var playerSystem: PlayerSystem

	var renderSystem: RenderSystem
	private var enemySystem: EnemySystem
	private var statusSystem: StatusSystem
	private var gateSystem: GateSystem

	private var engine: Engine = Engine()
	private lateinit var player: Entity
	private lateinit var gun: Gun

	private val colorAmbiente = ColorAttribute(ColorAttribute.AmbientLight, 0.7f, 0.2f, 0.2f, 1f)

	companion object {
	    val tag: String = GameWorld::class.java.simpleName
	}

	//______________________________________________________________________________________________
	init {
		Bullet.init()

		val lonMundo = 4000f

		///----
		bulletSystem = BulletSystem()
		renderSystem = RenderSystem(colorAmbiente)//, Assets, bulletSystem.broadphase)
		playerSystem = PlayerSystem(game, renderSystem.perspectiveCamera, bulletSystem)
		enemySystem = EnemySystem(game)
		statusSystem = StatusSystem(this)
		gateSystem = GateSystem()

		///----
		engine.addSystem(renderSystem)
		engine.addSystem(bulletSystem)
		engine.addSystem(playerSystem)
		engine.addSystem(enemySystem)
		engine.addSystem(statusSystem)
		engine.addSystem(gateSystem)

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
		player = Player.create(pos, colorAmbiente, engine)
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
		statusSystem.setProcessing( ! Status.paused)
		gateSystem.setProcessing( ! Status.paused)
	}

	fun pause() {
		//enemySystem.pause()
	}
	fun resume() {
		//enemySystem.resume()
	}

	//______________________________________________________________________________________________
	fun resize(width: Int, height: Int) {
		renderSystem.resize(width, height)
	}

	//______________________________________________________________________________________________
	fun dispose() {
		bulletSystem.dispose()
		playerSystem.dispose()
		renderSystem.dispose()
	}

	//______________________________________________________________________________________________
	fun removeEnemyCollider(enemy: Enemy) {
		bulletSystem.removeBody(enemy)
	}
	//______________________________________________________________________________________________
	fun enemyDied(enemy: Enemy) {
		engine.removeEntity(enemy)
		PlayerComponent.addScore(20)
	}
}