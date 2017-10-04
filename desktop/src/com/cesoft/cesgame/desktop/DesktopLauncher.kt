package com.cesoft.cesgame.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.cesoft.cesgame.CesGame

object DesktopLauncher {
	@JvmStatic
	fun main(arg: Array<String>) {
		val config = LwjglApplicationConfiguration()
		config.width = CesGame.VIRTUAL_WIDTH.toInt()
		config.height = CesGame.VIRTUAL_HEIGHT.toInt()
		LwjglApplication(CesGame(), config)
	}
}
