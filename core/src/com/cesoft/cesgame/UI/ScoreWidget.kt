package com.cesoft.cesgame.UI

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.components.PlayerComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ScoreWidget : Actor() {
	internal var label: Label

	init {
		val assets = Assets()
		label = Label("", assets.skin)
	}

	override fun act(delta: Float) {
		label.act(delta)
		label.setText("Score : " + PlayerComponent.score)
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		label.draw(batch, parentAlpha)
	}

	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		label.setPosition(x, y)
	}

	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		label.setSize(width, height)
	}
}