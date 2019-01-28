package com.cesoft.cesdoom

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.cesoft.cesdoom.assets.Assets

/*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

class AppPreferences {

	protected val prefs: Preferences
		get() = Gdx.app.getPreferences(PREFS_NAME)

	var isSoundEffectsEnabled: Boolean
		get() = prefs.getBoolean(PREF_SOUND_ENABLED, true)
		set(soundEffectsEnabled) {
			prefs.putBoolean(PREF_SOUND_ENABLED, soundEffectsEnabled)
			prefs.flush()
		}

	var isMusicEnabled: Boolean
		get() = prefs.getBoolean(PREF_MUSIC_ENABLED, true)
		set(musicEnabled) {
			prefs.putBoolean(PREF_MUSIC_ENABLED, musicEnabled)
			prefs.flush()
		}

	var musicVolume: Float
		get() = prefs.getFloat(PREF_MUSIC_VOLUME, 0.5f)
		set(volume) {
			prefs.putFloat(PREF_MUSIC_VOLUME, volume)
			prefs.flush()
		}

	var soundVolume: Float
		get() = prefs.getFloat(PREF_SOUND_VOL, 0.5f)
		set(volume) {
			prefs.putFloat(PREF_SOUND_VOL, volume)
			prefs.flush()
		}

	companion object {
		private val PREF_MUSIC_VOLUME = "volume"
		private val PREF_MUSIC_ENABLED = "music.enabled"
		private val PREF_SOUND_ENABLED = "sound.enabled"
		private val PREF_SOUND_VOL = "sound"
		private val PREFS_NAME = "b2dtut"
	}
}*/


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object Settings {

	var paused: Boolean = false
	var gameOver: Boolean = false
	var gameWin: Boolean = false
	var mainMenu: Boolean = false
	var isSoundEnabled = false

	private var soundVolume = 100f

	fun getMusicVolume() = soundVolume
	fun setMusicVolume(volume: Float) {
		soundVolume = volume
		//Assets.getSoundEnemy1() .setVolume()
		//getSoundEnemy1Die()
		//getSoundFootSteps()
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
