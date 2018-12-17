package com.cesoft.cesdoom

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.cesoft.cesdoom.components.EnemyComponent
import com.cesoft.cesdoom.components.GunComponent
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.entities.Gun
import com.cesoft.cesdoom.managers.*
import com.cesoft.cesdoom.systems.*
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO: Sound... https://www.gamefromscratch.com/post/2013/11/19/LibGDX-Tutorial-8-Audio.aspx
//TODO: Steering AI !!!!!! https://www.gamedevelopment.blog/full-libgdx-game-tutorial-ashley-steering-behaviors/
//TODO: Velocidad carga y velocidad ejecucion (mobile 20 fps !!!)
//TODO: VR Glasses !!!!!! https://github.com/LWJGL/lwjgl3/blob/master/modules/core/src/test/java/org/lwjgl/demo/openvr/HelloOpenVR.java

//TODO: Configurar joystick Android -> Ampliar y cambiar por mitad pantalla, mirar+disparo unidos? .... añadir recarga y salto?
//TODO: splash mientras carga .... recursos con AssetManager, que ventaja tiene?
//TODO: about screen...

//TODO: Logs solo en debug
//TODO: Constructor para laberinto¿?
//TODO: Dependeci Injection?
//TODO: cuando te mueves, camara gun se mueve a los lados
//TODO: Shadows? Fog?
//TODO: Columnas en maze, maze cerrado !!

//https://github.com/mbrlabs/gdx-splash

//VR
//https://github.com/yangweigbh/Libgdx-CardBoard-Extension
//https://github.com/Brummi/VRDemo
//https://github.com/nooone/gdx-vr

class GameWorld(game: CesDoom) {

	private val debugCollision = false
	private var debugDrawer: DebugDrawer? = null

	private var bulletSystem: BulletSystem
	private var playerSystem: PlayerSystem

	var renderSystem: RenderSystem
	private var enemySystem: EnemySystem
	private var statusSystem: StatusSystem

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

		val assets = game.assets
		val lonMundo = 4000f

		///----
		bulletSystem = BulletSystem(this)
		renderSystem = RenderSystem(colorAmbiente, assets, bulletSystem.broadphase)
		enemySystem = EnemySystem(game)
		playerSystem = PlayerSystem(game, renderSystem.perspectiveCamera, bulletSystem)
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
		engine.addEntity(SceneFactory.getDome(assets.getDome()))
		engine.addEntity(SceneFactory.getSuelo(assets.getSuelo(), lonMundo))
		SceneFactory.loadSkyline(assets.getSkyline(), engine, lonMundo/2f)
		SceneFactory.loadJunk(assets.getJunk(), engine, lonMundo/3f)

		/// MAZE
		MazeFactory.create(assets, engine)

		/// ENEMIES
		//TODO: How to create the enemy engine without creating an enemy
		engine.addEntity(EnemyFactory.create(
				assets.getEnemy1(),
				EnemyComponent.TYPE.MONSTER1,
				Vector3(0f, 150f, -300f)))

		/// PLAYER
		createPlayer(assets, Vector3(0f,150f,0f))
		PlayerComponent.health = 1f
		PlayerComponent.score = 0
		PlayerComponent.colorAmbiente = colorAmbiente
	}

	//______________________________________________________________________________________________
	private fun createPlayer(assets: Assets, pos: Vector3) {
		player = PlayerComponent.create(pos)
		engine.addEntity(player)
		gun = GunFactory.create(
				assets.getCZ805(),
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
		//renderSystem.setProcessing( ! Settings.paused)
		bulletSystem.setProcessing( ! Settings.paused)
		enemySystem.setProcessing( ! Settings.paused)
		playerSystem.setProcessing( ! Settings.paused)
		statusSystem.setProcessing( ! Settings.paused)
		//shotSystem.setProcessing( ! Settings.paused)
	}

	fun pause() {
		enemySystem.pause()
	}
	fun resume() {
		enemySystem.resume()
	}

	//______________________________________________________________________________________________
	fun resize(width: Int, height: Int) {
		renderSystem.resize(width, height)
	}

	//______________________________________________________________________________________________
	fun dispose() {
		Log.e(tag, "dispose:--------------------------------------------")
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
	fun enemyDied(entity: Entity) {
		Log.e(tag, "enemyDied:-------------------------------------------------------------")
		remove(entity)
		PlayerComponent.score += 20
	}
}