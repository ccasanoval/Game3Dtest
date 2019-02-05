package com.cesoft.cesdoom.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.util.Log


object Sounds {

    val tag: String = Sounds::class.java.simpleName

	// Thanks to https://freesound.org/
    private const val SOUND_RIFLE = "sounds/assaultrifle.ogg"
    private const val SOUND_ENEMY_ATTACK = "sounds/enemyAttack.ogg"
    private const val SOUND_ENEMY_HURT = "sounds/enemyHurt.ogg"
	private const val SOUND_ENEMY_DIE = "sounds/enemyDie.ogg"
    private const val SOUND_FOOT_STEPS = "sounds/footsteps.ogg"
    //TODO:Monstruo: solo suena si esta a menos de xx metros!!
    private const val SOUND_GATE_OPENS = "sounds/gate.ogg"
    private const val SOUND_GATE_LOCKED = "sounds/gatelocked.ogg"
    private const val SOUND_SWITCH = "sounds/switch.ogg"
    private const val SOUND_GAME_OVER = "sounds/gameOver.ogg"
    private const val SOUND_YOU_WIN = "sounds/missionCompleted.ogg"
    private const val SOUND_PLAYER_HURT = "sounds/playerHurt.ogg"
	private const val SOUND_PLAYER_DYING = "sounds/playerDying.ogg"
    private const val SOUND_NO_AMMO = "sounds/noAmmo.ogg"
    private const val SOUND_AMMO_RELOAD = "sounds/reload.ogg"
	private const val SOUND_HEALTH_RELOAD = "sounds/reload.ogg"//TODO
    // Effects : https://archive.org/details/dsbossit ***
    // http://www.wolfensteingoodies.com/archives/olddoom/music.htm
    private const val MUSIC = "sounds/tnt_doom.ogg" // http://sycraft.org/content/audio/doom.shtml

    enum class SoundType {
		RIFLE, ENEMY_ATTACK, ENEMY_DIE, ENEMY_HURT, FOOT_STEPS, GATE_OPENS, GATE_LOCKED, SWITCH,
		GAME_OVER, YOU_WIN, PLAYER_HURT, PLAYER_DYING, NO_AMMO, AMMO_RELOAD
	}

    private val lastPlayed = HashMap<SoundType, Long>()
    private fun soundByType(type: SoundType) : String {
        return when(type) {
            SoundType.RIFLE -> SOUND_RIFLE
            SoundType.ENEMY_ATTACK -> SOUND_ENEMY_ATTACK
            SoundType.ENEMY_DIE -> SOUND_ENEMY_DIE
			SoundType.ENEMY_HURT -> SOUND_ENEMY_HURT
            SoundType.FOOT_STEPS -> SOUND_FOOT_STEPS
            SoundType.GATE_OPENS -> SOUND_GATE_OPENS
            SoundType.GATE_LOCKED -> SOUND_GATE_LOCKED
            SoundType.SWITCH -> SOUND_SWITCH
            SoundType.GAME_OVER -> SOUND_GAME_OVER
            SoundType.YOU_WIN -> SOUND_YOU_WIN
            SoundType.PLAYER_HURT -> SOUND_PLAYER_HURT
			SoundType.PLAYER_DYING -> SOUND_PLAYER_DYING
            SoundType.NO_AMMO -> SOUND_NO_AMMO
            SoundType.AMMO_RELOAD -> SOUND_AMMO_RELOAD
        }
    }
	private fun minDelay(soundType: SoundType): Int {
		return when(soundType) {
			SoundType.RIFLE -> 100//250
			Sounds.SoundType.NO_AMMO -> 300
			SoundType.PLAYER_HURT -> 800
			SoundType.FOOT_STEPS -> 700
			SoundType.ENEMY_DIE -> 3600
			SoundType.ENEMY_ATTACK -> 1500
			Sounds.SoundType.GATE_OPENS -> 3000
			else-> 300
		}
	}

    private lateinit var assetManager: AssetManager
    fun ini(assetManager: AssetManager) {
        this.assetManager = assetManager
    }
    fun load() {
        assetManager.load(Sounds.MUSIC, Music::class.java)
        for(sound in SoundType.values())
            assetManager.load(soundByType(sound), Sound::class.java)
    }

    fun dispose() {
        try {
            assetManager.get(MUSIC, Music::class.java).dispose()
        } catch(e: Exception) { Log.e(tag, "dispose:e: $e ") }
       for(sound in SoundType.values())
           try {
                assetManager.get(soundByType(sound), Sound::class.java).dispose()
           } catch(e: Exception) { Log.e(tag, "dispose:e: $e ") }
        //assetManager.dispose()
    }

    fun playMusic() {
        val music = assetManager.get(MUSIC, Music::class.java)
        if(Settings.isMusicEnabled && !music.isPlaying) {
			music.isLooping = true
			music.volume = Settings.musicVolume
            music.play()
        }
    }
	fun stopMusic() {
		val music = assetManager.get(MUSIC, Music::class.java)
		music.stop()
	}
    fun play(soundType: SoundType) {
        val sound = assetManager.get(soundByType(soundType), Sound::class.java)
        if(Settings.isSoundEnabled) {
            val last = lastPlayed[soundType] ?: 0
            if(System.currentTimeMillis() > last + minDelay(soundType)) {
                try {
                    sound.play(Settings.soundVolume)
                }
				catch(e: Exception) { Log.e(tag, "play:e: $e") }
                lastPlayed[soundType] = System.currentTimeMillis()
            }
        }
    }

}