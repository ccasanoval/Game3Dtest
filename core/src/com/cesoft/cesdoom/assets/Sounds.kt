package com.cesoft.cesdoom.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.util.Log

object Sounds {

    const val SOUND_RIFLE = "sounds/assaultrifle.ogg"
    const val SOUND_ENEMY = "sounds/enemy1.ogg"
    const val SOUND_ENEMY_DIE = "sounds/enemy1die.ogg"
    const val SOUND_FOOT_STEPS = "sounds/footsteps.ogg"
    //TODO:
    //TODO:Disparo: solo uno y repetir mietras dispara!! usar sound que lo carga en memoria, mas eficiente que music!!!, call music dispose!!
    //TODO:Monstruo: solo suena si esta a menos de xx metros!!
    const val SOUND_GATE_OPENS = "sounds/gate.ogg"
    const val SOUND_GATE_LOCKED = "sounds/footsteps.ogg"
    const val SOUND_SWITCH = "sounds/switch.ogg"
    const val SOUND_GAME_OVER = "sounds/footsteps.ogg"
    const val SOUND_YOU_WIN = "sounds/footsteps.ogg"
    const val SOUND_PLAYER_HURT = "sounds/footsteps.ogg"
    // Effects : https://archive.org/details/dsbossit ***
    // http://www.wolfensteingoodies.com/archives/olddoom/music.htm
    const val MUSIC = "sounds/tnt_doom.ogg" // Thanks to http://sycraft.org/content/audio/doom.shtml

    enum class SoundType { RIFLE, ENEMY, ENEMY_DIE, FOOT_STEPS, GATE_OPENS, GATE_LOCKED, SWITCH, GAME_OVER, YOU_WIN, PLAYER_HURT, }

    private val lastPlayed = HashMap<SoundType, Long>()
    private fun soundByType(type: SoundType) : String {
        return when(type) {
            SoundType.RIFLE -> SOUND_RIFLE
            SoundType.ENEMY -> SOUND_ENEMY
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
        assetManager.get(Sounds.SOUND_ENEMY, Sound::class.java).dispose()
        assetManager.get(Sounds.SOUND_ENEMY_DIE, Sound::class.java).dispose()
        assetManager.get(Sounds.SOUND_FOOT_STEPS, Sound::class.java).dispose()
        //assetManager.dispose()
    }

    fun playMusic() {
        val music = assetManager.get(MUSIC, Music::class.java)
        if(Settings.isMusicEnabled && !music.isPlaying) {
            music.play()
        }
    }
    fun play(soundType: SoundType) {
        val sound = assetManager.get(soundByType(soundType), Sound::class.java)
        if(Settings.isSoundEnabled) {
            val last = lastPlayed[soundType] ?: 0
            if(System.currentTimeMillis() > last + 300) {
                try {
                    sound.play(Settings.soundVolume)
                } catch (e: Exception) { Log.e("Sounds", "play:e: $e") }
                lastPlayed[soundType] = System.currentTimeMillis()
            }
        }
    }

}