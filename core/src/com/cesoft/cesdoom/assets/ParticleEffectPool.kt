package com.cesoft.cesdoom.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ParticleEffectPool(private val assetManager: AssetManager)
	: Pool<ParticleEffect>() {

	companion object {
	    val tag: String = ParticleEffectPool::class.java.simpleName
		private const val PARTICLES_ENEMY = "particles/dieparticle.pfx"
	}

	val particleSystem = ParticleSystem()
	private val billboardParticleBatch = BillboardParticleBatch()
	private val loadParam: ParticleEffectLoader.ParticleEffectLoadParameter

	init {
		particleSystem.add(billboardParticleBatch)
		loadParam = ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.batches)
		assetManager.load(PARTICLES_ENEMY, ParticleEffect::class.java, loadParam)
		assetManager.finishLoadingAsset(PARTICLES_ENEMY)
	}
	fun setCamera(camera: PerspectiveCamera) {
		billboardParticleBatch.setCamera(camera)
	}

	private val allEffects = Array<ParticleEffect>()
	override fun newObject(): ParticleEffect {
		val obj = assetManager.get(PARTICLES_ENEMY, ParticleEffect::class.java).copy()
		allEffects.add(obj)
		return obj
	}

	fun dispose() {
		val effects = Array.ArrayIterator(allEffects)
		for(effect in effects) {
			try {
				effect?.let {
					it.end()
					it.dispose()
				}
			}
			catch(e: Exception) {
				Log.e(tag, "dispose:e:$effect:$e")}
		}
		freeAll(allEffects)
		allEffects.clear()
	}
}
