package com.cesoft.cesgame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.cesoft.cesgame.screens.MainMenuScreen

////////////////////////////////////////////////////////////////////////////////////////////////////
// DESKTOP DEPLOY : gradlew desktop:dist --> cesgame2\desktop\build\libs
// DESKTOP RUN    : desktop:run
//
//https://xoppa.github.io/blog/using-the-libgdx-3d-physics-bullet-wrapper-part1/
//TODO: Comprobar disparo Android
//TODO: Configurar joystick Android -> Ampliar y cambiar por recuadro con flechas, añadir recarga y salto?
//TODO: Reducir tiempo de carga de la escena
//TODO: splash mientras carga recursos con AssetManager, que ventaja tiene?
//TODO: IA
//TODO i18n
class CesGame : ApplicationAdapter() {

	private lateinit var assets: Assets
	private var screen: Screen? = null

	//______________________________________________________________________________________________
	override fun create() {
		assets = Assets()
		Settings.load()
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
	fun delScreen()
	{
		if(this.screen != null) {
			this.screen!!.hide()
			this.screen!!.dispose()
		}
	}
	//______________________________________________________________________________________________
	fun setScreen(screen: Screen) {
		this.screen = screen
		if(this.screen != null) {
			this.screen!!.show()
			this.screen!!.resize(Gdx.graphics.width, Gdx.graphics.height)
		}
	}

	//______________________________________________________________________________________________
	override fun dispose() {
		System.err.println("CesGame:dispose:----------------------------------------")
		Settings.save()
		assets.dispose()
	}

	//______________________________________________________________________________________________
	companion object {
		val VIRTUAL_WIDTH = 1024f
		val VIRTUAL_HEIGHT = 576f
	}
}
