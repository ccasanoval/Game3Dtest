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
// DESKTOP RUN    : desktop:run
//
//
//NOTE:
//
// si te dice que no exite el apk: https://stackoverflow.com/questions/34039834/the-apk-file-does-not-exist-on-disk
// https://xoppa.github.io/blog/using-the-libgdx-3d-physics-bullet-wrapper-part1/

//TODO:
//
//TODO: FPS !!!  (Enemy consume mucho, es todo por pathfinding?)
//TODO: You win the game widget que muestre puntuacion etc...
//TODO: Pathfinding 3D (enhancement) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//TODO: Joystick!!
//TODO: Level Constructor
//TODO: Radar de monster?
//TODO: Textura en suelo donde nacen los bichos?
//TODO: Ascensores y escaleras!!!
//TODO: Monster3, Weapon2 ?
//
//TODO: Columnas en maze?
//TODO: mandos de pantalla : Ampliar y cambiar por mitad pantalla, mirar+disparo unidos? .... añadir salto?

//TODO: Dependecy Injection? https://github.com/denisk20/libgdx-dagger2
//TODO: Shadows? Fog?

//TODO: Multiplayer !!!!!
//https://developers.google.com/games/services/common/concepts/realtimeMultiplayer
//https://github.com/playgameservices/android-basic-samples
//TODO: Google Game Services
//Kill all the hideous monsters to get out of the labyrinth of death. CesDooM is just a proof of concept of a First Person Shooter game for Android, developed with Kotlin under Android Studio by Cesar Casanova
//Mátalos a todos esos monstruos horribles para salir del laberinto de la muerte. CesDooM es solo una prueba de concepto de un juego de acción en primera persona para Android, desarrollado en Kotlin con Android Studio por Cesar Casanova

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
		Log.e(tag, "CREATE--------------------------------------------------------------------------------")
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
		if(PlayerComponent.currentLevel > MazeFactory.MAX_LEVEL) {//TODO: show final congrats screen
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
		Log.e("CesDoom", "dispose------------------------------------------------")
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
			assets.iniWallMetal1()
			assets.iniWallMetal2()
			assets.iniWallMetal3()
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
			assets.iniJunk()
		}

		//TODO: Also the rest of the object initialization?
	}

}
