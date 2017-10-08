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

	fun setScreen(screen: Screen) {
		if(this.screen != null) {
			this.screen!!.hide()
			this.screen!!.dispose()
		}
		this.screen = screen
		if(this.screen != null) {
			this.screen!!.show()
			this.screen!!.resize(Gdx.graphics.width, Gdx.graphics.height)
		}
	}

	override fun dispose() {
		Settings.save()
	}

	companion object {
		val VIRTUAL_WIDTH = 1024f
		val VIRTUAL_HEIGHT = 720f
		val SALIR = "Salir"
		val JUGAR = "Jugar"
		val PUNTOS = "Puntuaciones"
	}
}
