package com.cesoft.cesgame.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ControllerWidget {
	init {
		val touchpadStyle = Touchpad.TouchpadStyle()
		touchpadStyle.knob = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("data/touchKnob.png"))))
		touchpadStyle.knob.minWidth = 44f
		touchpadStyle.knob.minHeight = 44f
		touchpadStyle.background = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("data/touchBackground.png"))))
		touchpadStyle.background.minWidth = 44f
		touchpadStyle.background.minHeight = 44f

		movementPad.style = touchpadStyle
		watchPad.style = touchpadStyle
		firePad.style = touchpadStyle //Touchpad.TouchpadStyle()
		movementPad.setColor(0.25f, 0.25f, 0.25f, 0.25f)
		watchPad.setColor(0.25f, 0.25f, 0.25f, 0.25f)
		firePad.setColor(0.25f, 0.25f, 0.25f, 0.25f)
	}

	fun addToStage(stage: Stage) {
		movementPad.setBounds(15f, 15f, 300f, 300f)
		watchPad.setBounds(stage.width - 315, 15f, 300f, 300f)
		firePad.setBounds(15f, stage.height-215, 200f, 200f)
		stage.addActor(movementPad)
		stage.addActor(watchPad)
		stage.addActor(firePad)
	}

	companion object {
		private var movementPad: Touchpad = Touchpad(5f, Touchpad.TouchpadStyle())
		private var watchPad: Touchpad = Touchpad(5f, Touchpad.TouchpadStyle())
		private var firePad: Touchpad = Touchpad(.1f, Touchpad.TouchpadStyle())
		var movementVector: Vector2 = Vector2(0f, 0f)
			get() = Vector2(movementPad.knobPercentX, movementPad.knobPercentY)
			private set
		var watchVector: Vector2 = Vector2(0f, 0f)
			get() = Vector2(watchPad.knobPercentX, watchPad.knobPercentY)
			private set
		var isFiring: Boolean = false
			get() = firePad.isTouched
			private set
	}
}