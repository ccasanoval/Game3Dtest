package com.cesoft.cesdoom.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
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
// TEXTURES: https://steamcommunity.com/id/Hoover1979/images/?appid=2280
// TEXTURES: DOOM LIKE: https://imgur.com/a/6zVaU
class Assets {

	companion object {
		private val tag: String = Assets::class.java.simpleName
		private val fileHandle = Gdx.files.internal("skin/star-soldier-ui.json")!!
		private val atlasFile = fileHandle.sibling("star-soldier-ui.atlas")!!
		private var i18n = Gdx.files.internal("i18n/cesdoom")

		// I18n
		const val MENU="MENU"
		const val JUGAR="JUGAR"
		const val SALIR="SALIR"
		const val RECARGAR="RECARGAR"
		const val PUNTUACIONES="PUNTUACIONES"
		const val CREDITOS="CREDITOS"
		const val SOBRE_TXT="SOBRE_TXT"
		const val SOBRE="SOBRE"
		const val ATRAS="ATRAS"
		const val CONFIG="CONFIG"
		const val CONFIG_SOUND_VOLUME="CONFIG_SOUND_VOLUME"
		const val CONFIG_SOUND_ONOF="CONFIG_SOUND_ONOF"

		/// MODELS
		private const val MODEL_DOME = "scene/dome/spacedome.g3db"
		private const val MODEL_MONSTER = "foes/monster1/a.g3db"
		private const val MODEL_RIFLE = "weapons/cz805/a.g3db"

		/// IMG
		private const val IMG_FIRE_SHOT = "weapons/fire.png"
		private const val IMG_GROUND = "scene/ground.jpg"
		private const val IMG_SKYLINE = "scene/skyline.png"
		private const val IMG_JUNK = "scene/junk.png"
		private const val IMG_METAL1 = "scene/wall/metal1.jpg"
		private const val IMG_METAL2 = "scene/wall/metal2.png"
		private const val IMG_METAL3 = "scene/wall/metal3.png"
		private const val IMG_GATE = "scene/gate/doomdoor1.jpg"
		private const val IMG_SWITCH_ON = "scene/switch/switchOn.png"
		private const val IMG_SWITCH_OFF = "scene/switch/switchOff.png"

		/// PARTICLES
		private const val PARTICLES_ENEMY = "particles/dieparticle.pfx"
		/// TEXTURE
		private const val TXT_LOADING = "data/loading.pack"

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
		Sounds.ini(assetManager)
	}

	// SOUND
	//______________________________________________________________________________________________
	fun iniSoundRifle() {
		//https://freesound.org/people/SuperPhat/sounds/416417/
		assetManager.load(Sounds.SOUND_RIFLE, Sound::class.java)
	}
	fun getSoundRifle() = assetManager.get(Sounds.SOUND_RIFLE, Sound::class.java)
	fun endSoundRifle() = assetManager.get(Sounds.SOUND_RIFLE, Sound::class.java).dispose()
	//______________________________________________________________________________________________
	fun iniSoundEnemy() {
		//https://freesound.org/people/cylon8472/sounds/326743/
		assetManager.load(Sounds.SOUND_ENEMY, Sound::class.java)
	}
	fun getSoundEnemy() = assetManager.get(Sounds.SOUND_ENEMY, Sound::class.java)
	fun endSoundEnemy() = assetManager.get(Sounds.SOUND_ENEMY, Sound::class.java).dispose()
	//
	fun iniSoundEnemyDie() = assetManager.load(Sounds.SOUND_ENEMY_DIE, Sound::class.java)
	fun getSoundEnemyDie() = assetManager.get(Sounds.SOUND_ENEMY_DIE, Sound::class.java)
	fun endSoundEnemyDie() = assetManager.get(Sounds.SOUND_ENEMY_DIE, Sound::class.java).dispose()
	//
	fun iniSoundFootSteps() = assetManager.load(Sounds.SOUND_FOOT_STEPS, Sound::class.java)
	fun getSoundFootSteps() = assetManager.get(Sounds.SOUND_FOOT_STEPS, Sound::class.java)
	fun endSoundFootSteps() = assetManager.get(Sounds.SOUND_FOOT_STEPS, Sound::class.java).dispose()


