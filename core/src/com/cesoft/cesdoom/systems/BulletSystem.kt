package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.*
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.entities.Gate
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class BulletSystem(private val gameWorld: GameWorld) : EntitySystem(), EntityListener {

	companion object {
		private val tag : String = BulletSystem::class.java.simpleName
		private const val GRAVITY = -200f
	}

	private val collisionConfig: btCollisionConfiguration = btDefaultCollisionConfiguration()
	private val dispatcher: btCollisionDispatcher = btCollisionDispatcher(collisionConfig)
	val broadphase = btDbvtBroadphase()
	private val solver: btConstraintSolver = btSequentialImpulseConstraintSolver()
	val collisionWorld: btDiscreteDynamicsWorld = btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig)

	//______________________________________________________________________________________________
	init {
		//broadphase.overlappingPairCache.setInternalGhostPairCallback(ghostPairCallback)
		collisionWorld.gravity = Vector3(0f, GRAVITY, 0f)
		CesContactListener().enable()
	}

	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine?) {
		engine!!.addEntityListener(Family.all(BulletComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		// Calcular colisiones
		collisionWorld.stepSimulation(Math.min(1f / 30f, delta), 5, 1f / 60f)
	}

	//______________________________________________________________________________________________
	inner class CesContactListener : ContactListener() {

		override fun onContactAdded(//SI usa filtros de colision, al tener los parametros matchX
			userValue0: Int, partId0: Int, index0: Int, match0: Boolean,
			userValue1: Int, partId1: Int, index1: Int, match1: Boolean) : Boolean {

			when(getValor(userValue0)) {
				BulletComponent.PLAYER_FLAG -> {
					when(getValor(userValue1)) {
						BulletComponent.ENEMY_FLAG -> Unit//collPlayerEnemy(getIndex(userValue1))
						BulletComponent.GROUND_FLAG -> collPlayerGround()
						BulletComponent.SCENE_FLAG -> collPlayerScene()
						BulletComponent.GATE_FLAG -> //collPlayerGate(getIndex(userValue1))
							Log.e(tag, "CesContactListener:--------PLAYER-GATE------$userValue0 : "+getIndex(userValue0))
					}
				}
				BulletComponent.ENEMY_FLAG -> {
					when(getValor(userValue1)) {
						BulletComponent.PLAYER_FLAG -> collPlayerEnemy(getIndex(userValue0))
						BulletComponent.GROUND_FLAG -> collEnemyGround(getIndex(userValue0))
						BulletComponent.SHOT_FLAG -> collShotEnemy(getIndex(userValue1), getIndex(userValue0))
					}
				}
				BulletComponent.GATE_FLAG -> {
					collPlayerGate(getIndex(userValue0))
					Log.e(tag, "CesContactListener:--------GATE-PLAYER------${getValor(userValue0)} : ${getIndex(userValue0)}   /  ${getValor(userValue1)} : ${getIndex(userValue1)}")
				}
				//BulletComponent.SHOT_FLAG -> collShotWall() //TODO: dejar marca de disparo en pared
			}
			return true
		}
	}
	//______________________________________________________________________________________________
	private fun collEnemyGround(iEnemy: Int) {
		val entityEnemy = enemies[iEnemy]
		val statusEnemy = entityEnemy?.getComponent(StatusComponent::class.java)
		statusEnemy?.isSaltando = false
	}
	//______________________________________________________________________________________________
	private fun collPlayerGround() {
		player.getComponent(PlayerComponent::class.java).isSaltando = false
	}
	//______________________________________________________________________________________________
	private fun collPlayerScene() {
		//Log.e(tag, "--------- COLLISION: Player + Scene --------------------------")
	}
	//______________________________________________________________________________________________
	private fun collPlayerEnemy(iEnemy: Int) {
		val entityEnemy = enemies[iEnemy]
		val statusEnemy = entityEnemy?.getComponent(StatusComponent::class.java)
		if(statusEnemy?.isDead() == false)
		{
			statusEnemy.setAttacking()
			PlayerComponent.hurt(5f)
		}
		else
			enemies.remove(iEnemy)
	}
	//______________________________________________________________________________________________
	private fun collShotEnemy(iShot: Int, iEnemy: Int) {
		// Enemy
		var entity = enemies[iEnemy]
		if(entity != null) {
			val estado = entity.getComponent(StatusComponent::class.java)
			estado.hurt(20f)
			if(estado.isDead()) {
				enemies.remove(iEnemy)
				removeBody(entity)
			}
		}
		// Shot
		entity = shots[iShot]
		if(entity!=null) {
			gameWorld.removeShot(entity)
			removeBody(entity)
		}
		shots.remove(iShot)
	}

	//______________________________________________________________________________________________
	private fun collPlayerGate(iGate: Int) {
		val gate = gates[iGate] as Gate
		gate.isOpening = true
	}


	//______________________________________________________________________________________________
	fun dispose() {
		for(entity in enemies)
			gameWorld.removeEnemy(entity.value as Enemy)
		for(entity in shots)
			gameWorld.removeShot(entity.value)
		collisionWorld.dispose()
		solver.dispose()
		broadphase.dispose()
		dispatcher.dispose()
		collisionConfig.dispose()
	}

	//______________________________________________________________________________________________
	private var enemyIndex = 0
	private var shotIndex = 0
	private var gateIndex = 0
	private val enemies = mutableMapOf<Int, Entity>()
	private val shots = mutableMapOf<Int, Entity>()
	private val gates = mutableMapOf<Int, Entity>()
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
			BulletComponent.GATE_FLAG -> {
				gateIndex++
				bullet.rigidBody.userIndex = gateIndex
				bullet.rigidBody.userIndex2 = gateIndex
				gates[gateIndex] = entity
				bullet.rigidBody.userValue = comprimeCodigo(bullet.rigidBody.userValue, bullet.rigidBody.userIndex)
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
			//else -> Log.e(tag, "Collision else added: "+bullet.rigidBody.userValue)
		}
		collisionWorld.addRigidBody(bullet.rigidBody)
	}
	private val MASCARA_INDEX = 0x7FFFFF00
	private val MASCARA_VALUE = 0x000000FF
	private fun comprimeCodigo(valor: Int, index: Int) = (valor and MASCARA_VALUE) + (index shl 16)
	private fun getValor(codigo : Int) = codigo and MASCARA_VALUE
	private fun getIndex(codigo : Int) = (codigo and MASCARA_INDEX) ushr 16

	//______________________________________________________________________________________________
	fun removeBody(entity: Entity) {
		val comp = entity.getComponent(BulletComponent::class.java)
		if(comp != null)
			collisionWorld.removeCollisionObject(comp.rigidBody)
	}

	//______________________________________________________________________________________________
	override fun entityRemoved(entity: Entity) {
		entity.getComponent(BulletComponent::class.java)
	}
}