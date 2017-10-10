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
//TODO: Configurar joystick Android
//TODO: Enemigos?
//TODO: Evitar que chocar haga volar (solo pasa en desktop?)
//TODO: Reducir tiempo de carga de la escena
class CesGame : ApplicationAdapter() {

	private var screen: Screen? = null

	override fun create() {
		Assets()
		Settings.load()
		Gdx.input.isCatchBackKey = true
		setScreen(MainMenuScreen(this))
	}

	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
		screen!!.render(Gdx.graphics.deltaTime)
	}

	override fun resize(width: Int, height: Int) {
		screen!!.resize(width, height)
	}

	fun delScreen()//TODO: Estudiar que estoy eliminando que luego no se crea¿? ->GameScreen.dispose
	{
		if(this.screen != null) {
			this.screen!!.hide()
			this.screen!!.dispose()
		}
	}
	fun setScreen(screen: Screen) {
		System.err.println("CesGame:setScreen:----------------------------------------")

		/*if(this.screen != null) {
			this.screen!!.hide()
			this.screen!!.dispose()
		}*/
		this.screen = screen
		if(this.screen != null) {
			this.screen!!.show()
			this.screen!!.resize(Gdx.graphics.width, Gdx.graphics.height)
		}
	}

	override fun dispose() {
		System.err.println("CesGame:dispose:----------------------------------------")
		Settings.save()
	}

	companion object {
		val VIRTUAL_WIDTH = 1024f
		val VIRTUAL_HEIGHT = 576f
		val SALIR = "Salir"
		val JUGAR = "Jugar"
		val PUNTOS = "Puntuaciones"
	}
}
