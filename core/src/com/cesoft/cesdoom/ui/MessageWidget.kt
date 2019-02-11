package com.cesoft.cesdoom.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.components.PlayerComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MessageWidget : Actor() {
	private var message: TextField = TextField("", CesDoom.instance.assets.skin)

	init {
		message.setAlignment(Align.center)
		setSize(CesDoom.VIRTUAL_WIDTH/2f, CesDoom.VIRTUAL_HEIGHT/23f)
		setPosition(-100f,-100f)
	}

	override fun act(delta: Float) {
		message.act(delta)
		if(PlayerComponent.message.isEmpty() && !message.text.isEmpty()) {
			/*if(PlayerComponent.isGodModeOn) {
				message.text = " * GOD MODE * "
			}
			else {*/
			message.text = ""
			setPosition(-100f, -100f)
			//}
		}
		else if(PlayerComponent.message != message.text) {
			message.text = PlayerComponent.message
			setPosition(CesDoom.VIRTUAL_WIDTH - width, CesDoom.VIRTUAL_HEIGHT - height)
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