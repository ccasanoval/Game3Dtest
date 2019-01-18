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
	private var reduceCPU = 0

	private var nextStep3D = Vector3()

	private var stepCalc2D = Vector2()
	private var currentPos2D = Vector2()

	fun mover(playerPosition: Vector3, delta: Float) {

		//TODO: Solo si distancia > EnemyFactory.RADIO + PlayerComponent.RADIO+10

		/// Posicion Enemigo
		val transf1 = Matrix4()
		/*val pos1 = Vector3()
		rigidBody!!.getWorldTransform(transf1)
		transf1.getTranslation(pos1)
		currentPos = Vector2(pos1.x, pos1.z)
		Log.e(tag, "POSIT---------------------------------------------------$currentPos")*/

		val model = getComponent(ModelComponent::class.java)
		model.instance.transform.getTranslation(posTemp)
		val dX_ = playerPosition.x - posTemp.x
		val dZ_ = playerPosition.z - posTemp.z
		currentPos2D = Vector2(posTemp.x, posTemp.z)
		Log.e(tag, "POSIT----------------////////////////////////////////// ${Vector2(posTemp.x, posTemp.z)}")


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

		//Log.e(tag, "move---------------------------------------------fuerza= $fuerza ")

		var dX = 0f
		var dZ = 0f
		/// Si hay movimiento
        if(status.isAttacking() || fuerza == 0f) {
            //dX = playerPosition.x - pos1.x
            //dZ = playerPosition.y - pos1.z
//            val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
//            rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat())
        }
        else {
			//TODO: comparar con posicion anterior y si no cambia no hace falta que recalcules..¿?
			val player2D = Vector2(playerPosition.x, playerPosition.z)
			val map = MazeFactory.mapFactory.map
			val path = map.findPath(currentPos2D, player2D)
			if(path.size > 1) {
				stepCalc2D = path[1]
				nextStep3D = Vector3(stepCalc2D.x, posTemp.y, stepCalc2D.y)
			}
			else
				nextStep3D = Vector3(player2D.x, posTemp.y, player2D.y)
			Log.e(tag, "ENEMY--------- $currentPos2D")
			Log.e(tag, "PLAYER--------- $player2D")
			if(path.size > 2)
				Log.e(tag, "PATH--------- ${path[0]}  ${path[1]}  ${path[2]}")

			//if(++reduceCPU % 2 == 0) {
			/*val dis :Float =
					if(stepCalc.x == -696969f) 0f
					else currentPos.dst2(stepCalc)
			//Log.e(tag, "dis------------------------------- $dis")
			if(dis < Enemy.RADIO+PlayerComponent.RADIO || ++reduceCPU % 5 == 0) {
				val player = Vector2(playerPosition.x, playerPosition.z)
				if (stepCalc.x == -696969f) {
					stepCalc = player.cpy()
					step3 = playerPosition.cpy()
				}
				try {
					//stepCalc = MazeFactory.map.getNextSteep(currentPos, player)
					val map = MazeFactory.mapFactory.map
					map.findPath(currentPos, player)
					step3 = Vector3(stepCalc.x, playerPosition.y, stepCalc.y)
					Log.e(tag, "MOVER---------------------------------------------------$stepCalc")
				} catch (e: Exception) {
					Log.e(tag, "mover:e:------------------------------------------------------------$e")
				}
			}*/

			//Log.e(tag, "mover-------------------------player: $playerPosition  /  step: $step2  /  ")

			dX = stepCalc2D.x - currentPos2D.x
			dZ = stepCalc2D.y - currentPos2D.y
//			val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
//			rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat())
		}

		/// Set velocity
		val dir = nextStep3D.add(posTemp.scl(-1f)).nor().scl(fuerza*delta)
		dir.y = rigidBody!!.linearVelocity.y
		rigidBody!!.linearVelocity = dir

		val transf = Matrix4()
		rigidBody!!.getWorldTransform(transf)
		transf.getTranslation(posTemp)

		/*dX = playerPosition.x - pos1.x
		dZ = playerPosition.y - pos1.z
		val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
		rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat())*/

		/// Set position and rotation
		val theta = Math.atan2(dX_.toDouble(), dZ_.toDouble()).toFloat()
		rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat())
		model.instance.transform.set(posTemp, rot)


		//Log.e(tag, "POSIT2--------------------------------------------------$currentPos")
/*


		val status = getComponent(StatusComponent::class.java)

		val model = getComponent(ModelComponent::class.java)
		model.instance.transform.getTranslation(posTemp)
		val dX = playerPosition.x - posTemp.x
		val dZ = playerPosition.z - posTemp.z

		var fuerza = 0f
		val distanciaConPlayer = posTemp.dst(playerPosition)

		/// No está en condiciones de atacar
		if(status.isAching() || status.isDead()) {
			fuerza = 0f
		}
		/// Esta al lado, atacale (Las colisiones no valen, porque aqui ignoro el estado)
		else if(distanciaConPlayer < Enemy.RADIO + PlayerComponent.RADIO+2)
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

		val dir = playerPosition.add(posTemp.scl(-1f)).nor().scl(fuerza*delta)
		dir.y = rigidBody!!.linearVelocity.y//bullet.rigidBody.linearVelocity.y
		rigidBody!!.linearVelocity = dir//bullet.rigidBody.linearVelocity = dir

		val transf = Matrix4()
		rigidBody!!.getWorldTransform(transf)//bullet.rigidBody.getWorldTransform(transf)
		transf.getTranslation(posTemp)

		val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
		val rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat())
		model.instance.transform.set(posTemp, rot)

		val pos = Vector3()
		model.instance.transform.getTranslation(pos)
		*/
	}

}