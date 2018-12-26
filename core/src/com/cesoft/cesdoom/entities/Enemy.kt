package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.assets.ParticleEffectPool
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.components.EnemyComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.components.StatusComponent
import com.cesoft.cesdoom.managers.EnemyFactory
import com.cesoft.cesdoom.util.Log

class Enemy(val id: Int) : Entity() {

	private var alive = false
    var position: Vector3 = Vector3()
    var type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1
    var mase: Float = 100f
	private var particleEffectPool: ParticleEffectPool? = null
	var particleEffect: ParticleEffect? = null
	var rigidBody: btRigidBody? = null
	var rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo? = null

	fun reset() {
		Log.e("Enemy", "reset:------------------------------------------------------particleEffect=$particleEffect")
        alive = false
		position = Vector3(0f, 150f, -250f)
    	type = EnemyComponent.TYPE.MONSTER1
    	mase = 100f

		/// Animation
		EnemyFactory.playWalking(this)
		//getComponent(AnimationComponent::class.java)
		//animat?

		/// Status
		getComponent(StatusComponent::class.java).reset()
		//status.setWalking()

		/// Model
		val model = getComponent(ModelComponent::class.java)
		if(model.blendingAttribute != null)
			model.blendingAttribute!!.opacity = 1f
		val rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(0.0).toFloat())
		model.instance.transform.set(position, rot)

		/// Collision
		val bullet = getComponent(BulletComponent::class.java)
		bullet.rigidBody.linearVelocity = Vector3.Zero
		val transf = Matrix4()
		transf.setTranslation(position)
		bullet.rigidBody.worldTransform = transf
	}

    fun init(position: Vector3,
			type: EnemyComponent.TYPE=EnemyComponent.TYPE.MONSTER1,
			mase: Float = 100f,
			particleEffectPool: ParticleEffectPool,
			rigidBody: btRigidBody,
			rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo) {
        this.position.set(position)
        this.type = type
        this.mase = mase
        this.alive = true
		//this.particleEffectPool = particleEffectPool
		//this.particleEffect = particleEffectPool.obtain()
		this.rigidBody = rigidBody
		this.rigidBodyInfo = rigidBodyInfo
		Log.e("Enemy", "INI-------------------------"+this.particleEffect)
    }
}