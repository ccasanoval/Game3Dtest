package com.cesoft.cesdoom.UI

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.cesoft.cesdoom.Assets
import com.cesoft.cesdoom.components.PlayerComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ScoreWidget(private val assets: Assets) : Actor() {
	private var score: TextField

	init {
		score = TextField("", assets.skin)
	}

	override fun act(delta: Float) {
		score.act(delta)
		score.text = assets.formatString(Assets.CREDITOS, PlayerComponent.score)
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