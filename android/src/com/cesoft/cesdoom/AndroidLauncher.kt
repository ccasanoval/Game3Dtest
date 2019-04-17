package com.cesoft.cesdoom

import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

import com.cesoft.cesdoom.util.PlayServices
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.app.AlertDialog
import android.net.Uri
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.games.GamesActivityResultCodes
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


//import com.badlogic.gdx.Gdx
//import com.badlogic.gdx.backends.android.CardBoardAndroidApplication
//import com.badlogic.gdx.backends.android.CardBoardApplicationListener
//import com.badlogic.gdx.backends.android.CardboardCamera
//import com.badlogic.gdx.graphics.GL20
//import com.google.vr.sdk.base.Eye
//import com.google.vr.sdk.base.HeadTransform
//import com.google.vr.sdk.base.Viewport
//import com.badlogic.gdx.graphics.g3d.Environment
//import com.badlogic.gdx.graphics.g3d.Model
//import com.badlogic.gdx.graphics.g3d.ModelBatch
//import com.badlogic.gdx.graphics.g3d.ModelInstance
//import com.badlogic.gdx.math.Matrix4

////////////////////////////////////////////////////////////////////////////////////////////////////
//
// TODO: Player Info to use in game messages: Hey Mr X, now you are dead! Hey Mr X, you Win!!
//
// Para que GPGS te deje pasar:
// Google Play Console -> Gestion de versiones (release management) -> Firma de aplicaciones () -> Certificado de firma de aplicaciones -> SHA-1
// Ahora vas a https://console.developers.google.com/apis/credentials?project=cesdoom -> IDs de cliente de OAuth 2.0
// y cambias el SHA-1 al que cogiste antes
//
// Para pruebas:
// Ve a Google Play Game Services y linka con otra app, auqnue en realidad selecciona la misma, pero con otro nombre (Debug)
// Eso generara otro OAuth 2.0 en console.developers.google.com
// keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
// y añade ese SHA en el nuevo OAuth 2.0 de console.developers.google.com
//
//cesoftw@gmail.com
class AndroidLauncher: AndroidApplication(), PlayServices {

