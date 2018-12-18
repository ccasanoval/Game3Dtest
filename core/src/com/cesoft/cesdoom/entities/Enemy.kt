package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import com.cesoft.cesdoom.assets.ParticleEffectPool
import com.cesoft.cesdoom.components.EnemyComponent
import com.cesoft.cesdoom.util.Log

class Enemy
	: Entity(), Pool.Poolable {

	private var alive = false
		private set
    var position: Vector3 = Vector3()
    var type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1
    var mase: Float = 100f
	lateinit var particleEffectPool: ParticleEffectPool
	lateinit var particleEffect: ParticleEffect

	override fun reset() {
		Log.e("Enemy", "reset:------------------------------------------------------particleEffect="+particleEffect)
        alive = false
		position = Vector3()
    	type = EnemyComponent.TYPE.MONSTER1
    	mase = 100f
		particleEffectPool.free(particleEffect)
		removeAll()
	}

    fun init(position: Vector3,
			type: EnemyComponent.TYPE=EnemyComponent.TYPE.MONSTER1,
			mase: Float = 100f,
			particleEffectPool: ParticleEffectPool) {
        this.position.set(position)
        this.type = type
        this.mase = mase
        this.alive = true
		this.particleEffectPool = particleEffectPool
		this.particleEffect = particleEffectPool.obtain()
		Log.e("Enemy", "INI-------------------------"+this.particleEffect)
    }
}