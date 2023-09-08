package com.cesoft.cesdoom

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.cesoft.cesdoom.util.PlayServices
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games
import com.google.android.gms.games.GamesActivityResultCodes
import com.google.android.gms.tasks.Task

class TvActivity : AndroidApplication(), PlayServices {

    companion object {
        private val tag: String = TvActivity::class.java.simpleName
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
        if(isSignedIn())return
        val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        signInClient.silentSignIn().addOnCompleteListener(this) { task ->
            if(task.isSuccessful) {
                // The signed in account is stored in the task's result.
                val signedInAccount = task.result
            }
            else {
                // Player will need to sign-in explicitly using via UI
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
                if(resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                    gpgsListener?.onSignedOut()
                }
            }
            RC_LEADER_BOARD -> {
                if(resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                    gpgsListener?.onSignedOut()
                }
            }
            RC_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
                if(result?.isSuccess == true) {
                    // The signed in account is stored in the result.
                    val signedInAccount = result.signInAccount
                    gpgsListener?.onSignedIn()
                }
                else {
                    var message = result?.status?.statusMessage
                    Log.e(tag, "onActivityResult:RC_SIGN_IN: NOT isSuccess---------------- $message --------- $resultCode ")
                    if(message == null || message.isEmpty()) {
                        message = "Error ?"//getString(R.string.signin_other_error)
                    }
                    AlertDialog.Builder(this)
                            .setMessage(message)
                            .setNeutralButton(android.R.string.ok, null)
                            .show()
                }

            }
        }
    }
    private var gpgsListener: PlayServices.Listener? = null
    override fun addOnSignedIn(listener: PlayServices.Listener) {
        gpgsListener = listener
    }

    override fun showLeaderBoard() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let {
            val leaderBoard = Games.getLeaderboardsClient(this, account)
            leaderBoard.allLeaderboardsIntent.addOnCompleteListener { result: Task<Intent> ->
                startActivityForResult(result.result, RC_LEADER_BOARD)
            }
        }
    }
    override fun showAchievements() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
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
}