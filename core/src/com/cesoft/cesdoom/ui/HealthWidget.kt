package com.cesoft.cesdoom.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.utils.Align
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.PlayerComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class HealthWidget(assets: Assets) : Actor() {
	private val healthBar: ProgressBar
	private val progressBarStyle: ProgressBar.ProgressBarStyle = ProgressBar.ProgressBarStyle(
			assets.skin.newDrawable("progress-bar-back", Color.RED),
			assets.skin.newDrawable("white", Color.GREEN))
	private val label: Label

	//______________________________________________________________________________________________
	init {
		progressBarStyle.knobBefore = progressBarStyle.knob
		healthBar = ProgressBar(0f, 100f, 1f, false, progressBarStyle)

		val ls = Label.LabelStyle()
		ls.font = BitmapFont()
		ls.fontColor = Color.YELLOW
		label = Label("", ls)
		label.setAlignment(Align.center)

		setSize(CesDoom.VIRTUAL_WIDTH/4.5f, CesDoom.VIRTUAL_HEIGHT/9.5f)
		setPosition(CesDoom.VIRTUAL_WIDTH/2 - width/2, 0f)
	}

	//______________________________________________________________________________________________
	override fun act(delta: Float) {
		if(Status.paused) return
		updateValue()
		healthBar.act(delta)
		label.act(delta)
	}

	//______________________________________________________________________________________________
	override fun draw(batch: Batch?, parentAlpha: Float) {
		healthBar.draw(batch!!, parentAlpha)
		label.draw(batch, parentAlpha)
	}

	//______________________________________________________________________________________________
	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		healthBar.setPosition(x, y)
		label.setPosition(x, y)
	}

	//______________________________________________________________________________________________
	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		healthBar.setSize(width, height)
		progressBarStyle.background.minWidth = width+1
		progressBarStyle.background.minHeight = height+1
		//progressBarStyle.knob.minWidth = 0f//healthBar.value
		progressBarStyle.knob.minHeight = height-23f
		label.setSize(width, height)
	}

	//______________________________________________________________________________________________
	private fun updateValue() {
		val value = PlayerComponent.health
		healthBar.value = value-3f
		label.setText("$value %")
	}
}