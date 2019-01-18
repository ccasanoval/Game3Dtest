package com.cesoft.cesdoom.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import com.cesoft.cesdoom.managers.GunFactory
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Assets {

	companion object {
		private val tag: String = Assets::class.java.simpleName
		private val fileHandle = Gdx.files.internal("skin/star-soldier-ui.json")!!
		private val atlasFile = fileHandle.sibling("star-soldier-ui.atlas")!!
		private var i18n = Gdx.files.internal("i18n/cesdoom")

		// I18n
		val MENU="MENU"
		val JUGAR="JUGAR"
		val SALIR="SALIR"
		val RECARGAR="RECARGAR"
		val PUNTUACIONES="PUNTUACIONES"
		val CREDITOS="CREDITOS"
		val SOBRE_TXT="SOBRE_TXT"
		val SOBRE="SOBRE"
		val ATRAS="ATRAS"
		val CONFIG="CONFIG"
		val CONFIG_SOUND="CONFIG_SOUND"
	}

	private val assetManager = AssetManager()

	/// Skin
	var skin: Skin = Skin()
		private set

	/// i18n
	private var i18nBundle = I18NBundle.createBundle(i18n)!!
		fun getString(clave: String):String = i18nBundle.get(clave)
		fun formatString(clave: String, param: Int):String = i18nBundle.format(clave, param)

	//______________________________________________________________________________________________
	init {
		Log.e(tag, "INI-----------------------------------------------------")
		if(atlasFile.exists())
			skin.addRegions(TextureAtlas(atlasFile))
		skin.load(fileHandle)
	}

	// SOUND
	//______________________________________________________________________________________________
	fun iniSoundCZ805() {
		//https://freesound.org/people/SuperPhat/sounds/416417/
		assetManager.load("sounds/assaultrifle.ogg", Music::class.java)
	}
	fun getSoundCZ805() = assetManager.get("sounds/assaultrifle.ogg", Music::class.java)
	fun endSoundCZ805() = assetManager.get("sounds/assaultrifle.ogg", Music::class.java).dispose()
	//______________________________________________________________________________________________
	fun iniSoundEnemy1() {
		//https://freesound.org/people/cylon8472/sounds/326743/
		assetManager.load("sounds/enemy1.ogg", Music::class.java)
	}
	fun getSoundEnemy1() = assetManager.get("sounds/enemy1.ogg", Music::class.java)
	fun endSoundEnemy1() = assetManager.get("sounds/enemy1.ogg", Music::class.java).dispose()
	//
	fun iniSoundEnemy1Die() = assetManager.load("sounds/enemy1die.ogg", Sound::class.java)
	fun getSoundEnemy1Die() = assetManager.get("sounds/enemy1die.ogg", Sound::class.java)
	fun endSoundEnemy1Die() = assetManager.get("sounds/enemy1die.ogg", Sound::class.java).dispose()
	//
	fun iniSoundFootSteps() = assetManager.load("sounds/footsteps.ogg", Music::class.java)
	fun getSoundFootSteps() = assetManager.get("sounds/footsteps.ogg", Music::class.java)
	fun endSoundFootSteps() = assetManager.get("sounds/footsteps.ogg", Music::class.java).dispose()


	// LOADING
    //______________________________________________________________________________________________
    fun iniLoading() {
        assetManager.load("data/loading.pack", TextureAtlas::class.java)
        assetManager.finishLoading()
    }
	fun getLoading():TextureAtlas = assetManager.get("data/loading.pack", TextureAtlas::class.java)
    fun endLoading() {
        assetManager.unload("data/loading.pack")
    }

	// MODELS
	//______________________________________________________________________________________________
	fun iniDome() = assetManager.load("scene/spaceDome/spacedome.g3db", Model::class.java)
	fun getDome():Model = assetManager.get("scene/spaceDome/spacedome.g3db", Model::class.java)
	//______________________________________________________________________________________________
	fun iniEnemy1() = assetManager.load("foes/monster1/a.g3db", Model::class.java)
	fun getEnemy1():Model = assetManager.get("foes/monster1/a.g3db", Model::class.java)
	//______________________________________________________________________________________________
	fun iniCZ805() = assetManager.load("weapons/cz805/a.g3db", Model::class.java)
	fun getCZ805():Model = assetManager.get("weapons/cz805/a.g3db", Model::class.java)

	// IMAGES
	//______________________________________________________________________________________________
	//fun endSuelo() = assetManager.unload("scene/ground.jpg")
	fun iniSuelo() = assetManager.load("scene/ground.jpg", Texture::class.java)
	fun getSuelo():Texture = assetManager.get("scene/ground.jpg", Texture::class.java)
	//______________________________________________________________________________________________
	fun iniSkyline() = assetManager.load("scene/skyline.png", Texture::class.java)
	fun getSkyline():Texture = assetManager.get("scene/skyline.png", Texture::class.java)
	//______________________________________________________________________________________________
	fun iniJunk() = assetManager.load("scene/junk.png", Texture::class.java)
	fun getJunk():Texture = assetManager.get("scene/junk.png", Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallMetal1() = assetManager.load("scene/wall/metal1.jpg", Texture::class.java)
	fun getWallMetal1():Texture = assetManager.get("scene/wall/metal1.jpg", Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallMetal2() = assetManager.load("scene/wall/metal2.png", Texture::class.java)
	fun getWallMetal2():Texture = assetManager.get("scene/wall/metal2.png", Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallMetal3() = assetManager.load("scene/wall/metal3.png", Texture::class.java)
	fun getWallMetal3():Texture = assetManager.get("scene/wall/metal3.png", Texture::class.java)
	//______________________________________________________________________________________________
	fun iniFireShot() = assetManager.load("weapons/fire.png", Texture::class.java)
	fun getFireShot():Image = Image(assetManager.get("weapons/fire.png", Texture::class.java))
	private fun endFireShot() = assetManager.get("weapons/fire.png", Texture::class.java).dispose()

	// PARTICLES
	//______________________________________________________________________________________________
	var particleEffectPool: ParticleEffectPool? = null
	fun iniParticleEffectPool(camera: PerspectiveCamera) {
		//Log.e(tag, "iniParticleEffectPool--------------------------------------- 1")
		if(particleEffectPool == null) {
			//Log.e(tag, "iniParticleEffectPool--------------------------------------- 2")
			particleEffectPool = ParticleEffectPool(this, camera)
			assetManager.load("particles/dieparticle.pfx", ParticleEffect::class.java, particleEffectPool!!.loadParam)
			assetManager.finishLoadingAsset("particles/dieparticle.pfx")
		}
	}
	fun getParticleEffectDie(): ParticleEffect {
		return assetManager.get("particles/dieparticle.pfx", ParticleEffect::class.java)
	}
	fun getParticleSystem() : ParticleSystem? {
		return particleEffectPool?.particleSystem
	}


    //______________________________________________________________________________________________
	fun dispose() {
		Log.e(tag, "dispose------------------------------------------------------------------------")

		try {
		getDome().dispose()
		getEnemy1().dispose()
		getCZ805().dispose()
		getSuelo().dispose()
		getSkyline().dispose()
		getJunk().dispose()
		getWallMetal1().dispose()
		getWallMetal2().dispose()
		getWallMetal3().dispose()
		endFireShot()

		getSoundCZ805().dispose()
		getSoundEnemy1().dispose()
		getSoundEnemy1Die().dispose()
		getSoundFootSteps().dispose()
		}
		catch(e: Exception) { Log.e(tag, "dispose:assetManager.dispose:e1: $e") }


		skin.dispose()
		//endParticleEffectDeath()
		GunFactory.dispose()
		try { assetManager.dispose() }
		catch(e: Exception) { Log.e(tag, "dispose:assetManager.dispose:e2: $e") }
	}

    //______________________________________________________________________________________________
    fun getProgress() = assetManager.progress
    fun update() = assetManager.update()

}
