package com.cesoft.cesgame

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
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
//TODO: Steering AI !!!!!!
//TODO: Velocidad carga y velocidad ejecucion (mobile 20 fps !!!)
//TODO: VR Glasses !!!!!!

//TODO: Configurar joystick Android -> Ampliar y cambiar por mitad pantalla, mirar+disparo unidos? .... añadir recarga y salto?
//TODO: splash mientras carga .... recursos con AssetManager, que ventaja tiene?
//TODO: about screen...

//TODO: Logs solo en debug
//TODO: Letrero LOADING... cuando le das a play
//TODO: Constructor para laberinto¿?
//TODO: Dependeci Injection?
//TODO: cuando te mueves, camara gun se mueve a los lados
//TODO: Shadows? Fog?

class GameWorld(gameUI: GameUI, assets: Assets) {

	private val debugCollision = false
	private var debugDrawer: DebugDrawer? = null

	private var bulletSystem: BulletSystem
	private var playerSystem: PlayerSystem

	private var renderSystem: RenderSystem
	private var enemySystem: EnemySystem
	private var statusSystem: StatusSystem

	private var engine: Engine = Engine()
	private lateinit var player: Entity
	private lateinit var gun: Entity

	private val colorAmbiente = ColorAttribute(ColorAttribute.AmbientLight, 0.7f, 0.4f, 0.4f, 1f)


	//______________________________________________________________________________________________
	init {
		Bullet.init()

		val lonMundo = 4000f

		///----
		renderSystem = RenderSystem(colorAmbiente, assets)
		bulletSystem = BulletSystem(this)
		enemySystem = EnemySystem(assets)
		playerSystem = PlayerSystem(gameUI, renderSystem.perspectiveCamera, bulletSystem)
		statusSystem = StatusSystem(this)

		///----
		engine.addSystem(renderSystem)
		engine.addSystem(bulletSystem)
		engine.addSystem(playerSystem)
		engine.addSystem(enemySystem)
		engine.addSystem(statusSystem)

		///---
		if(debugCollision) {
			debugDrawer = DebugDrawer()
			debugDrawer!!.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE
			bulletSystem.collisionWorld.debugDrawer = debugDrawer
		}

		// TODO: Cargar desde constructor...
		/// SCENE
		engine.addEntity(SceneFactory.loadDome())
		engine.addEntity(SceneFactory.loadSuelo(lonMundo))
		SceneFactory.loadSkyline(engine, lonMundo/2f)
		SceneFactory.loadJunk(engine, lonMundo/3f)

		/// MAZE
		MazeFactory.create(engine)

		/// ENEMIES
		engine.addEntity(EnemyFactory.create(EnemyComponent.TYPE.MONSTER1, Vector3(0f, 150f, -300f)))

		/// PLAYER
		createPlayer(Vector3(0f,150f,0f))
		PlayerComponent.health = 100f
		PlayerComponent.score = 0
		PlayerComponent.colorAmbiente = colorAmbiente
		System.err.println("---------------GameWorld:init:8-----------------------")
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