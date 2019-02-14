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
import com.cesoft.cesdoom.managers.MazeFactory
import com.cesoft.cesdoom.screens.GameScreen
import com.cesoft.cesdoom.screens.LoadingScreen
import com.cesoft.cesdoom.screens.MainMenuScreen
import com.cesoft.cesdoom.systems.RenderSystem
import com.cesoft.cesdoom.util.Log

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
//TODO: Joystick!!
//TODO: Level Constructor
//TODO: Pathfinding 3D (enhancement)
//TODO: Monster2, Weapon2 ?
//TODO: Radar de monster?
//TODO: Textura en suelo donde nacen los bichos?
//
//TODO: Columnas en maze?
//TODO: mandos de pantalla : Ampliar y cambiar por mitad pantalla, mirar+disparo unidos? .... añadir salto?

//TODO: Dependecy Injection? https://github.com/denisk20/libgdx-dagger2
//TODO: Shadows? Fog?

//VR
//TODO: VR Glasses !!!!!! https://github.com/LWJGL/lwjgl3/blob/master/modules/core/src/test/java/org/lwjgl/demo/openvr/HelloOpenVR.java
//https://github.com/yangweigbh/Libgdx-CardBoard-Extension
//https://github.com/badlogic/gdx-vr/tree/master/test/com/badlogic/gdx/vr
//https://github.com/raphaelbruno/ZombieInvadersVR
//https://github.com/yangweigbh/Libgdx-CardBoard-Extension
//https://github.com/Brummi/VRDemo
//https://github.com/nooone/gdx-vr

class CesDoom(debugMode: Boolean) : ApplicationAdapter() {

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

	init {
	    Log.debugMode = debugMode
	}

	//______________________________________________________________________________________________
	override fun create() {
		Log.e(tag, "CREATE--------------------------------------------------------------------------------")
		isMobile = Gdx.app.type == Application.ApplicationType.Android
		assets = Assets()
		gameUI = GameUI(this, assets)

		Gdx.input.isCatchBackKey = true
		setScreen(MainMenuScreen(this, assets))
		Settings.loadPrefs()
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
	fun nextLevel() {
		PlayerComponent.currentLevel++
		if(PlayerComponent.currentLevel > MazeFactory.MAX_LEVEL)
			PlayerComponent.currentLevel = 0
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

		/*
		// Gate
		try {assets.getGate()}
		catch(ignore: GdxRuntimeException) {assets.iniGate()}
		// Switch
		try {assets.getSwitchOn()}
		catch(ignore: GdxRuntimeException) {assets.iniSwitchOn()}
		try {assets.getSwitchOff()}
		catch(ignore: GdxRuntimeException) {assets.iniSwitchOff()}
		// Wall
		try {assets.getWallMetal1()}
		catch(ignore: GdxRuntimeException) {assets.iniWallMetal1()}
		try {assets.getWallMetal2()}
		catch(ignore: GdxRuntimeException) {assets.iniWallMetal2()}
		try {assets.getWallMetal3()}
		catch(ignore: GdxRuntimeException) {assets.iniWallMetal3()}

		// Enemy
		try {assets.getEnemy(EnemyComponent.TYPE.MONSTER0)}
		catch(ignore: Exception) {assets.iniEnemy(EnemyComponent.TYPE.MONSTER0)}
		try {assets.getEnemy(EnemyComponent.TYPE.MONSTER1)}
		catch(ignore: Exception) {assets.iniEnemy(EnemyComponent.TYPE.MONSTER1)}

		// Weapons
		try {assets.getRifle()}
		catch(ignore: Exception) {assets.iniRifle()}
		try {assets.getFireShot()}
		catch(ignore: Exception) {assets.iniFireShot()}

		// Scene
		try {assets.getAmmo()}
		catch(ignore: GdxRuntimeException) {assets.iniAmmo()}
		try {assets.getHealth()}
		catch(ignore: GdxRuntimeException) {assets.iniHealth()}
		try {assets.getDome()}
		catch(ignore: GdxRuntimeException) {assets.iniDome()}
		try {assets.getSuelo()}
		catch(ignore: GdxRuntimeException) {assets.iniSuelo()}
		try {assets.getSkyline()}
		catch(ignore: GdxRuntimeException) {assets.iniSkyline()}
		try {assets.getJunk()}
		catch(ignore: GdxRuntimeException) {assets.iniJunk()}
		*/
		//TODO: Also the rest of the object initialization?
	}

	val render: RenderSystem
		get() = (screen as GameScreen).render

}
