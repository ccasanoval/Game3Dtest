package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class CrosshairWidget : Actor() {

	private val cx = CesDoom.VIRTUAL_WIDTH / 32f
	private val crosshairDot: Image= Image(Texture(Gdx.files.internal("crosshair/crossHairPoint.png")))
	private val crosshairInnerRing: Image= Image(Texture(Gdx.files.internal("crosshair/crossHairInnerRing.png")))

	init {
		setSize(cx, cx)
		setPosition((CesDoom.VIRTUAL_WIDTH-cx) / 2, (CesDoom.VIRTUAL_HEIGHT-cx) / 2)
	}

	override fun act(delta: Float) {
		if(Status.paused) return
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		if(Status.paused) return
		crosshairDot.draw(batch!!, parentAlpha)
		crosshairInnerRing.draw(batch, parentAlpha)
	}

	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		crosshairDot.setPosition(x - cx/2, y - cx/2)
		crosshairInnerRing.setPosition(x - cx/2, y - cx/2)
		crosshairInnerRing.setOrigin(crosshairInnerRing.width / 2, crosshairInnerRing.height / 2)
	}

	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		crosshairDot.setSize(width * 2, height * 2)
		crosshairInnerRing.setSize(width * 2, height * 2)
	}

}
