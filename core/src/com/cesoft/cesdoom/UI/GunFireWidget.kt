package com.cesoft.cesdoom.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings


/////////////////////////////////////////////////////////////////////////////////////
object GunFireWidget : Actor() {
	private val fire: Image = Image(Texture(Gdx.files.internal("weapons/fire.png")))

	//gfw.setPosition(CesDoom.VIRTUAL_WIDTH / 2 +30, CesDoom.VIRTUAL_HEIGHT / 2 -70)
	//gfw.setSize(100f, 100f)
	init {
		stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
		stage.addActor(this)
		setSize(100f, 100f)
		setPosition(+30f, -60f)
	}

	fun draw()
	{
		stage.draw()
	}

	override fun act(delta: Float) {
		if(Settings.paused) return
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		if(Settings.paused) return
		fire.draw(batch!!, parentAlpha)
	}

	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		fire.setPosition(CesDoom.VIRTUAL_WIDTH/2f + x,  CesDoom.VIRTUAL_HEIGHT/2f + y)
	}

	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		fire.setSize(width, height)
	}
}
//______________________________________________________________________________________________
/*private fun drawGunFire() {
	//TODO: Compiar del cross widget por ejemplo!!!!!!!!!!!!
	//shapeRenderer.projectionMatrix = gunCamera.combined
	spriteBatch.projectionMatrix = gunCamera.combined
	spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
	spriteBatch.begin()
	val posGun = Vector3()
	val posGun2D = gunCamera.project(posGun)
	spriteBatch.draw(
			gunFire,
			CesDoom.VIRTUAL_WIDTH/2f, CesDoom.VIRTUAL_WIDTH/2f,
			//posGun2D.x-100, posGun2D.y-100,
			gunFire.regionWidth.toFloat(), gunFire.regionHeight.toFloat())
	spriteBatch.end()
}*/