package com.cesoft.cesdoom.util

////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Google Game Services
//Kill all the hideous monsters to get out of the labyrinth of death. CesDooM is just a proof of concept of a First Person Shooter game for Android, developed with Kotlin under Android Studio by Cesar Casanova
//Mátalos a todos esos monstruos horribles para salir del laberinto de la muerte. CesDooM es solo una prueba de concepto de un juego de acción en primera persona para Android, desarrollado en Kotlin con Android Studio por Cesar Casanova
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

    //fun signOut()
}