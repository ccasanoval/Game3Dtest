package com.cesoft.cesdoom.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.PlayerComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ScoreWidget : Actor() {
	private var score: TextField = TextField("", CesDoom.instance.assets.skin)

	init {
		setSize(CesDoom.VIRTUAL_WIDTH/4.5f, CesDoom.VIRTUAL_HEIGHT/23f)
		setPosition(0f, CesDoom.VIRTUAL_HEIGHT - height)
	}

	override fun act(delta: Float) {
		score.act(delta)
		score.text = CesDoom.instance.assets.formatString(Assets.CREDITOS, PlayerComponent.score)
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