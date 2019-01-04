package com.cesoft.cesdoom.assets

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch
import com.badlogic.gdx.utils.Pool
import com.cesoft.cesdoom.util.Log

class ParticleEffectPool(private val assets: Assets, camera: PerspectiveCamera)
	: Pool<ParticleEffect>() {

	val particleSystem = ParticleSystem()
	val loadParam: ParticleEffectLoader.ParticleEffectLoadParameter

	init {
		//Log.e("ParticleEffectPool", "INI ---------------------------------------------------------")
		val billboardParticleBatch = BillboardParticleBatch()
		billboardParticleBatch.setCamera(camera)
		particleSystem.add(billboardParticleBatch)
		loadParam = ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.batches)
	}

	override fun newObject(): ParticleEffect {
		Log.e("ParticleEffectPool", "newObject ---------------------------------------------------------")
		return assets.getParticleEffectDie().copy()
	}

	override fun free(effect: ParticleEffect) {
		try {
			Log.e("ParticleEffectPool", "free ---------------------------------------------------------$effect")
			effect.end()
			effect.reset()
		}
		catch(e: Exception) {
			Log.e("ParticleEffectPool", "free $effect : 	º$e")
		}
		super.free(effect)
	}
}
