package com.cesoft.cesdoom.ui

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
		touchpadStyle.knob = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("touchpads/touchKnob.png"))))
		touchpadStyle.knob.minWidth = 44f
		touchpadStyle.knob.minHeight = 44f
		touchpadStyle.background = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("touchpads/watch.png"))))
		touchpadStyle.background.minWidth = 44f
		touchpadStyle.background.minHeight = 44f

		val moveStyle = Touchpad.TouchpadStyle()
		moveStyle.knob = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("touchpads/touchKnob.png"))))
		moveStyle.knob.minWidth = 44f
		moveStyle.knob.minHeight = 44f
		moveStyle.background = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("touchpads/move.png"))))
		moveStyle.background.minWidth = 44f
		moveStyle.background.minHeight = 44f

		val fireStyle = Touchpad.TouchpadStyle()
		fireStyle.background = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("touchpads/fire.png"))))
		fireStyle.background.minWidth = 44f
		fireStyle.background.minHeight = 44f

		movementPad.style = moveStyle
		watchPad.style = touchpadStyle
		firePad.style = fireStyle
		val alfa = 0.5f
		val color = 1f
		movementPad.setColor(color, color, color, alfa)
		watchPad.setColor(color, color, color, alfa)
		firePad.setColor(color, color, color, alfa)
	}

	fun addToStage(stage: Stage) {
		movementPad.setBounds(15f, 15f, 350f, 350f)
		watchPad.setBounds(stage.width - 315, 15f, 350f, 350f)
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