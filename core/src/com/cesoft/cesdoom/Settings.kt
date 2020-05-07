package com.cesoft.cesdoom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.cesoft.cesdoom.input.InputMapper
import com.cesoft.cesdoom.input.Inputs


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object Settings {
	private const val PREF_NAME = "CesDooM_Prefs"
	private const val PREF_MUSIC_ONOFF = "music_onoff"
	private const val PREF_MUSIC_VOLUME = "music_volume"
	private const val PREF_SOUND_EFFECTS_ONOFF = "sound_onoff"
	private const val PREF_SOUND_EFFECTS_VOLUME = "sound_volume"
	private const val PREF_PAIN_VIBRATION_ONOFF = "pain_vibration"
	private const val PREF_GPGS_ONOFF = "google_game_services"
	private const val PREF_INPUT_MAPPING0 = "input_mapping0_"
	private const val PREF_INPUT_MAPPING1 = "input_mapping1_"
	const val GAMEPAD_INPUT_DELAY = .110f

	var isMusicEnabled = true
	var isSoundEnabled = true
	var isVibrationEnabled = true
	var isGPGSEnabled = false
	var soundVolume:Float = 1f
		set(value) {
			if(value in 0.0f..1.0f)
				field = value
		}
	var musicVolume:Float = 1f
		set(value) {
			if(value in 0.0f..1.0f)
				field = value
		}
	var inputMapping0: MutableList<Int> = arrayListOf()
	var inputMapping1: MutableList<Int> = arrayListOf()
	fun getInputMapper(): InputMapper {
		val im = InputMapper()
		for(action in Inputs.ActionName.values()) {
			val offset = when(action) {
				Inputs.ActionName.MOVE_X -> .6f
				Inputs.ActionName.MOVE_Y -> .4f
				Inputs.ActionName.LOOK_X -> .4f
				Inputs.ActionName.LOOK_Y -> .6f
				else -> 0f
			}
			im.addMap(inputMapping0[action.value], Inputs.Action.getAction(action), offset)
			im.addMap(inputMapping1[action.value], Inputs.Action.getAction(action), offset)
		}
        return im
	}

	/// Preferencias
	//
	private val prefs: Preferences = Gdx.app.getPreferences(PREF_NAME)
	fun loadPrefs() {
		isMusicEnabled = prefs.getBoolean(PREF_MUSIC_ONOFF, isMusicEnabled)
		musicVolume = prefs.getFloat(PREF_MUSIC_VOLUME, musicVolume)
		isSoundEnabled = prefs.getBoolean(PREF_SOUND_EFFECTS_ONOFF, isSoundEnabled)
		soundVolume = prefs.getFloat(PREF_SOUND_EFFECTS_VOLUME, soundVolume)
		isVibrationEnabled = prefs.getBoolean(PREF_PAIN_VIBRATION_ONOFF, isVibrationEnabled)
		isGPGSEnabled = prefs.getBoolean(PREF_GPGS_ONOFF, isGPGSEnabled)

		val values0 = intArrayOf(108,  97,  99, 103, 100,   4,   5,   0,   1)
		val values1 = intArrayOf( 96,   6,  -1, 102,  -1,  -1,  -1,  -1,  -1)
		inputMapping0.clear()
		inputMapping1.clear()
		for(i in Inputs.ActionName.values()) {
			inputMapping0.add(i.value, prefs.getInteger(PREF_INPUT_MAPPING0+i, values0[i.value]))
			inputMapping1.add(i.value, prefs.getInteger(PREF_INPUT_MAPPING1+i, values1[i.value]))
		}
	}
	fun savePrefs() {
		prefs.putBoolean(PREF_MUSIC_ONOFF, isMusicEnabled)
		prefs.putFloat(PREF_MUSIC_VOLUME, musicVolume)
		prefs.putBoolean(PREF_SOUND_EFFECTS_ONOFF, isSoundEnabled)
		prefs.putFloat(PREF_SOUND_EFFECTS_VOLUME, soundVolume)
		prefs.putBoolean(PREF_PAIN_VIBRATION_ONOFF, isVibrationEnabled)
		prefs.putBoolean(PREF_GPGS_ONOFF, isGPGSEnabled)

		for(i in Inputs.ActionName.values()) {
			prefs.putInteger(PREF_INPUT_MAPPING0+i, inputMapping0[i.value])
			prefs.putInteger(PREF_INPUT_MAPPING1+i, inputMapping1[i.value])
		}

		prefs.flush()
	}
}
