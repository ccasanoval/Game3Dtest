package com.cesoft.cesdoom.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.cesoft.cesdoom.Settings

object Sounds {

    const val SOUND_RIFLE = "sounds/assaultrifle.ogg"
    const val SOUND_ENEMY = "sounds/enemy1.ogg"
    const val SOUND_ENEMY_DIE = "sounds/enemy1die.ogg"
    const val SOUND_FOOT_STEPS = "sounds/footsteps.ogg"
    //TODO:
    //TODO:Disparo: solo uno y repetir mietras dispara!! usar sound que lo carga en memoria, mas eficiente que music!!!, call music dispose!!
    //TODO:Monstruo: solo suena si esta a menos de xx metros!!
    const val SOUND_DOOR_OPENS = "sounds/.ogg"
    const val SOUND_DOOR_LOCKED = "sounds/.ogg"
    const val SOUND_SWITCH = "sounds/.ogg"
    const val SOUND_GAME_OVER = "sounds/.ogg"
    const val SOUND_YOU_WIN = "sounds/.ogg"
    const val SOUND_PLAYER_HURT = "sounds/.ogg"
    const val MUSIC = "sounds/.ogg" //doom music?

    enum class SoundType { RIFLE, ENEMY, ENEMY_DIE, FOOT_STEPS, DOOR_OPENS, DOOR_LOCKED, SWITCH, GAME_OVER, YOU_WIN, PLAYER_HURT, }

    private val lastPlayed = HashMap<SoundType, Long>()
    private fun soundByType(type: SoundType) : String {
        return when(type) {
            SoundType.RIFLE -> SOUND_RIFLE
            SoundType.ENEMY -> SOUND_ENEMY
            SoundType.ENEMY_DIE -> SOUND_ENEMY_DIE
            SoundType.FOOT_STEPS -> SOUND_FOOT_STEPS
            SoundType.DOOR_OPENS -> SOUND_DOOR_OPENS
            SoundType.DOOR_LOCKED -> SOUND_DOOR_LOCKED
            SoundType.SWITCH -> SOUND_SWITCH
            SoundType.GAME_OVER -> SOUND_GAME_OVER
            SoundType.YOU_WIN -> SOUND_YOU_WIN
            SoundType.PLAYER_HURT -> SOUND_PLAYER_HURT
        }
    }

    private lateinit var assetManager: AssetManager
    fun ini(assetManager: AssetManager) {
        this.assetManager = assetManager
    }

    fun play(soundType: SoundType) {
        val sound = assetManager.get(soundByType(soundType), Sound::class.java)
        if(Settings.isSoundEnabled) {
            val last = lastPlayed[soundType] ?: 0
            if(System.currentTimeMillis() > last + 100) {
                sound.play(Settings.soundVolume)
                lastPlayed[soundType] = System.currentTimeMillis()
            }
        }
    }

}