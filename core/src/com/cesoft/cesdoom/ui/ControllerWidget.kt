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
		firePad2.style = fireStyle
		val alpha = 0.55f
		movementPad.setColor(1f, 1f, 1f, alpha)
		watchPad.setColor(1f, 1f, 1f, alpha)
		firePad.setColor(1f, .7f, .7f, alpha)
		firePad2.setColor(1f, .7f, .7f, alpha)
	}

	fun addToStage(stage: Stage) {
		val margin = 25f
		val cx = 350f
		val cx2 = 180f

		movementPad.setBounds(margin, 0f, cx, cx)

		val watchPadX = stage.width - cx - margin
		val watchPadY = 0f
		watchPad.setBounds(watchPadX, watchPadY, cx, cx)

		val firePadX = stage.width - cx2
		val firePadY = 1.75f*cx2
		firePad.setBounds(firePadX, firePadY, cx2, cx2)

		val firePadX2 = 0f
		val firePadY2 = 1.75f*cx2
		firePad2.setBounds(firePadX2, firePadY2, cx2, cx2)

		stage.addActor(movementPad)
		stage.addActor(watchPad)
		stage.addActor(firePad)
		stage.addActor(firePad2)
	}

	companion object {
		private var movementPad: Touchpad = Touchpad(15f, Touchpad.TouchpadStyle())
		private var watchPad: Touchpad = Touchpad(5f, Touchpad.TouchpadStyle())
		private var firePad: Touchpad = Touchpad(0f, Touchpad.TouchpadStyle())
		private var firePad2: Touchpad = Touchpad(0f, Touchpad.TouchpadStyle())
		var movementVector: Vector2 = Vector2(0f, 0f)
			get() = Vector2(movementPad.knobPercentX, movementPad.knobPercentY)
			private set
		var watchVector: Vector2 = Vector2(0f, 0f)
			get() = Vector2(watchPad.knobPercentX, watchPad.knobPercentY)
			private set
		var isFiring: Boolean = false
			get() = firePad.isTouched || firePad2.isTouched
			private set
	}
}