package com.cesoft.cesdoom.util

interface PlayServices {
    fun rateGame()

    fun isSignedIn(): Boolean
    fun signIn()
    fun showLeaderBoard()
    fun submitScore(highScore: Long)

/*
    fun signOut()
    fun unlockAchievement(str: String)
    fun submitScore(highScore: Int)
    fun submitLevel(highLevel: Int)
    fun showAchievement()
    fun showScore()
    fun showLevel()*/
}