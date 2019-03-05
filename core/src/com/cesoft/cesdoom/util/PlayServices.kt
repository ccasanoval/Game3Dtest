package com.cesoft.cesdoom.util

interface PlayServices {
    fun rateGame()

    fun isSignedIn(): Boolean
    fun signIn()
    fun showLeaderBoard()
    fun submitScore(highScore: Long)
    fun showAchievements()
    fun unlockAchievement(level: Int)

    interface Listener {
        fun onSignedIn()
        fun onSignedOut()
    }
    fun addOnSignedIn(listener: Listener)

/*
    fun signOut()
    fun unlockAchievement(str: String)
    fun submitScore(highScore: Int)
    fun submitLevel(highLevel: Int)
    fun showAchievement()
    fun showScore()
    fun showLevel()*/
}