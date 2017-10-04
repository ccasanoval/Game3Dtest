package com.cesoft.cesgame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Assets {
	var skin: Skin = Skin()
		private set

	init {
		if(atlasFile.exists())
			skin.addRegions(TextureAtlas(atlasFile))
		skin.load(fileHandle)
	}

	fun dispose() {
		skin.dispose()
	}

	companion object {
		val fileHandle = Gdx.files.internal("data/uiskin.json")!!
		val atlasFile = fileHandle.sibling("uiskin.atlas")!!
	}
}
