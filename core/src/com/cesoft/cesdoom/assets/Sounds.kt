package com.cesoft.cesdoom.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.util.Log

object Sounds {

    private const val SOUND_RIFLE = "sounds/assaultrifle.ogg"
    private const val SOUND_ENEMY_ATTACK = "sounds/enemyAttack.ogg"
    private const val SOUND_ENEMY_DIE = "sounds/enemyDie.ogg"
    private const val SOUND_FOOT_STEPS = "sounds/footsteps.ogg"
    //TODO:Monstruo: solo suena si esta a menos de xx metros!!
    private const val SOUND_GATE_OPENS = "sounds/gate.ogg"
    private const val SOUND_GATE_LOCKED = "sounds/gatelocked.ogg"
    private const val SOUND_SWITCH = "sounds/switch.ogg"
    private const val SOUND_GAME_OVER = "sounds/gameOver2.ogg"
    private const val SOUND_YOU_WIN = "sounds/missionCompleted.ogg"
    private const val SOUND_PLAYER_HURT = "sounds/playerHurt.ogg"
    // Effects : https://archive.org/details/dsbossit ***
    // http://www.wolfensteingoodies.com/archives/olddoom/music.htm
    private const val MUSIC = "sounds/doom.ogg" // Thanks to http://sycraft.org/content/audio/doom.shtml

    enum class SoundType {
		RIFLE, ENEMY_ATTACK, ENEMY_DIE, FOOT_STEPS, GATE_OPENS, GATE_LOCKED, SWITCH,
		GAME_OVER, YOU_WIN, PLAYER_HURT,
	}

    private val lastPlayed = HashMap<SoundType, Long>()
    private fun soundByType(type: SoundType) : String {
        return when(type) {
            SoundType.RIFLE -> SOUND_RIFLE
            SoundType.ENEMY_ATTACK -> SOUND_ENEMY_ATTACK
            SoundType.ENEMY_DIE -> SOUND_ENEMY_DIE
            SoundType.FOOT_STEPS -> SOUND_FOOT_STEPS
            SoundType.GATE_OPENS -> SOUND_GATE_OPENS
            SoundType.GATE_LOCKED -> SOUND_GATE_LOCKED
            SoundType.SWITCH -> SOUND_SWITCH
            SoundType.GAME_OVER -> SOUND_GAME_OVER
            SoundType.YOU_WIN -> SOUND_YOU_WIN
            SoundType.PLAYER_HURT -> SOUND_PLAYER_HURT
        }
    }
	private fun minDelay(soundType: SoundType): Int {
		return when(soundType) {
			Sounds.SoundType.RIFLE -> 250
			Sounds.SoundType.FOOT_STEPS -> 700
			Sounds.SoundType.ENEMY_DIE -> 2000
			Sounds.SoundType.ENEMY_ATTACK -> 1500
			Sounds.SoundType.GATE_OPENS -> 4000
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
        assetManager.get(MUSIC, Music::class.java).dispose()
        assetManager.get(Sounds.SOUND_RIFLE, Sound::class.java).dispose()
        assetManager.get(Sounds.SOUND_ENEMY_ATTACK, Sound::class.java).dispose()
        assetManager.get(Sounds.SOUND_ENEMY_DIE, Sound::class.java).dispose()
        assetManager.get(Sounds.SOUND_FOOT_STEPS, Sound::class.java).dispose()
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
				catch (e: Exception) { Log.e("Sounds", "play:e: $e") }
                lastPlayed[soundType] = System.currentTimeMillis()
            }
        }
    }

}