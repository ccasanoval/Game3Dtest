package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.*
import com.cesoft.cesgame.GameWorld
import com.cesoft.cesgame.components.*
import com.sun.org.apache.xpath.internal.operations.Bool

////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Ghost object ???
class BulletSystem(private val gameWorld: GameWorld) : EntitySystem(), EntityListener {

	private val collisionConfig: btCollisionConfiguration = btDefaultCollisionConfiguration()
	private val dispatcher: btCollisionDispatcher = btCollisionDispatcher(collisionConfig)
	private val broadphase: btBroadphaseInterface = btDbvtBroadphase()//btAxisSweep3(Vector3(-1000f, -1000f, -1000f), Vector3(1000f, 1000f, 1000f))
	private val solver: btConstraintSolver = btSequentialImpulseConstraintSolver()
	//private val ghostPairCallback: btGhostPairCallback = btGhostPairCallback()
	val collisionWorld: btDiscreteDynamicsWorld = btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig)

	//______________________________________________________________________________________________
	init {
		//broadphase.overlappingPairCache.setInternalGhostPairCallback(ghostPairCallback)
		collisionWorld.gravity = Vector3(0f, -10f, 0f)
		CesContactListener().enable()
	}

	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine?) {
		engine!!.addEntityListener(Family.all(BulletComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(deltaTime: Float) {
		// Calcular colisiones
		collisionWorld.stepSimulation(Math.min(1f / 30f, deltaTime), 5, 1f / 60f)
	}

	//______________________________________________________________________________________________
	inner class CesContactListener : ContactListener() {

		override fun onContactAdded(//SI usa filtros de colision, al tener los parametros matchX
			userValue0: Int, partId0: Int, index0: Int, match0: Boolean,
			userValue1: Int, partId1: Int, index1: Int, match1: Boolean) : Boolean
		//override fun onContactProcessed(userValue0: Int, userValue1: Int)//No usa filtros de colision
		{
			//System.err.println("----- COLLISION PRICESSSSS: "+getValor(userValue0)+"/"+getValor(userValue1))
			when(getValor(userValue0))
			{
				/*BulletComponent.ARENA_FLAG ->
				{
					when(getValor(userValue1))
					{
						BulletComponent.PLAYER_FLAG -> collArenaPlayer()
						BulletComponent.SHOT_FLAG -> collArenaShot(getIndex(userValue1))
						//BulletComponent.ENEMY_FLAG -> collArenaEnemy(getIndex(userValue1))
					}
				}*/
				BulletComponent.PLAYER_FLAG ->
				{
					when(getValor(userValue1))
					{
						BulletComponent.ARENA_FLAG -> collArenaPlayer()
						BulletComponent.ENEMY_FLAG -> collPlayerEnemy(getIndex(userValue1))
					}
				}
				BulletComponent.ENEMY_FLAG ->
				{
					System.err.println("*****************************************---Enemy  ::: "+index0+" == "+getIndex(userValue0))

					when(getValor(userValue1))
					{
						BulletComponent.PLAYER_FLAG -> collPlayerEnemy(getIndex(userValue0))
						BulletComponent.SHOT_FLAG -> collShotEnemy(getIndex(userValue1), getIndex(userValue0))
						//BulletComponent.ENEMY_FLAG -> collEnemyEnemy(getIndex(userValue0), getIndex(userValue1))
						//BulletComponent.ENEMY_FLAG -> collArenaEnemy(getIndex(userValue0))
					}
				}
				/*BulletComponent.SHOT_FLAG ->
				{
					when(getValor(userValue1))
					{
						BulletComponent.ENEMY_FLAG -> collShotEnemy(getIndex(userValue0), getIndex(userValue1))
						BulletComponent.ARENA_FLAG -> collArenaShot(getIndex(userValue0))
					}
				}*/
			}
			return true
		}
	}
	//______________________________________________________________________________________________
	private fun collArenaPlayer()
	{
		player.getComponent(PlayerComponent::class.java).isSaltando = false //TODO: Mal, porque arena son paredes tambien
	}
	//______________________________________________________________________________________________
	private fun collArenaShot(index : Int)
	{
		System.err.println("----aa------ COLLISION: Arena + Shot "+index)

		val e = shots[index]
		if(e != null) {
			gameWorld.remove(e)
			shots.remove(index)
		}
	}
	//______________________________________________________________________________________________
	private fun collPlayerEnemy(index: Int)
	{
		val e = enemies[index]
		if(e != null && e.getComponent(StatusComponent::class.java).alive) {
			System.err.println("----aa------ COLLISION: Player + Enemy VIVO ::: "+index)
			PlayerComponent.health -= 2
			PlayerComponent.score -= 20
			e.getComponent(StatusComponent::class.java)?.alive = false
			enemies.remove(index)
		}
		//else			System.err.println("----aa------ COLLISION: Player + Enemy MUERTO")
	}
	//______________________________________________________________________________________________
	private fun collShotEnemy(iShot: Int, iEnemy: Int)
	{
		System.err.println("---bb------- COLLISION: Shot ("+iShot+") + enemy ("+iEnemy+")")
		//
		var e = enemies[iEnemy]
		if(e != null) {

			//if(e.getComponent(StatusComponent::class.java).alive) {
				PlayerComponent.score += 20
				e.getComponent(StatusComponent::class.java).alive = false
			//}
			enemies.remove(iEnemy)
		}
		//
		e = shots[iShot]
		if(e!=null)
		{
			gameWorld.remove(e)
			System.err.println("---bb------- COLLISION: Shot + enemy REMOVE BODY SHOT")
		}
		shots.remove(iShot)
	}

	//______________________________________________________________________________________________
	private fun eliminarBalasPerdidas()
	{
		shots.forEach { (i, entity) ->
			run {
				val trans = Matrix4()
				entity.getComponent(BulletComponent::class.java).rigidBody.getWorldTransform(trans)
				val pos = Vector3()
				trans.getTranslation(pos)
				if(pos.x*pos.x > 500000 || pos.z*pos.z > 500000 || pos.y*pos.y > 1000)
				{
					//shots.remove(i)
					collArenaShot(i)
				}
			}
		}
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