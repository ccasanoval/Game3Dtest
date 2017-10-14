package com.cesoft.cesgame.UI

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.components.PlayerComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ScoreWidget : Actor() {
	private var score: TextField

	init {
		val assets = Assets()
		score = TextField("", assets.skin)
		assets.dispose()
	}

	override fun act(delta: Float) {
		score.act(delta)
		score.setText("Creditos: " + PlayerComponent.score)
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		score.draw(batch, parentAlpha)
	}

	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		score.setPosition(x, y)
	}

	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		score.setSize(width, height)
	}
}