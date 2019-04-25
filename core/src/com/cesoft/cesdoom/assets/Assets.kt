package com.cesoft.cesdoom.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import com.cesoft.cesdoom.components.EnemyComponent
import com.cesoft.cesdoom.managers.GunFactory
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
// TEXTURES: https://steamcommunity.com/id/Hoover1979/images/?appid=2280
// TEXTURES: DOOM LIKE: https://imgur.com/a/6zVaU
//@Singleton
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
		const val NEXT_LEVEL="NEXT_LEVEL"
		const val PUNTUACIONES="PUNTUACIONES"
		const val LOGROS="LOGROS"
		const val GPGS_SIGN_IN="GPGS_SIGN_IN"
		const val CREDITOS="CREDITOS"
		const val AMMO="AMMO"
		const val SOBRE_TXT="SOBRE_TXT"
		const val SOBRE="SOBRE"
		const val ATRAS="ATRAS"
		const val PUNTUA="PUNTUA"
		const val CONFIG="CONFIG"
		const val CONFIG_SOUND_EFFECTS_VOLUME="CONFIG_SOUND_EFFECTS_VOLUME"
		const val CONFIG_SOUND_EFFECTS_ONOF="CONFIG_SOUND_EFFECTS_ONOF"
        const val CONFIG_MUSIC_VOLUME="CONFIG_MUSIC_VOLUME"
        const val CONFIG_MUSIC_ONOF="CONFIG_MUSIC_ONOF"
		const val CONFIG_VIBRATION_ONOF="CONFIG_VIBRATION_ONOF"
		const val CONFIG_GPGS_ONOF="CONFIG_GPGS_ONOF"
		const val GATE_LOCKED="GATE_LOCKED"
		const val GATE_OPENS="GATE_OPENS"
		const val GATE_UNLOCKED="GATE_UNLOCKED"

		/// MODELS
		private const val MODEL_DOME = "scene/dome/spacedome.g3db"
		private const val MODEL_MONSTER0 = "foes/monster0/a.g3db"	//Thanks to https://www.turbosquid.com/FullPreview/Index.cfm/ID/312341
		private const val MODEL_MONSTER1 = "foes/monster1/out.g3db"
		private const val MODEL_RIFLE = "weapons/cz805/a.g3db"
		private const val MODEL_AMMO = "items/ammo/bullet.g3db"
		private const val MODEL_HEALTH = "items/health/can.g3db"
		//https://free3d.com/3d-model/spider-animated-low-poly-and-game-ready-87147.html

		/// IMG
		private const val IMG_FIRE_SHOT = "weapons/fire.png"
		private const val IMG_GROUND = "scene/ground.jpg"
		private const val IMG_SKYLINE = "scene/skyline.png"
		private const val IMG_JUNK = "scene/junk.png"
		private const val IMG_JUNK1 = "scene/junk1.png"
		private const val IMG_JUNK2 = "scene/junk2.png"
		private const val IMG_WALL_CONCRETE = "scene/wall/concrete.jpg"
		private const val IMG_WALL_STEEL = "scene/wall/steel.png"
		private const val IMG_WALL_GRILLE = "scene/wall/grille.png"
		private const val IMG_WALL_CIRCUITS = "scene/wall/circuits.jpg"
		private const val IMG_GATE = "scene/gate/doomdoor1.jpg"
		private const val IMG_SWITCH_ON = "scene/switch/switchOn.png"
		private const val IMG_SWITCH_OFF = "scene/switch/switchOff.png"
		private const val IMG_BIKE = "scene/bike.png"
		//
		private const val IMG_MM_BG = "data/background.png"
		private const val IMG_MM_TITLE = "data/title.png"

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
		fun formatString(clave: String, param: Any):String = i18nBundle.format(clave, param)

	//______________________________________________________________________________________________
	init {
		if(atlasFile.exists())
			skin.addRegions(TextureAtlas(atlasFile))
		skin.load(fileHandle)
		Sounds.ini(assetManager)
	}

	// MAIN MENU
	//______________________________________________________________________________________________
	fun iniMainMenuBg() {
		assetManager.load(IMG_MM_BG, Texture::class.java)
		assetManager.finishLoading()
	}
	fun getMainMenuBg():Image = Image(assetManager.get(IMG_MM_BG, Texture::class.java))
	fun iniMainMenuTitle() {
		assetManager.load(IMG_MM_TITLE, Texture::class.java)
		assetManager.finishLoading()
	}
	fun getMainMenuTitle():Image = Image(assetManager.get(IMG_MM_TITLE, Texture::class.java))



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
	private fun removeEmissiveAttrib(modelFileName: String):Model {
		val model = assetManager.get(modelFileName, Model::class.java)
		for(i in 0 until model.materials.size)//Some blender models comes with emissive colors...
			model.materials.get(i).set(ColorAttribute(ColorAttribute.Emissive, Color.BLACK))
		return model
	}
	fun iniDome() = assetManager.load(MODEL_DOME, Model::class.java)
	fun getDome():Model = removeEmissiveAttrib(MODEL_DOME)
	//______________________________________________________________________________________________
	fun iniEnemy(type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER0) {
		when(type) {
			EnemyComponent.TYPE.MONSTER0 -> assetManager.load(MODEL_MONSTER0, Model::class.java)
			EnemyComponent.TYPE.MONSTER1 -> assetManager.load(MODEL_MONSTER1, Model::class.java)
		}
	}
	fun getEnemy(type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER0): Model {
		return when(type) {
			EnemyComponent.TYPE.MONSTER0 -> removeEmissiveAttrib(MODEL_MONSTER0)
			EnemyComponent.TYPE.MONSTER1 -> removeEmissiveAttrib(MODEL_MONSTER1)
		}
	}
	//______________________________________________________________________________________________
	fun iniAmmo() = assetManager.load(MODEL_AMMO, Model::class.java)
	fun getAmmo():Model = removeEmissiveAttrib(MODEL_AMMO)
	//______________________________________________________________________________________________
	fun iniHealth() = assetManager.load(MODEL_HEALTH, Model::class.java)
	fun getHealth():Model = removeEmissiveAttrib(MODEL_HEALTH)
	//______________________________________________________________________________________________
	fun iniRifle() = assetManager.load(MODEL_RIFLE, Model::class.java)
	fun getRifle():Model = removeEmissiveAttrib(MODEL_RIFLE)

	// IMAGES
	//______________________________________________________________________________________________
	fun iniSuelo() = assetManager.load(IMG_GROUND, Texture::class.java)
	fun getSuelo():Texture = assetManager.get(IMG_GROUND, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniSkyline() = assetManager.load(IMG_SKYLINE, Texture::class.java)
	fun getSkyline():Texture = assetManager.get(IMG_SKYLINE, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniJunkAntenna() = assetManager.load(IMG_JUNK, Texture::class.java)
	fun getJunkAntenna():Texture = assetManager.get(IMG_JUNK, Texture::class.java)
	fun iniJunkWall() = assetManager.load(IMG_JUNK1, Texture::class.java)
	fun getJunkWall():Texture = assetManager.get(IMG_JUNK1, Texture::class.java)
	fun iniJunkBuilding() = assetManager.load(IMG_JUNK2, Texture::class.java)
	fun getJunkBuilding():Texture = assetManager.get(IMG_JUNK2, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallConcrete() = assetManager.load(IMG_WALL_CONCRETE, Texture::class.java)
	fun getWallConcrete():Texture = assetManager.get(IMG_WALL_CONCRETE, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallSteel() = assetManager.load(IMG_WALL_STEEL, Texture::class.java)
	fun getWallSteel():Texture = assetManager.get(IMG_WALL_STEEL, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallGrille() = assetManager.load(IMG_WALL_GRILLE, Texture::class.java)
	fun getWallGrille():Texture = assetManager.get(IMG_WALL_GRILLE, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniWallCircuits() = assetManager.load(IMG_WALL_CIRCUITS, Texture::class.java)
	fun getWallCircuits():Texture = assetManager.get(IMG_WALL_CIRCUITS, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniGate() = assetManager.load(IMG_GATE, Texture::class.java)
	fun getGate():Texture = assetManager.get(IMG_GATE, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniSwitchOn() = assetManager.load(IMG_SWITCH_ON, Texture::class.java)
	fun getSwitchOn():Texture = assetManager.get(IMG_SWITCH_ON, Texture::class.java)
	fun iniSwitchOff() = assetManager.load(IMG_SWITCH_OFF, Texture::class.java)
	fun getSwitchOff():Texture = assetManager.get(IMG_SWITCH_OFF, Texture::class.java)
	//______________________________________________________________________________________________
	fun iniBike() = assetManager.load(IMG_BIKE, Texture::class.java)
	fun getBike():Texture = assetManager.get(IMG_BIKE, Texture::class.java)

	//______________________________________________________________________________________________
	fun iniFireShot() = assetManager.load(IMG_FIRE_SHOT, Texture::class.java)
	fun getFireShot():Image = Image(assetManager.get(IMG_FIRE_SHOT, Texture::class.java))
	private fun endFireShot() = assetManager.get(IMG_FIRE_SHOT, Texture::class.java).dispose()

	// PARTICLES
	//______________________________________________________________________________________________
	private var particleEffectPool: ParticleEffectPool? = null
	fun iniParticleEffectPool(camera: PerspectiveCamera) {
		if(particleEffectPool == null)
			particleEffectPool = ParticleEffectPool(assetManager)
		particleEffectPool?.setCamera(camera)
	}
	fun newParticleEffect(): ParticleEffect {
		return particleEffectPool!!.obtain()
	}
	fun getParticleSystem() : ParticleSystem? {
		return particleEffectPool?.particleSystem
	}


    //______________________________________________________________________________________________
	fun dispose() {
		try {
			getDome().dispose()
			getEnemy().dispose()
			getRifle().dispose()
			getSuelo().dispose()
			getSkyline().dispose()
			getJunkAntenna().dispose()
			getJunkWall().dispose()
			getJunkBuilding().dispose()
			getBike().dispose()
			getWallConcrete().dispose()
			getWallSteel().dispose()
			getWallGrille().dispose()
			getWallCircuits().dispose()
			endFireShot()
		}
		catch(e: Exception) { Log.e(tag, "dispose:assetManager.dispose:e1: $e") }

		skin.dispose()
		particleEffectPool?.dispose()
		particleEffectPool=null
		GunFactory.dispose()
		try { assetManager.dispose() }
		catch(e: Exception) { Log.e(tag, "dispose:assetManager.dispose:e2: $e") }
	}

    //______________________________________________________________________________________________
    fun getProgress() = assetManager.progress
    fun update() = assetManager.update()

}
