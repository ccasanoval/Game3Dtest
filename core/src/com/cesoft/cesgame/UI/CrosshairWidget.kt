package com.cesoft.cesgame.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.cesoft.cesgame.Settings

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class CrosshairWidget : Actor() {

	private val crosshairDot: Image= Image(Texture(Gdx.files.internal("crosshair/crossHairPoint.png")))
	private val crosshairInnerRing: Image= Image(Texture(Gdx.files.internal("crosshair/crossHairInnerRing.png")))

	override fun act(delta: Float) {
		if(Settings.paused) return
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		if(Settings.paused) return
		crosshairDot.draw(batch!!, parentAlpha)
		crosshairInnerRing.draw(batch, parentAlpha)
	}

	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		crosshairDot.setPosition(x - 16, y - 16)
		crosshairInnerRing.setPosition(x - 16, y - 16)
		crosshairInnerRing.setOrigin(crosshairInnerRing.width / 2, crosshairInnerRing.height / 2)
	}

	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		crosshairDot.setSize(width * 2, height * 2)
		crosshairInnerRing.setSize(width * 2, height * 2)
	}

}
