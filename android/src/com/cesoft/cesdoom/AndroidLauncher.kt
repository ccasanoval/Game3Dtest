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
//(https://thinkmobiles.com/blog/best-vr-hardware/)
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
class AndroidLauncher: AndroidApplication() {

	companion object {
		private val tag: String = AndroidLauncher::class.java.simpleName
	}

	private val playServices = AndroidPlayServices(this)

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val config = AndroidApplicationConfiguration()
		initialize(CesDoom(BuildConfig.DEBUG, playServices), config)
		Settings.loadPrefs()
	}

	override fun onResume() {
		super.onResume()
		if(Settings.isGPGSEnabled)
			playServices.signInSilently()
	}
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		playServices.onActivityResult(requestCode, resultCode, data)
	}


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
