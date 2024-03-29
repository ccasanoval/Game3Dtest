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
	private const val SOUND_ENEMY_NEAR = "sounds/enemyNear.ogg"
	private const val SOUND_ENEMY1 = "sounds/enemy1.ogg"
	private const val SOUND_ENEMY1_DIE = "sounds/enemyDie1.ogg"
    private const val SOUND_FOOT_STEPS = "sounds/footsteps.ogg"
    //TODO:Monstruo: solo suena si esta a menos de xx metros!!
    private const val SOUND_GATE_OPENS = "sounds/gate.ogg"
    private const val SOUND_GATE_LOCKED = "sounds/gatelocked.ogg"
    private const val SOUND_SWITCH = "sounds/switch.ogg"
    private const val SOUND_GAME_OVER = "sounds/gameOver.ogg"
    private const val SOUND_YOU_WIN = "sounds/missionCompleted.ogg"
    private const val SOUND_YOU_WIN_OVER = "sounds/youWinGame.ogg"
    private const val SOUND_PLAYER_HURT = "sounds/playerHurt.ogg"
	private const val SOUND_PLAYER_DYING = "sounds/playerDying.ogg"
    private const val SOUND_NO_AMMO = "sounds/noAmmo.ogg"
    private const val SOUND_AMMO_RELOAD = "sounds/reload.ogg"
	private const val SOUND_HEALTH_RELOAD = "sounds/reloadHealth.ogg"
    // Effects : https://archive.org/details/dsbossit ***
    // http://www.wolfensteingoodies.com/archives/olddoom/music.htm
    private const val MUSIC = "sounds/tnt_doom.ogg" // http://sycraft.org/content/audio/doom.shtml

    enum class SoundType {
		ENEMY_ATTACK, ENEMY_DIE, ENEMY_HURT, ENEMY_NEAR,
        ENEMY1_ATTACK, ENEMY1_DIE, ENEMY1_HURT, ENEMY1_NEAR,
        RIFLE, FOOT_STEPS, GATE_OPENS, GATE_LOCKED, SWITCH,
		GAME_OVER, YOU_WIN, YOU_WIN_OVER,
        PLAYER_HURT, PLAYER_DYING, NO_AMMO, AMMO_RELOAD, HEALTH_RELOAD
	}

    private val lastPlayed = HashMap<SoundType, Long>()
    private fun soundByType(type: SoundType) : String {
        return when(type) {
            SoundType.RIFLE -> SOUND_RIFLE
            SoundType.ENEMY_ATTACK -> SOUND_ENEMY_ATTACK
            SoundType.ENEMY_DIE -> SOUND_ENEMY_DIE
			SoundType.ENEMY_HURT -> SOUND_ENEMY_HURT
			SoundType.ENEMY_NEAR -> SOUND_ENEMY_NEAR
            SoundType.ENEMY1_ATTACK -> SOUND_ENEMY1
            SoundType.ENEMY1_DIE -> SOUND_ENEMY1_DIE
            SoundType.ENEMY1_HURT -> SOUND_ENEMY1_DIE
            SoundType.ENEMY1_NEAR -> SOUND_ENEMY1
            SoundType.FOOT_STEPS -> SOUND_FOOT_STEPS
            SoundType.GATE_OPENS -> SOUND_GATE_OPENS
            SoundType.GATE_LOCKED -> SOUND_GATE_LOCKED
            SoundType.SWITCH -> SOUND_SWITCH
            SoundType.GAME_OVER -> SOUND_GAME_OVER
            SoundType.YOU_WIN -> SOUND_YOU_WIN
            SoundType.YOU_WIN_OVER -> SOUND_YOU_WIN_OVER
            SoundType.PLAYER_HURT -> SOUND_PLAYER_HURT
			SoundType.PLAYER_DYING -> SOUND_PLAYER_DYING
            SoundType.NO_AMMO -> SOUND_NO_AMMO
            SoundType.AMMO_RELOAD -> SOUND_AMMO_RELOAD
			SoundType.HEALTH_RELOAD -> SOUND_HEALTH_RELOAD
        }
    }
	private fun minDelay(soundType: SoundType): Int {
		return when(soundType) {
			SoundType.RIFLE -> 100//250
			SoundType.NO_AMMO -> 300
			SoundType.PLAYER_HURT -> 800
			SoundType.FOOT_STEPS -> 700
			SoundType.ENEMY_DIE -> 3600
            SoundType.ENEMY_HURT -> 3600
			SoundType.ENEMY_ATTACK -> 2000
            SoundType.ENEMY_NEAR -> 3000
            SoundType.ENEMY1_DIE -> 1500
            SoundType.ENEMY1_HURT -> 3000
            SoundType.ENEMY1_ATTACK -> 2000
			SoundType.ENEMY1_NEAR -> 15000
			SoundType.GATE_OPENS -> 3000
			else-> 300
		}
	}

    private var assetManager: AssetManager? = null//TODO:Static Context! --------------------------------------------
    fun ini(assetManager: AssetManager) {
        this.assetManager = assetManager
    }
    fun load() {
        assetManager?.load(MUSIC, Music::class.java)
        for(sound in SoundType.values())
            assetManager?.load(soundByType(sound), Sound::class.java)
    }

    fun dispose() {
        try {
            assetManager?.get(MUSIC, Music::class.java)?.dispose()
        } catch(e: Exception) { Log.e(tag, "dispose:e: $e ") }
       for(sound in SoundType.values())
           try {
                assetManager?.get(soundByType(sound), Sound::class.java)?.dispose()
               assetManager = null
           } catch(e: Exception) { Log.e(tag, "dispose:e: $e ") }
        //assetManager.dispose()
    }

    fun playMusic() {
        val music = assetManager?.get(MUSIC, Music::class.java)
        music?.let {
            if(Settings.isMusicEnabled && !music.isPlaying) {
                music.isLooping = true
                music.volume = Settings.musicVolume
                music.play()
            }
        }
    }
	fun stopMusic() {
		val music = assetManager?.get(MUSIC, Music::class.java)
		music?.stop()
	}
    fun play(soundType: SoundType) {
        val sound = assetManager?.get(soundByType(soundType), Sound::class.java)
        if(Settings.isSoundEnabled) {
            val last = lastPlayed[soundType] ?: 0
            if(System.currentTimeMillis() > last + minDelay(soundType)) {
                try {
                    sound?.play(Settings.soundVolume)
                }
				catch(e: Exception) { Log.e(tag, "play:e: $e") }
                lastPlayed[soundType] = System.currentTimeMillis()
            }
        }
    }

}