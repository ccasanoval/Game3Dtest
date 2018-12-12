package com.cesoft.cesdoom

import android.os.Bundle
import com.badlogic.gdx.Gdx

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
//import com.badlogic.gdx.backends.android.CardBoardAndroidApplication
//import com.badlogic.gdx.backends.android.CardBoardApplicationListener
//import com.badlogic.gdx.backends.android.CardboardCamera
import com.badlogic.gdx.graphics.GL20
import com.google.vr.sdk.base.Eye
import com.google.vr.sdk.base.HeadTransform
import com.google.vr.sdk.base.Viewport
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4


//AndroidApplication(),
class AndroidLauncher: AndroidApplication() {//CardBoardAndroidApplication(), CardBoardApplicationListener {

	private lateinit var game: CesDoom
	//private lateinit var cam: CardboardCamera
	private val Z_NEAR = 0.1f
	private val Z_FAR = 1000.0f
	private val CAMERA_Z = 0.01f
	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val config = AndroidApplicationConfiguration()
		//initialize(this, config)//CesGame(), config)
		initialize(CesDoom(), config)
	}
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
