package com.cesoft.cesdoom.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.util.Log


/////////////////////////////////////////////////////////////////////////////////////
class GunFireWidget(private val fire: Image, x: Float, y: Float) : Actor() {

	init {
		stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
		stage.addActor(this)
		setSize(100f, 100f)
		setPosition(x, y)//+30f, -60f)
	}

	fun dispose() {
		Log.e("GunFireWidget", "dispose----------------------------------------------")
		//stage.dispose()
	}

	fun draw() {
		stage.draw()
	}

	override fun act(delta: Float) {
		if(Status.paused) return
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		if(Status.paused) return
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
