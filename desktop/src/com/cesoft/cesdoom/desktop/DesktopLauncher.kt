package com.cesoft.cesdoom.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.cesoft.cesdoom.CesDoom

// To see it running: ./gradlew desktop:run
object DesktopLauncher {
	@JvmStatic
	fun main(arg: Array<String>) {
		val config = LwjglApplicationConfiguration()
		config.width = CesDoom.VIRTUAL_WIDTH.toInt()
		config.height = CesDoom.VIRTUAL_HEIGHT.toInt()
		config.title = "CesDooM"
		//config.addIcon("data/ic_launcher.png", Files.FileType.Internal);
		LwjglApplication(CesDoom(debugMode = true), config)
	}
}