	// LOADING
    //______________________________________________________________________________________________
    fun iniLoading() {
        assetManager.load(TXT_LOADING, TextureAtlas::class.java)
        assetManager.finishLoading()
    }
	fun getLoading():TextureAtlas = assetManager.get(TXT_LOADING, TextureAtlas::class.java)
    fun endLoading() = assetManager.unload(TXT_LOADING)

	// MODELS
	//______________________________________________________________________________________________
	fun iniDome() = assetManager.load(MODEL_DOME, Model::class.java)
	fun getDome():Model = assetManager.get(MODEL_DOME, Model::class.java)
	//______________________________________________________________________________________________
	fun iniEnemy() = assetManager.load(MODEL_MONSTER, Model::class.java)
	fun getEnemy():Model = assetManager.get(MODEL_MONSTER, Model::class.java)
	//______________________________________________________________________________________________
	fun iniRifle() = assetManager.load(MODEL_RIFLE, Model::class.java)
	fun getRifle():Model = assetManager.get(MODEL_RIFLE, Model::class.java)

	// IMAGES
	//______________________________________________________________________________________________
	//fun endSuelo() = assetManager.unload(IMG_GROUND)
	fun iniSuelo() = assetManager.load(IMG_GROUND, Texture::class.java)
	fun getSuelo():Texture = assetManager.get(IMG_GROUND, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniSkyline() = assetManager.load(IMG_SKYLINE, Texture::class.java)
	fun getSkyline():Texture = assetManager.get(IMG_SKYLINE, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniJunk() = assetManager.load(IMG_JUNK, Texture::class.java)
	fun getJunk():Texture = assetManager.get(IMG_JUNK, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallMetal1() = assetManager.load(IMG_METAL1, Texture::class.java)
	fun getWallMetal1():Texture = assetManager.get(IMG_METAL1, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallMetal2() = assetManager.load(IMG_METAL2, Texture::class.java)
	fun getWallMetal2():Texture = assetManager.get(IMG_METAL2, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallMetal3() = assetManager.load(IMG_METAL3, Texture::class.java)
	fun getWallMetal3():Texture = assetManager.get(IMG_METAL3, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniGate() = assetManager.load(IMG_GATE, Texture::class.java)
	fun getGate():Texture = assetManager.get(IMG_GATE, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniSwitchOn() = assetManager.load(IMG_SWITCH_ON, Texture::class.java)
	fun getSwitchOn():Texture = assetManager.get(IMG_SWITCH_ON, Texture::class.java)
	fun iniSwitchOff() = assetManager.load(IMG_SWITCH_OFF, Texture::class.java)
	fun getSwitchOff():Texture = assetManager.get(IMG_SWITCH_OFF, Texture::class.java)

	//______________________________________________________________________________________________
	fun iniFireShot() = assetManager.load(IMG_FIRE_SHOT, Texture::class.java)
	fun getFireShot():Image = Image(assetManager.get(IMG_FIRE_SHOT, Texture::class.java))
	private fun endFireShot() = assetManager.get(IMG_FIRE_SHOT, Texture::class.java).dispose()

	// PARTICLES
	//______________________________________________________________________________________________
	var particleEffectPool: ParticleEffectPool? = null
	fun iniParticleEffectPool(camera: PerspectiveCamera) {
		if(particleEffectPool == null) {
			particleEffectPool = ParticleEffectPool(this, camera)
			assetManager.load(PARTICLES_ENEMY, ParticleEffect::class.java, particleEffectPool!!.loadParam)
			assetManager.finishLoadingAsset(PARTICLES_ENEMY)
		}
	}
	fun getParticleEffectDie(): ParticleEffect {
		return assetManager.get(PARTICLES_ENEMY, ParticleEffect::class.java)
	}
	fun getParticleSystem() : ParticleSystem? {
		return particleEffectPool?.particleSystem
	}


    //______________________________________________________________________________________________
	fun dispose() {
		Log.e(tag, "dispose------------------------------------------------------------------------")

		try {
		getDome().dispose()
		getEnemy().dispose()
		getRifle().dispose()
		getSuelo().dispose()
		getSkyline().dispose()
		getJunk().dispose()
		getWallMetal1().dispose()
		getWallMetal2().dispose()
		getWallMetal3().dispose()
		endFireShot()

		getSoundRifle().dispose()
		getSoundEnemy().dispose()
		getSoundEnemyDie().dispose()
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
