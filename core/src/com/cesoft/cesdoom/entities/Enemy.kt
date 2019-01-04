package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.assets.ParticleEffectPool
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.managers.EnemyFactory
import com.cesoft.cesdoom.managers.MazeFactory
import com.cesoft.cesdoom.map.MapPathFinder


////////////////////////////////////////////////////////////////////////////////////////////////////
//
//TODO:Steering:
// http://fightingkitten.webcindario.com/?p=690
// https://gamedevelopment.tutsplus.com/tutorials/understanding-steering-behaviors-collision-avoidance--gamedev-7777 !!!!!!!!!
// https://github.com/libgdx/libgdx/wiki/Tile-maps --> CONSTRUCTOR
// https://github.com/libgdx/gdx-ai/wiki/Steering-Behaviors
// https://gamedev.stackexchange.com/questions/104021/pathfinding-and-collision-avoidance-on-mobile
class Enemy(val id: Int) : Entity() {

	private var alive = false
    var type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1
    private var mase: Float = 100f
	private var particleEffectPool: ParticleEffectPool? = null
	var particleEffect: ParticleEffect? = null
	var rigidBody: btRigidBody? = null
	private var rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo? = null

	fun getStatus() = StatusComponent(this)

	/*fun getPosition() : Vector3 {
		val pos = Vector3()
		/*val transf = Matrix4()
		rigidBody!!.getWorldTransform(transf)//bullet.rigidBody.getWorldTransform(transf)
		transf.getTranslation(pos)*/
		val model = getComponent(ModelComponent::class.java)
		model.instance.transform.getTranslation(pos)
		return pos
	}*/

	fun reset() {
        alive = false
    	mase = 100f


		//---- MAP TEST
		val width = 20
		val height = 20
		val mapData = BooleanArray(width * height)
		mapData[4 + width/2      +     (5 + height/2) * width] = true
		mapData[5 + width/2      +     (5 + height/2) * width] = true
		mapData[6 + width/2      +     (5 + height/2) * width] = true
		mapData[7 + width/2      +     (5 + height/2) * width] = true
		mapData[7 + width/2      +     (6 + height/2) * width] = true
		mapData[8 + width/2      +     (6 + height/2) * width] = true
		val map = MapPathFinder(width, height, mapData)
		map.getNextSteep(Vector2(2f,4f), Vector2(9f,7f))

		//MazeFactory.map.getNextSteep()
		//---- MAP TEST


		/// Animation
		EnemyFactory.playWalking(this)

		/// Status
		getComponent(StatusComponent::class.java).reset()

		/// Position
		val position = Vector3(0f, 150f, -250f)

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

    fun init(
			type: EnemyComponent.TYPE=EnemyComponent.TYPE.MONSTER1,
			mase: Float = 100f,
			particleEffectPool: ParticleEffectPool,
			rigidBody: btRigidBody,
			rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo) {
        this.type = type
        this.mase = mase
        this.alive = true
		this.particleEffectPool = particleEffectPool
		this.particleEffect = particleEffectPool.obtain()
		this.rigidBody = rigidBody
		this.rigidBodyInfo = rigidBodyInfo
    }



	private var posTempEnemy = Vector3()
	fun mover(playerPosition: Vector3, delta: Float)
	{
		val status = getComponent(StatusComponent::class.java)

		val model = getComponent(ModelComponent::class.java)
		model.instance.transform.getTranslation(posTempEnemy)
		val dX = playerPosition.x - posTempEnemy.x
		val dZ = playerPosition.z - posTempEnemy.z

		var fuerza = 0f
		val distanciaConPlayer = posTempEnemy.dst(playerPosition)

		/// No está en condiciones de atacar
		if(status.isAching() || status.isDead()) {
			fuerza = 0f
		}
		/// Esta al lado, atacale (Las colisiones no valen, porque aqui ignoro el estado)
		else if(distanciaConPlayer < EnemyFactory.RADIO + PlayerComponent.RADIO+2)
		{
			status.setAttacking()
			val pain = 20f
			PlayerComponent.hurt(delta * pain)
		}
		/// Esta cerca, corre a por el
		else if(distanciaConPlayer < 180f)
		{
			fuerza = if(CesDoom.isMobile) 1800f else 2200f
			status.setRunning()
		}
		/// Esta lejos, camina buscando
		else
		{
			fuerza = if(CesDoom.isMobile) 600f else 800f
			status.setWalking()
		}

		val dir = playerPosition.add(posTempEnemy.scl(-1f)).nor().scl(fuerza*delta)
		dir.y = rigidBody!!.linearVelocity.y//bullet.rigidBody.linearVelocity.y
		rigidBody!!.linearVelocity = dir//bullet.rigidBody.linearVelocity = dir

		val transf = Matrix4()
		rigidBody!!.getWorldTransform(transf)//bullet.rigidBody.getWorldTransform(transf)
		transf.getTranslation(posTempEnemy)

		val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
		val rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat())
		model.instance.transform.set(posTempEnemy, rot)

		val pos = Vector3()
		model.instance.transform.getTranslation(pos)
	}

}