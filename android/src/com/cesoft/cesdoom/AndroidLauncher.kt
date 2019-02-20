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
class AndroidLauncher: AndroidApplication(), PlayServices {

	companion object {
		private val tag: String = AndroidLauncher::class.java.simpleName
		private const val RC_SIGN_IN: Int = 69691
	}

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val config = AndroidApplicationConfiguration()
		initialize(CesDoom(BuildConfig.DEBUG, this), config)
	}


	// Implements PlayServices ---------------------------------------------------------------------
//TODO: Player Info to use in game messages: Hey Mr X, now you are dead! Hey Mr X, you Win!!
//https://developers.google.com/games/services/android/signin#retrieving_player_information

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
		Log.e(tag, "signInSilently------------------------- isSignedIn="+isSignedIn())
		if(isSignedIn())return
		val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
		signInClient.silentSignIn().addOnCompleteListener(this) { task ->
			if (task.isSuccessful) {
				// The signed in account is stored in the task's result.
				val signedInAccount = task.result
				Log.e(tag, "signInSilently:isSuccess!!!!!!!!!!!!!!!!------------------------- $signedInAccount")
			} else {
				// Player will need to sign-in explicitly using via UI
				try {
					Log.e(tag, "signInSilently: NOT isSuccess1------------------------- ${task.exception?.printStackTrace()}")
					Log.e(tag, "signInSilently: NOT isSuccess2------------------------- ${task.exception?.message}")
					//Log.e(tag, "signInSilently: NOT isSuccess3------------------------- ${task.result}")
				}catch (e: Exception) {}
				startSignInIntent()
			}
		}
	}
	private var hasTriedToSignIn = false
	private fun startSignInIntent() {
		Log.e(tag, "startSignInIntent 0-------------------------")
		if(hasTriedToSignIn)return
		hasTriedToSignIn = true
		Log.e(tag, "startSignInIntent 1-------------------------")
		val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
		val intent = signInClient.signInIntent
		startActivityForResult(intent, RC_SIGN_IN)
		Log.e(tag, "startSignInIntent 9-------------------------")
	}
	override fun onResume() {
		super.onResume()
		signInSilently()
		Log.e(tag, "onResume-------------------------")
	}
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when(requestCode) {
			RC_SIGN_IN -> {
				val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
				if(result.isSuccess) {
					// The signed in account is stored in the result.
					val signedInAccount = result.signInAccount
					Log.e(tag, "onActivityResult:RC_SIGN_IN:isSuccess------------------------- $signedInAccount")
				}
				else {
					Log.e(tag, "onActivityResult:RC_SIGN_IN: NOT isSuccess------------------------- $resultCode ")
					var message = result.status.statusMessage
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

	override fun showLeaderBoard() {
		//val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
		val account = GoogleSignIn.getLastSignedInAccount(this)
		account?.let {
			Games.getLeaderboardsClient(this, account)
		}
	}

	override fun submitScore(highScore: Long) {
		val account = GoogleSignIn.getLastSignedInAccount(this)
		account?.let {
			Games.getLeaderboardsClient(this, account)
					.submitScore(resources.getString(R.string.leaderboard_kill_count), highScore)
		}
	}




/*


	private fun iniPlayServices() {
		Log.e(tag, "iniPlayServices-----------------------------------------------------0")
		gameHelper = GameHelper(this)
		Log.e(tag, "iniPlayServices-----------------------------------------------------1")
		gameHelper!!.enableDebugLog(false)
		Log.e(tag, "iniPlayServices-----------------------------------------------------2")

		val gameHelperListener = object : GameHelper.GameHelperListener {
			override fun onSignInFailed() {
				Log.e(tag, "iniPlayServices:onSignInFailed-----------------------------------------------------")
			}
			override fun onSignInSucceeded() {
				Log.e(tag, "iniPlayServices:onSignInSucceeded-----------------------------------------------------")
			}
		}
		Log.e(tag, "iniPlayServices-----------------------------------------------------3")
		gameHelper!!.setup(gameHelperListener)
		Log.e(tag, "iniPlayServices-----------------------------------------------------4")
	}

	private val requestCode = 1
	private var gameHelper: GameHelper? = null

	override fun onStart() {
		super.onStart()
		gameHelper?.onStart(this)
	}

	override fun onStop() {
		super.onStop()
		gameHelper?.onStop()
	}



	override fun isSignedIn(): Boolean {
		gameHelper?.let {
			return it.isSignedIn
		}?: run {
			return false
		}
	}

	override fun signIn() {
		try {
			runOnUiThread { gameHelper?.beginUserInitiatedSignIn() }
		} catch (e: Exception) { Log.e(tag, "signIn:e:",e) }
	}

	override fun signOut() {
		try {
			runOnUiThread { gameHelper?.signOut() }
		} catch (e: Exception) { Log.e(tag, "signOut:e:",e) }
	}


	override fun unlockAchievement(str: String) {
		Games.Achievements.unlock(gameHelper?.apiClient, str)
	}

	override fun submitScore(highScore: Int) {
		if (isSignedIn()) {
			Games.Leaderboards.submitScore(gameHelper?.apiClient, "CgkIkdTIyacFEAIQAQ", highScore.toLong())
		}
	}

	override fun submitLevel(highLevel: Int) {
		if (isSignedIn()) {
			Games.Leaderboards.submitScore(gameHelper?.apiClient, "CgkIkdTIyacFEAIQAg", highLevel.toLong())
		}
	}

	override fun showAchievement() {
		if (isSignedIn()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper?.apiClient), requestCode)
		} else {
			signIn()
		}
	}

	override fun showScore() {
		if (isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper?.apiClient, "CgkIkdTIyacFEAIQAQ"), requestCode)
		} else {
			signIn()
		}
	}

	override fun showLevel() {
		if (isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper?.apiClient, "CgkIkdTIyacFEAIQAg"), requestCode)
		} else {
			signIn()
		}
	}*/



/*
	override fun showBannerAd() {
		runOnUiThread {
			bannerAd.setVisibility(View.VISIBLE)
			val builder = AdRequest.Builder().addTestDevice("BAA254E1D59E02763BB1A917CF86CDDE")
			val ad = builder.build()
			bannerAd.loadAd(ad)
		}
	}
	override fun hideBannerAd() {
		//runOnUiThread { bannerAd.setVisibility(View.INVISIBLE) }
	}
	override fun isRewardEarned(): Boolean {
		val temp = rewarded
		rewarded = false
		return temp
	}
	override fun showRewardedVideo() {
		runOnUiThread {
			if (rewardedVideoAd.isLoaded()) {
				rewardedVideoAd.show()
			}
			loadRewardedVideoAd()
		}
	}
	override fun showInterstitialAd(then: Runnable) {
		runOnUiThread {
			if (then != null) {
				interstitialAd.setAdListener(object : AdListener() {
					fun onAdClosed() {
						Gdx.app.postRunnable(then)
						requestNewInterstitial()
					}
				})
			}
			if (interstitialAd.isLoaded()) {
				interstitialAd.show()
			} else {
				requestNewInterstitial()
			}
		}
	}*/


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
