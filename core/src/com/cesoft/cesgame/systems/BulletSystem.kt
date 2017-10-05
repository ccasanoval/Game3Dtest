package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.*
import com.cesoft.cesgame.components.*

////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Ghost object ???
class BulletSystem : EntitySystem(), EntityListener {

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
		override fun onContactAdded(
			userValue0: Int, partId0: Int, index0: Int,
			userValue1: Int, partId1: Int, index1: Int): Boolean
		{

			/// ARENA + PLAYER
			if(userValue0 == BulletComponent.ARENA_FLAG && userValue1 == BulletComponent.PLAYER_FLAG)
			{
				System.err.println("------aa---- COLLISION: Player + Arena")
				player.getComponent(PlayerComponent::class.java).isSaltando = false
			}

			/// PLAYER + ENEMY
			if(userValue0 == BulletComponent.PLAYER_FLAG && userValue1 == BulletComponent.ENEMY_FLAG)
			{
				System.err.println("----aa------ COLLISION: Player + Enemy")
				PlayerComponent.health -= 2
				PlayerComponent.score -= 25
				val e = foes[index1]
				e.getComponent(StatusComponent::class.java).alive = false
				removeBody(e)
				foes.remove(e)
			}

			/// ENEMY + SHOT
			if(userValue0 == BulletComponent.ENEMY_FLAG && userValue1 == BulletComponent.SHOT_FLAG)
			{
				System.err.println("---bb------- COLLISION: Shot + enemy")
				//removeBody(eShot)
				System.err.println("---b2------- COLLISION: Shot + enemy")
			}

			/// ARENA + SHOT
			if(userValue0 == BulletComponent.ARENA_FLAG && userValue1 == BulletComponent.SHOT_FLAG)
			{
				System.err.println("---bb------- COLLISION: Shot + Arena")
				//removeBody(eShot)
				System.err.println("---b2------- COLLISION: Shot + Arena")
			}

			System.err.println("----A-----CesContactListener-------"+partId0+"--"+index0+"-- -- "+userValue0)
			System.err.println("----B-----CesContactListener-------"+partId1+"--"+index1+"-- -- "+userValue1)
			return true
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
	private val foes = arrayListOf<Entity>()
	private lateinit var arena : Entity
	private lateinit var player : Entity
	override fun entityAdded(entity: Entity)
	{
		val bulletComponent = entity.getComponent(BulletComponent::class.java)
		collisionWorld.addRigidBody(bulletComponent.rigidBody)
		when(bulletComponent.rigidBody.userValue)
		{
			BulletComponent.ARENA_FLAG -> arena = entity
			BulletComponent.PLAYER_FLAG -> player = entity
			BulletComponent.ENEMY_FLAG -> {
				foes.add(entity)
				bulletComponent.rigidBody.userIndex = foes.indexOf(entity)
			}
			else -> System.err.println("Collision else added: "+bulletComponent.rigidBody.userValue)
		}
		/*
		bulletComponent.rigidBody.userIndex = index++
		bulletComponent.rigidBody.userIndex2 = bulletComponent.rigidBody.userIndex

		if(entity.getComponent(PlayerComponent::class.java) != null)
			player  = entity

		lista.add(entity)*/
	}

	//______________________________________________________________________________________________
	fun removeBody(entity: Entity)
	{
		val comp = entity.getComponent(BulletComponent::class.java)
		if(comp != null) {
			collisionWorld.removeCollisionObject(comp.rigidBody)
			//comp.rigidBody.dispose()

		}
	}

	//______________________________________________________________________________________________
	override fun entityRemoved(entity: Entity) {}
}