package com.cesoft.cesgame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Assets {
	var skin: Skin = Skin()
		private set

	//______________________________________________________________________________________________
	init {
		if(atlasFile.exists())
			skin.addRegions(TextureAtlas(atlasFile))
		skin.load(fileHandle)
	}

	//______________________________________________________________________________________________
	fun dispose() {
		skin.dispose()
	}

	//______________________________________________________________________________________________
	companion object {
		//val fileHandle = Gdx.files.internal("data/uiskin.json")!!
		//val atlasFile = fileHandle.sibling("uiskin.atlas")!!
		val fileHandle = Gdx.files.internal("skin/star-soldier-ui.json")!!
		val atlasFile = fileHandle.sibling("star-soldier-ui.atlas")!!
	}
}
