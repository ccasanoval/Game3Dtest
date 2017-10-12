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

	private val GRAVEDAD = 200f

	private val collisionConfig: btCollisionConfiguration = btDefaultCollisionConfiguration()
	private val dispatcher: btCollisionDispatcher = btCollisionDispatcher(collisionConfig)
	private val broadphase: btBroadphaseInterface = btDbvtBroadphase()//btAxisSweep3(Vector3(-1000f, -1000f, -1000f), Vector3(1000f, 1000f, 1000f))
	private val solver: btConstraintSolver = btSequentialImpulseConstraintSolver()
	//private val ghostPairCallback: btGhostPairCallback = btGhostPairCallback()
	val collisionWorld: btDiscreteDynamicsWorld = btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig)

	//______________________________________________________________________________________________
	init {
		//broadphase.overlappingPairCache.setInternalGhostPairCallback(ghostPairCallback)
		collisionWorld.gravity = Vector3(0f, -GRAVEDAD, 0f)
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
				BulletComponent.PLAYER_FLAG ->
				{
					when(getValor(userValue1))
					{
						BulletComponent.ENEMY_FLAG -> collPlayerEnemy(getIndex(userValue1))
						BulletComponent.GROUND_FLAG -> collPlayerGround()
						BulletComponent.SCENE_FLAG -> collPlayerScene()
					}
				}
				BulletComponent.ENEMY_FLAG ->
				{
					when(getValor(userValue1))
					{
						BulletComponent.PLAYER_FLAG -> collPlayerEnemy(getIndex(userValue0))
						BulletComponent.SHOT_FLAG -> collShotEnemy(getIndex(userValue1), getIndex(userValue0))
					}
				}
			}
			return true
		}
	}
	//______________________________________________________________________________________________
	private fun collPlayerGround()
	{
		//System.err.println("--------- COLLISION: Player + Ground ")
		player.getComponent(PlayerComponent::class.java).isSaltando = false
	}
	//______________________________________________________________________________________________
	private fun collPlayerScene()
	{
		//System.err.println("--------- COLLISION: Player + Scene --------------------------")
	}
	//______________________________________________________________________________________________
	private fun collPlayerEnemy(index: Int)
	{
		//System.err.println("--------- COLLISION: Player + Enemy --------------------------"+index)

		val e = enemies[index]
		if(e?.getComponent(StatusComponent::class.java) != null
			&& e.getComponent(StatusComponent::class.java).isAlive)
		{
			//System.err.println("----aa------ COLLISION: Player + Enemy VIVO ::: "+index)
			PlayerComponent.health -= 0.1f//delay
			//PlayerComponent.score -= 20
			//TODO: delay para que no mate al tipo enseguida..........
			e.getComponent(StatusComponent::class.java).hurt() //.isAlive = false
			enemies.remove(index)
		}
		//else			System.err.println("----aa------ COLLISION: Player + Enemy MUERTO")
	}
	//______________________________________________________________________________________________
	private fun collShotEnemy(iShot: Int, iEnemy: Int)
	{
		//System.err.println("---bb------- COLLISION: Shot ("+iShot+") + enemy ("+iEnemy+")")
		//
		var e = enemies[iEnemy]
		if(e != null) {
			PlayerComponent.score += 20
			e.getComponent(StatusComponent::class.java).hurt()
			//enemies.remove(iEnemy)
			//System.err.println("---bb------- COLLISION: Shot + enemy REMOVE BODY ENEMY")
		}
		//
		e = shots[iShot]
		if(e!=null)
		{
			gameWorld.remove(e)
			//System.err.println("---bb------- COLLISION: Shot + enemy REMOVE BODY SHOT")
		}
		shots.remove(iShot)
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
	//private lateinit var arena : Entity
	private lateinit var player : Entity
	override fun entityAdded(entity: Entity)
	{
		val bullet = entity.getComponent(BulletComponent::class.java)

		when(bullet.rigidBody.userValue)
		{
			BulletComponent.GROUND_FLAG ->
			{
				//ground = entity
			}
			BulletComponent.SCENE_FLAG ->
			{
				//scene = entity
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