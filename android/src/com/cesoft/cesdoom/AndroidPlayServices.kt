package com.cesoft.cesdoom

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.badlogic.gdx.backends.android.AndroidApplication
import com.cesoft.cesdoom.util.PlayServices
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games
import com.google.android.gms.games.GamesActivityResultCodes
import com.google.android.gms.tasks.Task

class AndroidPlayServices(private val context: AndroidApplication) : PlayServices {

    companion object {
        private val tag: String = AndroidLauncher::class.java.simpleName
        const val RC_SIGN_IN: Int = 69691
        const val RC_LEADER_BOARD: Int = 69692
        const val RC_ACHIEVEMENTS: Int = 69693
    }

    override fun rateGame() {
        val str = "https://play.google.com/store/apps/details?id=com.cesoft.cesdoom"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(str)))
    }

    override fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(context) != null
    }

    override fun signIn() {
        hasTriedToSignIn = false
        startSignInIntent()
    }

    fun signInSilently() {
        //Log.e(tag, "signInSilently------------------------- isSignedIn="+isSignedIn())
        if(isSignedIn())return
        val signInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        signInClient.silentSignIn().addOnCompleteListener(context) { task ->
            if(task.isSuccessful) {
                // The signed in account is stored in the task's result.
                val signedInAccount = task.result
                //Log.e(tag, "signInSilently:isSuccess!!!!!!!!!!!!!!!!------------------------- $signedInAccount")
            }
            else {
                // Player will need to sign-in explicitly using via UI
//                try {
//                    Log.e(tag, "signInSilently: NOT isSuccess1------------------------- ${task.exception?.printStackTrace()}")
//                    Log.e(tag, "signInSilently: NOT isSuccess2------------------------- ${task.exception?.message}")
//                    Log.e(tag, "signInSilently: NOT isSuccess3------------------------- ${task.result}")
//                } catch(e: Exception) {}
                startSignInIntent()
            }
        }
    }

    private var hasTriedToSignIn = false
    private fun startSignInIntent() {
        if(hasTriedToSignIn)return
        hasTriedToSignIn = true
        val signInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        val intent = signInClient.signInIntent
        context.startActivityForResult(intent, RC_SIGN_IN)
    }

    private var gpgsListener: PlayServices.Listener? = null
    override fun addOnSignedIn(listener: PlayServices.Listener) {
        gpgsListener = listener
        //Log.e(tag, "addOnSignedIn:-----------($gpgsListener)--------------")
    }

    override fun showLeaderBoard() {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        //Log.e(tag, "showLeaderBoard:------- ${account?.displayName} / ${account?.email}")
        account?.let {
            val leaderBoard = Games.getLeaderboardsClient(context, account)
            leaderBoard.allLeaderboardsIntent.addOnCompleteListener { result: Task<Intent> ->
                context.startActivityForResult(result.result, RC_LEADER_BOARD)
            }

        }
    }
    override fun showAchievements() {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        //Log.e(tag, "showLeaderBoard:------- ${account?.displayName} / ${account?.email}")
        account?.let {
            val leaderBoard = Games.getAchievementsClient(context, account)
            leaderBoard.achievementsIntent.addOnCompleteListener { result: Task<Intent> ->
                context.startActivityForResult(result.result, RC_ACHIEVEMENTS)
            }
        }
    }

    override fun submitScore(highScore: Long) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        val txt = context.resources.getString(R.string.leaderboard_kill_count)
        account?.let {
            Games.getLeaderboardsClient(context, account)
                    .submitScoreImmediate(txt, highScore)
        }
    }
    override fun unlockAchievement(level: Int) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        account?.let {
            val achievement = when(level) {
                0 -> context.resources.getString(R.string.level_0_completed)
                1 -> context.resources.getString(R.string.level_1_completed)
                2 -> context.resources.getString(R.string.level_2_completed)
                3 -> context.resources.getString(R.string.level_3_completed)
                4 -> context.resources.getString(R.string.level_4_completed)
                else -> return
            }
            Games.getAchievementsClient(context, account).unlockImmediate(achievement)
        }
    }


    //----------------------
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
                if(result?.isSuccess == true) {
                    // The signed in account is stored in the result.
                    val signedInAccount = result.signInAccount
                    gpgsListener?.onSignedIn()
                    //Log.e(tag, "onActivityResult:RC_SIGN_IN:isSuccess-----------($gpgsListener)-------------- $signedInAccount")
                }
                else {
                    var message = result?.status?.statusMessage
                    Log.e(tag, "onActivityResult:RC_SIGN_IN: NOT isSuccess---------------- $message --------- $resultCode ")
                    if(message == null || message.isEmpty()) {
                        message = "Error ?"//getString(R.string.signin_other_error)
                    }
                    else
                        AlertDialog.Builder(context)
                                .setMessage(message)
                                .setNeutralButton(android.R.string.ok, null)
                                .show()
                }
            }
        }
    }
}