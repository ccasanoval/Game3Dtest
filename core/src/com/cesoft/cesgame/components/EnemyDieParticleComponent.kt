package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem

////////////////////////////////////////////////////////////////////////////////////////////////////
class EnemyDieParticleComponent(particleSystem: ParticleSystem, assetManager: AssetManager) : Component {
	var originalEffect: ParticleEffect
	var used = false

	init {
		val loadParam = ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.batches)

		if( ! assetManager.isLoaded("data/dieparticle.pfx")) {
			assetManager.load("data/dieparticle.pfx", ParticleEffect::class.java, loadParam)
			assetManager.finishLoading()
		}
		originalEffect = assetManager.get("data/dieparticle.pfx")
	}
}
