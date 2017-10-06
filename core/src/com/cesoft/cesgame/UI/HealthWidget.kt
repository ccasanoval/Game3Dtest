package com.cesoft.cesgame.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.utils.Align
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.Settings

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class HealthWidget : Actor() {
	private val healthBar: ProgressBar
	private val progressBarStyle: ProgressBar.ProgressBarStyle
	private val label: Label

	init {
		val assets = Assets()
		progressBarStyle = ProgressBar.ProgressBarStyle(
				assets.skin.newDrawable("white", Color.RED),
				assets.skin.newDrawable("white", Color.GREEN))
		progressBarStyle.knobBefore = progressBarStyle.knob
		healthBar = ProgressBar(0f, 100f, 1f, false, progressBarStyle)
		label = Label("Energía", assets.skin)
		label.setAlignment(Align.center)
	}

	override fun act(delta: Float) {
		if(Settings.paused) return
		healthBar.act(delta)
		label.act(delta)
	}

	override fun draw(batch: Batch?, parentAlpha: Float) {
		healthBar.draw(batch!!, parentAlpha)
		label.draw(batch, parentAlpha)
	}

	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		healthBar.setPosition(x, y)
		label.setPosition(x, y)
	}

	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		healthBar.setSize(width, height)
		progressBarStyle.background.minWidth = width
		progressBarStyle.background.minHeight = height
		progressBarStyle.knob.minWidth = healthBar.value
		progressBarStyle.knob.minHeight = height
		label.setSize(width, height)
	}

	fun setValue(value: Float) {
		healthBar.value = value
	}
}