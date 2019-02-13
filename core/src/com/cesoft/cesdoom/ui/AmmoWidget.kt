package com.cesoft.cesdoom.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.PlayerComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class AmmoWidget(private val assets: Assets) : Actor() {
	companion object {
		const val cx = CesDoom.VIRTUAL_WIDTH/5f
		const val cy = CesDoom.VIRTUAL_HEIGHT/23f
	}

	private var text: TextField = TextField("", assets.skin)

	init {
		setSize(cx, cy)
		setPosition(ScoreWidget.cx+10, CesDoom.VIRTUAL_HEIGHT - height)
	}

	override fun act(delta: Float) {
		text.act(delta)
		text.text = assets.formatString(Assets.AMMO, PlayerComponent.ammo)
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		text.draw(batch, parentAlpha)
	}

	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		text.setPosition(x, y)
	}

	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		text.setSize(width, height)
	}
}