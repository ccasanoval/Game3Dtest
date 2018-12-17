package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import com.cesoft.cesdoom.components.EnemyComponent
import com.cesoft.cesdoom.util.Log

class Enemy
	: Entity(), Pool.Poolable {

	private var alive = false
		private set
    var position: Vector3 = Vector3()
    var type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1
    var mase: Float = 100f
	var particleEffect: ParticleEffect? = null

	override fun reset() {
		Log.e("Enemy", "reset:------------------------------------------------------")
        alive = false
		position = Vector3()
    	type = EnemyComponent.TYPE.MONSTER1
    	mase = 100f
		removeAll()
		particleEffect?.dispose()
		particleEffect = null
	}

    fun init(position: Vector3,
			type: EnemyComponent.TYPE=EnemyComponent.TYPE.MONSTER1,
			mase: Float = 100f) {
        this.position.set(position)
        this.type = type
        this.mase = mase
        this.alive = true
		this.particleEffect = null
    }
}