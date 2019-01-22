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
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
//https://www.gamedevelopment.blog/full-libgdx-game-tutorial-ashley-steering-behaviors/
//
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

	companion object {
	    val tag: String = Enemy::class.java.simpleName
		const val RADIO = 18f
	}

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


	private var posTemp = Vector3()
	private var nextStep3D = Vector3()
	private var stepCalc2D = Vector2()
	private var currentPos2D = Vector2()

	private var stepCounter = 0
	private var path: ArrayList<Vector2>? = null
	private var pathIndex = 0
	private var timePathfinding = 0L
	fun mover(playerPosition: Vector3, delta: Float) {

		val model = getComponent(ModelComponent::class.java)
		model.instance.transform.getTranslation(posTemp)
		val dX = playerPosition.x - posTemp.x
		val dZ = playerPosition.z - posTemp.z
		currentPos2D = Vector2(posTemp.x, posTemp.z)
		//Log.e(tag, "POSIT----------------////////////////////////////////// ${Vector2(posTemp.x, posTemp.z)}")


		val status = getComponent(StatusComponent::class.java)
		val fuerza: Float
		val rot: Quaternion
		val distanciaConPlayer = posTemp.dst(playerPosition)

		/// No está en condiciones de atacar
		if(status.isAching() || status.isDead() || posTemp.y > RADIO+2) {
			fuerza = 0f
		}
		/// Esta al lado, atacale (Las colisiones no valen, porque aqui ignoro el estado)
		else if(distanciaConPlayer < Enemy.RADIO + PlayerComponent.RADIO+2) {
			status.setAttacking()
			val pain = 20f
			PlayerComponent.hurt(delta * pain)
			fuerza = 0f
		}
		/// Esta cerca, corre a por el
		else if(distanciaConPlayer < 180f) {
			fuerza = if(CesDoom.isMobile) 1600f else 2200f
			status.setRunning()
		}
		/// Esta lejos, camina buscando
		else {
			fuerza = if(CesDoom.isMobile) 600f else 800f
			status.setWalking()
		}

		/// Si hay movimiento
        if( ! status.isAttacking() && fuerza != 0f) {

			val player2D = Vector2(playerPosition.x, playerPosition.z)
			val map = MazeFactory.mapFactory.map

			//TODO: Si player cambia mucho la posicion, obliga a recalcular
			stepCounter++ //Obliga a recalcular pase lo que pase cada x ciclos
			if(stepCounter%20==0 || pathIndex == 0 || pathIndex >= path!!.size) {
				timePathfinding = System.currentTimeMillis()
				path = map.findPath(currentPos2D, player2D)
				path?.let { path ->
					if(path.size > 1) {
						pathIndex = 2
						stepCalc2D = path[1]
						nextStep3D = Vector3(stepCalc2D.x, posTemp.y, stepCalc2D.y)
						//if(path.size > 2)Log.e(tag, "PATH---------************************  ${path[0]}      *** ${path[1]}      ${path[2]}")
					}
					else
						nextStep3D = Vector3(player2D.x, posTemp.y, player2D.y)
				}
			}
			else {
				val next = path!![pathIndex]

				if(currentPos2D.dst(next) < 5) {
					pathIndex++
				}
				if(pathIndex >= path!!.size) {
					pathIndex = 0
				}
				else {
					stepCalc2D = next
					nextStep3D = Vector3(stepCalc2D.x, posTemp.y, stepCalc2D.y)
				}
			}

			//Log.e(tag, "$id PATH---------******************::: $stepCalc2D")
			//Log.e(tag, "$id ENEMY--------- $currentPos2D")
			//Log.e(tag, "$id PLAYER--------- $player2D")
		}

		/// Set velocity
		val dir = nextStep3D.add(posTemp.scl(-1f)).nor().scl(fuerza*delta)
		dir.y = rigidBody!!.linearVelocity.y
		rigidBody!!.linearVelocity = dir

		val transf = Matrix4()
		rigidBody!!.getWorldTransform(transf)
		transf.getTranslation(posTemp)

		/// Set position and rotation
		val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
		rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat())
		model.instance.transform.set(posTemp, rot)
	}

}