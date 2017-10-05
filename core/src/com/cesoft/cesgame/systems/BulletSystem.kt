package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.*
import com.cesoft.cesgame.GameWorld
import com.cesoft.cesgame.components.*

////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Ghost object ???
class BulletSystem(private val gameWorld: GameWorld) : EntitySystem(), EntityListener {

	private val collisionConfig: btCollisionConfiguration = btDefaultCollisionConfiguration()
	private val dispatcher: btCollisionDispatcher = btCollisionDispatcher(collisionConfig)
	private val broadphase: btBroadphaseInterface = btDbvtBroadphase()//btAxisSweep3(Vector3(-1000f, -1000f, -1000f), Vector3(1000f, 1000f, 1000f))
	private val solver: btConstraintSolver = btSequentialImpulseConstraintSolver()
	//private val ghostPairCallback: btGhostPairCallback = btGhostPairCallback()
	val collisionWorld: btDiscreteDynamicsWorld = btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig)

	init {
		//broadphase.overlappingPairCache.setInternalGhostPairCallback(ghostPairCallback)
		collisionWorld.gravity = Vector3(0f, -10f, 0f)
		//CesContactListener().enable()
		CesContactListener2().enable()

	}

	inner class CesContactListener2 : ContactListener() {
		override fun onContactProcessed(userValue0: Int, userValue1: Int)
		{
			//System.err.println("----- COLLISION PRICESSSSS: "+getValor(userValue0)+"/"+getValor(userValue1))

			if(false)

			/// ARENA + PLAYER
			else if(userValue1 == BulletComponent.ARENA_FLAG && userValue0 == BulletComponent.PLAYER_FLAG)
			{
				System.err.println("------bb---- COLLISION: Player + Arena")
				player.getComponent(PlayerComponent::class.java).isSaltando = false
			}

			/// PLAYER + ENEMY
			else if(userValue0 == BulletComponent.PLAYER_FLAG && getValor(userValue1) == BulletComponent.ENEMY_FLAG)
			{
				val iEnemy = getIndex(userValue1)
				val e = enemies[iEnemy]
				if(e != null && e.getComponent(StatusComponent::class.java).alive) {
					System.err.println("----aa------ COLLISION: Player + Enemy VIVO")
					PlayerComponent.health -= 2
					PlayerComponent.score -= 25
					e.getComponent(StatusComponent::class.java)?.alive = false
					enemies.remove(iEnemy)
				}
			}
			else if(userValue1 == BulletComponent.PLAYER_FLAG && userValue0 == BulletComponent.ENEMY_FLAG)
			{
				System.err.println("----bb------ COLLISION: Player + Enemy")
				val iEnemy = getIndex(userValue0)
				PlayerComponent.health -= 2
				PlayerComponent.score -= 25
				val e = enemies[iEnemy]
				e?.getComponent(StatusComponent::class.java)?.alive = false
				//removeBody(e)
				enemies.remove(iEnemy)
			}

			/// ENEMY + SHOT
			else if(getValor(userValue0) == BulletComponent.ENEMY_FLAG && getValor(userValue1) == BulletComponent.SHOT_FLAG)
			{
				val iEnemy = getIndex(userValue0)
				val iShot = getIndex(userValue1)
				System.err.println("---bb------- COLLISION: Shot ("+iShot+") + enemy ("+iEnemy+")")
				//
				var e = enemies[iEnemy]
				e?.getComponent(StatusComponent::class.java)?.alive = false
				enemies.remove(iEnemy)
				//
				e = shots[iShot]
				if(e!=null)
				{
					gameWorld.remove(e)
					System.err.println("---bb------- COLLISION: Shot + enemy REMOVE BODY SHOT")
				}
				shots.remove(iShot)
			}

			/// ARENA + SHOT
			else if(userValue1 == BulletComponent.ARENA_FLAG && getValor(userValue0) == BulletComponent.SHOT_FLAG)
			{
				System.err.println("---bb------- COLLISION: Shot + Arena")

				val iShot = getIndex(userValue0)
				val e = shots[iShot]!!
				gameWorld.remove(e)
				shots.remove(iShot)
			}

			/// ARENA + ENEMY
			else if(userValue1 == BulletComponent.ARENA_FLAG && getValor(userValue0) == BulletComponent.ENEMY_FLAG)
			{
				//System.err.println("---aa------- COLLISION: Shot + Arena")
			}
			else
			{
				System.err.println("----- COLLISION OTRA: "+userValue0+"/"+userValue1)
			}
		}
	}

	//______________________________________________________________________________________________
	/*inner class CesContactListener : ContactListener()
	{
		override fun onContactStarted(colObj0: btCollisionObject, colObj1: btCollisionObject)
		{
			System.err.println("COLL------------${colObj0.userValue}--${colObj1.userValue}-------${colObj0.userIndex}--${colObj1.userIndex}----")

			//TODO: mejorar!

			/// ARENA + PLAYER
			if(colObj0.userValue == BulletComponent.ARENA_FLAG
				&& colObj1.userValue == BulletComponent.PLAYER_FLAG)
			{
				System.err.println("------a---- COLLISION: Player + Arena")
				//if(colObj1.userData is Entity)
				val e = colObj1.userData as Entity
				e.getComponent(PlayerComponent::class.java).isSaltando = false
			}
			else if(colObj1.userValue == BulletComponent.ARENA_FLAG
					&& colObj0.userValue == BulletComponent.PLAYER_FLAG)
			{
				System.err.println("------b---- COLLISION: Player + Arena")
				//if(colObj1.userData is Entity)
				val e = colObj0.userData as Entity
				e.getComponent(PlayerComponent::class.java).isSaltando = false
			}

			/// ARENA + SHOT
			else if(colObj1.userValue == BulletComponent.ARENA_FLAG
					&& colObj0.userValue == BulletComponent.SHOT_FLAG)
			{
				System.err.println("---b------- COLLISION: Shot + Arena")
				val eShot = colObj0.userData as Entity
				removeBody(eShot)
				System.err.println("---b2------- COLLISION: Shot + Arena")
			}

			/// ENEMY + SHOT
			else if(colObj0.userValue == BulletComponent.ENEMY_FLAG
				&& colObj1.userValue == BulletComponent.SHOT_FLAG)
			{
				System.err.println("----a------ COLLISION: Shot + Enemy")
				PlayerComponent.score += 100
				val eEnemy = colObj0.userData as Entity
				val eShot = colObj1.userData as Entity
				removeBody(eShot)
				eEnemy.getComponent(StatusComponent::class.java).alive = false
			}
			else if(colObj1.userValue == BulletComponent.ENEMY_FLAG
					&& colObj0.userValue == BulletComponent.SHOT_FLAG)
			{
				System.err.println("-----b----- COLLISION: Shot + Enemy")
				PlayerComponent.score += 100
				val eEnemy = colObj1.userData as Entity
				val eShot = colObj0.userData as Entity
				removeBody(eShot)
				eEnemy.getComponent(StatusComponent::class.java).alive = false
			}

			/// PLAYER + ENEMY
			else if(colObj0.userValue == BulletComponent.PLAYER_FLAG
				&& colObj1.userValue == BulletComponent.ENEMY_FLAG)
			{
				System.err.println("----a------ COLLISION: Player + Enemy")
				PlayerComponent.score -= 25
				val ePlayer = colObj0.userData as Entity
				val eEnemy = colObj1.userData as Entity
				eEnemy.getComponent(StatusComponent::class.java).alive = false
			}
			else if(colObj1.userValue == BulletComponent.PLAYER_FLAG
					&& colObj0.userValue == BulletComponent.ENEMY_FLAG)
			{
				System.err.println("--b-------- COLLISION: Player + Enemy")
				System.err.println("----a------ COLLISION: Player + Enemy")
				PlayerComponent.score -= 25
				val ePlayer = colObj1.userData as Entity
				val eEnemy = colObj0.userData as Entity
				eEnemy.getComponent(StatusComponent::class.java).alive = false
			}

			///
			/*if(colObj0.userData is Entity && colObj0.userData is Entity)
			{
				val entity0 = colObj0.userData as Entity
				val entity1 = colObj1.userData as Entity
				//

				if(entity0.getComponent(PlayerComponent::class.java) != null
					&& entity1.getComponent(EnemyComponent::class.java) != null)
				{
					System.err.println("---------- COLLISION: Player + Enemy")
					if(entity0.getComponent(EnemyComponent::class.java) != null
						&& entity0.getComponent(StatusComponent::class.java).alive)
					{
						//entity1.getComponent(PlayerComponent::class.java).health -= 10
						PlayerComponent.health -= 10
						entity0.getComponent(StatusComponent::class.java).alive = false
					}
					else if(entity1.getComponent(StatusComponent::class.java).alive)
					{
						//entity0.getComponent(PlayerComponent::class.java).health -= 10
						PlayerComponent.health -= 10
						entity1.getComponent(StatusComponent::class.java).alive = false
					}
				}

			}*/
		}
	}*/

	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine?) {
		engine!!.addEntityListener(Family.all(BulletComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(deltaTime: Float) {
		collisionWorld.stepSimulation(Math.min(1f / 30f, deltaTime), 5, 1f / 60f)
	}

	//______________________________________________________________________________________________
	fun dispose() {
		collisionWorld.dispose()
		solver.dispose()
		broadphase.dispose()
		dispatcher.dispose()
		collisionConfig.dispose()
		//ghostPairCallback.dispose()
	}

	//______________________________________________________________________________________________

	private var enemyIndex = 0
	private var shotIndex = 0
	private val enemies = mutableMapOf<Int, Entity>()
	private val shots = mutableMapOf<Int, Entity>()
	private lateinit var arena : Entity
	private lateinit var player : Entity
	override fun entityAdded(entity: Entity)
	{
		val bullet = entity.getComponent(BulletComponent::class.java)

		when(bullet.rigidBody.userValue)
		{
			BulletComponent.ARENA_FLAG ->
			{
				arena = entity
			}
			BulletComponent.PLAYER_FLAG ->
			{
				player = entity
			}
			BulletComponent.ENEMY_FLAG -> {
				enemyIndex++
				bullet.rigidBody.userIndex = enemyIndex
				bullet.rigidBody.userIndex2 = enemyIndex
				enemies[enemyIndex] = entity
				bullet.rigidBody.userValue = comprimeCodigo(bullet.rigidBody.userValue, bullet.rigidBody.userIndex)
			}
			BulletComponent.SHOT_FLAG -> {
				shotIndex++
				bullet.rigidBody.userIndex = shotIndex
				bullet.rigidBody.userIndex2 = shotIndex
				shots[shotIndex] = entity
				bullet.rigidBody.userValue = comprimeCodigo(bullet.rigidBody.userValue, bullet.rigidBody.userIndex)
			}
			else -> System.err.println("Collision else added: "+bullet.rigidBody.userValue)
		}
		collisionWorld.addRigidBody(bullet.rigidBody)
	}
	private val MASCARA_INDEX = 0x7FFFFF00
	private val MASCARA_VALUE = 0x000000FF
	private fun comprimeCodigo(valor: Int, index: Int) = (valor and MASCARA_VALUE) + (index shl 8)
	private fun getValor(codigo : Int) = codigo and MASCARA_VALUE
	private fun getIndex(codigo : Int) = (codigo and MASCARA_INDEX) ushr 8

	//______________________________________________________________________________________________
	fun removeBody(entity: Entity)
	{
		val comp = entity.getComponent(BulletComponent::class.java)
		collisionWorld.removeCollisionObject(comp.rigidBody)
	}

	//______________________________________________________________________________________________
	override fun entityRemoved(entity: Entity)
	{
		val comp = entity.getComponent(BulletComponent::class.java)
		System.err.println("------COLLISION : REMOVED----"+getValor(comp.rigidBody.userValue)+"-----"+comp.rigidBody.userValue+" : "+comp.rigidBody.userIndex)
	}
}