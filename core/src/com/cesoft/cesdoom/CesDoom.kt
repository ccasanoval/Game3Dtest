package com.cesoft.cesdoom

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.GdxRuntimeException
import com.cesoft.cesdoom.ui.GameUI
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.EnemyComponent
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.input.PlayerInput
import com.cesoft.cesdoom.managers.MazeFactory
import com.cesoft.cesdoom.screens.GameScreen
import com.cesoft.cesdoom.screens.LoadingScreen
import com.cesoft.cesdoom.screens.MainMenuScreen
import com.cesoft.cesdoom.util.Log
import com.cesoft.cesdoom.util.PlayServices


////////////////////////////////////////////////////////////////////////////////////////////////////
// DESKTOP DEPLOY : gradlew desktop:dist --> cesgame2\desktop\build\libs
// DESKTOP RUN    : gradlew desktop:run
//
//
//NOTE:
//
// si te dice que no existe el apk: https://stackoverflow.com/questions/34039834/the-apk-file-does-not-exist-on-disk
// https://xoppa.github.io/blog/using-the-libgdx-3d-physics-bullet-wrapper-part1/

//TODO:
//
//TODO: FPS !!!  (Enemy consume mucho, es todo por pathfinding?)
//TODO: Pathfinding 3D (enhancement) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//
//TODO: Ascensores y escaleras!!!
//TODO: Level Constructor + Pathfinding compilator
//TODO: Radar de monster?
//TODO: Textura en suelo donde nacen los bichos?
//TODO: Monster3, Weapon2 ?
//TODO: Saltar? Sonido al caer desde cierta altura y bounce de camara!
//
//TODO: mandos de pantalla : Ampliar y cambiar por mitad pantalla, mirar+disparo unidos? .... añadir salto?

//TODO: Dependecy Injection? https://github.com/denisk20/libgdx-dagger2
//TODO: Shadows? Fog?

//TODO: Multiplayer !!!!!
//https://developers.google.com/games/services/common/concepts/realtimeMultiplayer
//https://github.com/playgameservices/android-basic-samples

//VR
//TODO: VR Glasses !!!!!! https://github.com/LWJGL/lwjgl3/blob/master/modules/core/src/test/java/org/lwjgl/demo/openvr/HelloOpenVR.java
//https://github.com/yangweigbh/Libgdx-CardBoard-Extension
//https://github.com/badlogic/gdx-vr/tree/master/test/com/badlogic/gdx/vr
//https://github.com/raphaelbruno/ZombieInvadersVR
//https://github.com/yangweigbh/Libgdx-CardBoard-Extension
//https://github.com/Brummi/VRDemo
//https://github.com/nooone/gdx-vr

class CesDoom(
		debugMode: Boolean,
		val playServices: PlayServices?=null)
	: ApplicationAdapter() {

	companion object {
		private val tag: String = CesDoom::class.java.simpleName
		const val VIRTUAL_WIDTH = 1024f
		const val VIRTUAL_HEIGHT = 576f
		var isMobile = false
			private set
	}
	private var screen: Screen? = null
	private lateinit var gameUI: GameUI
	private lateinit var assets: Assets
	lateinit var playerInput: PlayerInput
	init {
	    Log.debugMode = debugMode
	}

	//______________________________________________________________________________________________
	override fun create() {
		Settings.loadPrefs()
		playerInput = PlayerInput(Settings.getInputMapper())
		isMobile = Gdx.app.type == Application.ApplicationType.Android
		assets = Assets()
		gameUI = GameUI(this, assets)
		Gdx.input.isCatchBackKey = true
		setScreen(MainMenuScreen(this, assets))
	}

	//______________________________________________________________________________________________
	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
		screen!!.render(Gdx.graphics.deltaTime)
	}

	//______________________________________________________________________________________________
	override fun resize(width: Int, height: Int) {
		screen!!.resize(width, height)
	}

	//______________________________________________________________________________________________
	private fun delScreen() {
		screen?.let {
			it.hide()
			it.dispose()
		}
	}
	//______________________________________________________________________________________________
	fun setScreen(screen: Screen) {
		this.screen = screen
		screen.show()
		screen.resize(Gdx.graphics.width, Gdx.graphics.height)
	}

	fun pauseGame() {
		(screen as GameScreen).pause()
	}
	//______________________________________________________________________________________________
	fun reset(forceLevelZero: Boolean = true) {
		if(forceLevelZero)
			PlayerComponent.currentLevel = 0
		delScreen()
		setScreen(LoadingScreen(this, gameUI, assets))
		Status.gameOver = false
		Status.mainMenu = false
	}
	fun nextLevel() : Int {
		val oldLevel = PlayerComponent.currentLevel
		PlayerComponent.currentLevel++
		if(PlayerComponent.currentLevel > MazeFactory.MAX_LEVEL) {
			PlayerComponent.currentLevel = 0
		}
		return oldLevel
	}
	fun isNextOrReload():Boolean {
		return PlayerComponent.currentLevel > 0
	}
	//______________________________________________________________________________________________
	fun reset2Menu() {
		Status.mainMenu = true
		Status.gameOver = false
		delScreen()
		setScreen(MainMenuScreen(this, assets))
	}

	//______________________________________________________________________________________________
	override fun dispose() {
		delScreen()
		Sounds.dispose()
		gameUI.dispose()
		assets.dispose()
	}

	//______________________________________________________________________________________________
	fun loadResources() {

		// Sounds
		Sounds.load()

		try {assets.getGate()}
		catch(ignore: GdxRuntimeException) {
			// Gate
			assets.iniGate()
			// Switch
			assets.iniSwitchOn()
			assets.iniSwitchOff()
			// Wall
			assets.iniWallConcrete()
			assets.iniWallSteel()
			assets.iniWallGrille()
			assets.iniWallCircuits()
			// Enemy
			assets.iniEnemy(EnemyComponent.TYPE.MONSTER0)
			assets.iniEnemy(EnemyComponent.TYPE.MONSTER1)

			// Weapons
			assets.iniRifle()
			assets.iniFireShot()

			// Scene
			assets.iniAmmo()
			assets.iniHealth()
			assets.iniDome()
			assets.iniSuelo()
			assets.iniSkyline()
			assets.iniJunkAntenna()
			assets.iniJunk2()
			assets.iniBike()
		}

		//TODO: Also the rest of the object initialization?
	}

}
