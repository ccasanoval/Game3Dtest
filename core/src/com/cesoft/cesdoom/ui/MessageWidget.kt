package com.cesoft.cesdoom.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MessageWidget : Actor() {
	private var message: TextField = TextField("", CesDoom.instance.assets.skin)

	init {
		message.setAlignment(Align.center)
		//message.setColor(.9f, .1f, .1f, 1f)
	}

	override fun act(delta: Float) {
		message.act(delta)
		if(PlayerComponent.message.isEmpty()) {
			message.text = ""
			message.isVisible = false
		}
		else {
			message.isVisible = true
			message.text = PlayerComponent.message
		}
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		message.draw(batch, parentAlpha)
	}

	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		message.setPosition(x, y)
	}

	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		message.setSize(width, height)
	}
}