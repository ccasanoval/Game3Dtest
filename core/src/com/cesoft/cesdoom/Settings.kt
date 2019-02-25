package com.cesoft.cesdoom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
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
		val values1 = intArrayOf( 96,   6, 999, 102, 999, 999, 999, 999, 999)
		inputMapping0.clear()
		inputMapping1.clear()
		for(i in 0 until Inputs.MAX) {
			inputMapping0.add(i, prefs.getInteger(PREF_INPUT_MAPPING0+i, values0[i]))
			inputMapping1.add(i, prefs.getInteger(PREF_INPUT_MAPPING1+i, values1[i]))
		}
	}
	fun savePrefs() {
		prefs.putBoolean(PREF_MUSIC_ONOFF, isMusicEnabled)
		prefs.putFloat(PREF_MUSIC_VOLUME, musicVolume)
		prefs.putBoolean(PREF_SOUND_EFFECTS_ONOFF, isSoundEnabled)
		prefs.putFloat(PREF_SOUND_EFFECTS_VOLUME, soundVolume)
		prefs.putBoolean(PREF_PAIN_VIBRATION_ONOFF, isVibrationEnabled)
		prefs.putBoolean(PREF_GPGS_ONOFF, isGPGSEnabled)

		for(i in 0 until Inputs.MAX) {
			prefs.putInteger(PREF_INPUT_MAPPING0+i, inputMapping0[i])
			prefs.putInteger(PREF_INPUT_MAPPING1+i, inputMapping1[i])
		}

		prefs.flush()
	}



	//var highscores = intArrayOf(1000, 800, 500, 300, 100)
	//val file = ".spaceglad"

	/*private val leaderURL = "http://dreamlo.com/lb/PLfBGtHgG02wU0lSzVNrPAG0uQf3J3-UGzK1i7mXmmxA"
	private val request5 = "/pipe/5"
	fun load(leaderboardItems: Array<Label>) {
		val requestBests = Net.HttpRequest(Net.HttpMethods.GET)
		requestBests.url = leaderURL + request5
		Gdx.net.sendHttpRequest(requestBests, object : Net.HttpResponseListener {
			override fun handleHttpResponse(httpResponse: Net.HttpResponse) {
				println(httpResponse)
				val string = httpResponse.resultAsString
				val scores = string.split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
				if(scores.size > 0)
					for(i in scores.indices) {
						val score = scores[i].split("\\|".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
						if(i == 0) {
							leaderboardItems[i].setText((Integer.valueOf(score[score.size - 1])!! + 1).toString() + ")" + score[0] + ": " + score[1])
						}
						else {
							val assets = Assets()
							leaderboardItems[i] = Label((Integer.valueOf(score[score.size - 1])!! + 1).toString() + ")" + score[0] + ": " + score[1], assets.skin)
							assets.dispose()
						}
					}
			}

			override fun failed(t: Throwable) {
				println(t)
			}

			override fun cancelled() {
				println("Cancel")
			}
		})
	}

	fun load() {
		try {
			val filehandle = Gdx.files.external(file)
			val strings = filehandle.readString().split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			soundEnabled = java.lang.Boolean.parseBoolean(strings[0])
			for(i in 0 .. 4) highscores[i] = Integer.parseInt(strings[i + 1])
		}
		catch(e: Throwable) {
		}
	}

	fun save() {
		try {
			val filehandle = Gdx.files.external(file)
			filehandle.writeString(java.lang.Boolean.toString(soundEnabled) + "\n", false)
			for(i in 0 .. 4) filehandle.writeString(Integer.toString(highscores[i]) + "\n", true)
		}
		catch(e: Throwable) {
		}
	}

	fun sendScore(score: Int) {
		val request = Net.HttpRequest("GET")
		request.url = "http://dreamlo.com/lb/PLfBGtHgG02wU0lSzVNrPAG0uQf3J3-UGzK1i7mXmmxA/add/" + "SpaceGladiator" + "/" + score
		Gdx.net.sendHttpRequest(request, object : Net.HttpResponseListener {
			override fun handleHttpResponse(httpResponse: Net.HttpResponse) {}

			override fun failed(t: Throwable) {}

			override fun cancelled() {}
		})
	}

	fun addScore(score: Int) {
		for(i in 0 .. 4) {
			if(highscores[i] < score) {
				for(j in 4 downTo i + 1) highscores[j] = highscores[j - 1]
				highscores[i] = score
			}
		}
	}*/
}
