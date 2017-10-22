package com.cesoft.cesgame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle



////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: AssetManager ????????
class Assets {
	var skin: Skin = Skin()
		private set
	private var i18nBundle = I18NBundle.createBundle(i18n)!!
		fun getString(clave: String) = i18nBundle.get(clave)
		//fun formatString(clave: String, vararg params: Any) = i18nBundle.format(clave, params)
		fun formatString(clave: String, param: Int) = i18nBundle.format(clave, param)

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
		private val fileHandle = Gdx.files.internal("skin/star-soldier-ui.json")!!
		private val atlasFile = fileHandle.sibling("star-soldier-ui.atlas")!!
		private var i18n = Gdx.files.internal("i18n/cesdoom")

		// I18n
		val SALIR="SALIR"
		val JUGAR="JUGAR"
		val PUNTUACIONES="PUNTUACIONES"
		val CREDITOS="CREDITOS"
		val RECARGAR="RECARGAR"
	}
}