	companion object {
		private val tag: String = AndroidLauncher::class.java.simpleName
		private const val RC_SIGN_IN: Int = 69691
		private const val RC_LEADER_BOARD: Int = 69692
		private const val RC_ACHIEVEMENTS: Int = 69693
	}

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val config = AndroidApplicationConfiguration()
		initialize(CesDoom(BuildConfig.DEBUG, this), config)
		Settings.loadPrefs()
	}

	// Implements PlayServices ---------------------------------------------------------------------

	override fun rateGame() {
		val str = "https://play.google.com/store/apps/details?id=com.cesoft.cesdoom"
		startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(str)))
	}

	override fun isSignedIn(): Boolean {
		return GoogleSignIn.getLastSignedInAccount(this) != null
	}

	override fun signIn() {
		hasTriedToSignIn = false
		startSignInIntent()
	}

	private fun signInSilently() {
		//Log.e(tag, "signInSilently------------------------- isSignedIn="+isSignedIn())
		if(isSignedIn())return
		val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
		signInClient.silentSignIn().addOnCompleteListener(this) { task ->
			if(task.isSuccessful) {
				// The signed in account is stored in the task's result.
				val signedInAccount = task.result
				//Log.e(tag, "signInSilently:isSuccess!!!!!!!!!!!!!!!!------------------------- $signedInAccount")
			}
			else {
				// Player will need to sign-in explicitly using via UI
				try {
					//Log.e(tag, "signInSilently: NOT isSuccess1------------------------- ${task.exception?.printStackTrace()}")
					//Log.e(tag, "signInSilently: NOT isSuccess2------------------------- ${task.exception?.message}")
					//Log.e(tag, "signInSilently: NOT isSuccess3------------------------- ${task.result}")
				} catch(e: Exception) {}
				startSignInIntent()
			}
		}
	}

	private var hasTriedToSignIn = false
	private fun startSignInIntent() {
		if(hasTriedToSignIn)return
		hasTriedToSignIn = true
		val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
		val intent = signInClient.signInIntent
		startActivityForResult(intent, RC_SIGN_IN)
	}
	override fun onResume() {
		super.onResume()
		if(Settings.isGPGSEnabled)
			signInSilently()
	}
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when(requestCode) {
			RC_ACHIEVEMENTS -> {
				//Log.e(tag, "RC_ACHIEVEMENTS---------------------------------------- $resultCode")
				if(resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
					gpgsListener?.onSignedOut()
				}
			}
			RC_LEADER_BOARD -> {
				//Log.e(tag, "RC_LEADER_BOARD---------------------------------------- $resultCode")
				if(resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
					gpgsListener?.onSignedOut()
				}
			}
			RC_SIGN_IN -> {
				val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
				if(result.isSuccess) {
					// The signed in account is stored in the result.
					val signedInAccount = result.signInAccount
					gpgsListener?.onSignedIn()
					//Log.e(tag, "onActivityResult:RC_SIGN_IN:isSuccess-----------($gpgsListener)-------------- $signedInAccount")
				}
				else {
					var message = result.status.statusMessage
					Log.e(tag, "onActivityResult:RC_SIGN_IN: NOT isSuccess---------------- $message --------- $resultCode ")
					if(message == null || message.isEmpty()) {
						message = "Error ?"//getString(R.string.signin_other_error)
					}
					else
						AlertDialog.Builder(this)
							.setMessage(message)
							.setNeutralButton(android.R.string.ok, null)
							.show()
				}

			}
		}
	}
	var gpgsListener: PlayServices.Listener? = null
	override fun addOnSignedIn(listener: PlayServices.Listener) {
		gpgsListener = listener
		//Log.e(tag, "addOnSignedIn:-----------($gpgsListener)--------------")
	}

	override fun showLeaderBoard() {
		val account = GoogleSignIn.getLastSignedInAccount(this)
		//Log.e(tag, "showLeaderBoard:------- ${account?.displayName} / ${account?.email}")
		account?.let {
			val leaderBoard = Games.getLeaderboardsClient(this, account)
			leaderBoard.allLeaderboardsIntent.addOnCompleteListener { result: Task<Intent> ->
				startActivityForResult(result.result, RC_LEADER_BOARD)
			}

		}
	}
	override fun showAchievements() {
		val account = GoogleSignIn.getLastSignedInAccount(this)
		//Log.e(tag, "showLeaderBoard:------- ${account?.displayName} / ${account?.email}")
		account?.let {
			val leaderBoard = Games.getAchievementsClient(this, account)
			leaderBoard.achievementsIntent.addOnCompleteListener { result: Task<Intent> ->
				startActivityForResult(result.result, RC_ACHIEVEMENTS)
			}
		}
	}

	override fun submitScore(highScore: Long) {
		val account = GoogleSignIn.getLastSignedInAccount(this)
		account?.let {
			Games.getLeaderboardsClient(this, account).submitScoreImmediate(resources.getString(R.string.leaderboard_kill_count), highScore)
		}
	}
	override fun unlockAchievement(level: Int) {
		val account = GoogleSignIn.getLastSignedInAccount(this)
		account?.let {
			val achievement = when(level) {
				0 -> resources.getString(R.string.level_0_completed)
				1 -> resources.getString(R.string.level_1_completed)
				2 -> resources.getString(R.string.level_2_completed)
				3 -> resources.getString(R.string.level_3_completed)
				else -> return
			}
			Games.getAchievementsClient(this, account).unlockImmediate(achievement)
		}
	}

	// Implements PlayServices ---------------------------------------------------------------------





	//CardBoardAndroidApplication(), CardBoardApplicationListener {
//TODO: VR
//	private lateinit var game: CesDoom
//	private lateinit var cam: CardboardCamera
//	private val Z_NEAR = 0.1f
//	private val Z_FAR = 1000.0f
//	private val CAMERA_Z = 0.01f

/*
	override fun create() {
		cam = CardboardCamera()
		cam.position.set(0f, 0f, CAMERA_Z)
		cam.lookAt(0f, 0f, 0f)
		cam.near = Z_NEAR
		cam.far = Z_FAR

		game = CesGame(cam)
	}

	override fun onDrawEye(eye: Eye?) {
		if(eye == null)return
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

		// Apply the eye transformation to the camera.
		cam.setEyeViewAdjustMatrix(Matrix4(eye.eyeView))

		val perspective = eye.getPerspective(Z_NEAR, Z_FAR)
		cam.setEyeProjection(Matrix4(perspective))
		cam.update()
	}
	override fun onNewFrame(p0: HeadTransform?) {
	}
	override fun pause() {
	}
	override fun resize(width: Int, height: Int) {
	}
	override fun onFinishFrame(p0: Viewport?) {
	}
	override fun render() {
	}
	override fun resume() {
	}
	override fun dispose() {
	}
	override fun onRendererShutdown() {
	}*/
}
