package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.*
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.entities.Gate
import com.cesoft.cesdoom.entities.Player
import com.cesoft.cesdoom.entities.Switch
import com.cesoft.cesdoom.util.Log



////////////////////////////////////////////////////////////////////////////////////////////////////
//
class BulletSystem : EntitySystem(), EntityListener {

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
		collisionWorld.gravity = Vector3(0f, GRAVITY, 0f)
		CesContactListener().enable()
	}

	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine?) {
		engine!!.addEntityListener(Family.all(BulletComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		//TODO: haz que no salte muros si delta crece por lentitud al procesar...
		// Calcular colisiones
		collisionWorld.stepSimulation(Math.min(1f / 30f, delta), 5, 1f / 60f)
	}

	//______________________________________________________________________________________________
	// Contact callbacks calls == Overhead, so use only when necessary
	inner class CesContactListener : ContactListener() {

		override fun onContactAdded(//SI usa filtros de colision, al tener los parametros matchX
			userValue0: Int, partId0: Int, index0: Int, match0: Boolean,
			userValue1: Int, partId1: Int, index1: Int, match1: Boolean) : Boolean {

Log.e(tag, "CesContactListener:--***************************------------${BulletComponent.getFlag(userValue0)} : ${BulletComponent.getFlag(userValue1)} ---- ${BulletComponent.getIndex(userValue1)}")

			when(BulletComponent.getFlag(userValue0)) {
				BulletComponent.SWITCH_FLAG -> {
					collPlayerSwitch(BulletComponent.getIndex(userValue0))
				}
				BulletComponent.GATE_FLAG -> {
					collPlayerGate(BulletComponent.getIndex(userValue0))
				}
				BulletComponent.YOU_WIN_FLAG -> {
					collPlayerYouWin()
				}
				//BulletComponent.SHOT_FLAG -> collShotWall() //TODO: dejar marca de disparo en pared
			}
			return true
		}
	}

	//______________________________________________________________________________________________
	private fun collPlayerGate(iGate: Int) {
		val gate = gates[iGate] as Gate
		gate.isOpening = true
	}

	//______________________________________________________________________________________________
	private fun collPlayerYouWin() {
		(player as Player).youWin()
	}
	//______________________________________________________________________________________________
	private fun collPlayerSwitch(iSwitch: Int) {
		val switch = switches[iSwitch] as Switch
		switch.activate()
	}

	//______________________________________________________________________________________________
	fun dispose() {
		//for(entity in enemies)
		//	gameWorld.removeEnemy(entity.value as Enemy)
		//for(entity in shots)
		//	gameWorld.removeShot(entity.value)
		collisionWorld.dispose()
		solver.dispose()
		broadphase.dispose()
		dispatcher.dispose()
		collisionConfig.dispose()
	}

	//______________________________________________________________________________________________
	private var gateIndex = 0
	private val gates = mutableMapOf<Int, Entity>()
	private var switchIndex = 0
	private val switches = mutableMapOf<Int, Entity>()
	private lateinit var player : Entity
	override fun entityAdded(entity: Entity) {
		val bullet = entity.getComponent(BulletComponent::class.java)

		//Log.e(tag, "Collision else added: --------------------------"+bullet.rigidBody.userValue)
		when(bullet.rigidBody.userValue) {
			BulletComponent.PLAYER_FLAG -> {
				player = entity
			}
			BulletComponent.GATE_FLAG -> {
				gateIndex++
				bullet.rigidBody.userIndex = gateIndex
				bullet.rigidBody.userIndex2 = gateIndex
				gates[gateIndex] = entity
				bullet.rigidBody.userValue = BulletComponent.calcCode(bullet.rigidBody.userValue, bullet.rigidBody.userIndex)
			}
			BulletComponent.SWITCH_FLAG -> {
				switchIndex++
				bullet.rigidBody.userIndex = switchIndex
				bullet.rigidBody.userIndex2 = switchIndex
				switches[switchIndex] = entity
				bullet.rigidBody.userValue = BulletComponent.calcCode(bullet.rigidBody.userValue, bullet.rigidBody.userIndex)
			}
			//else -> Log.e(tag, "Collision else added: "+bullet.rigidBody.userValue)
		}
		collisionWorld.addRigidBody(bullet.rigidBody)
	}


	//______________________________________________________________________________________________
	fun removeBody(entity: Entity) {
		val comp = entity.getComponent(BulletComponent::class.java)
		if(comp != null)
			collisionWorld.removeCollisionObject(comp.rigidBody)
	}

	//______________________________________________________________________________________________
	override fun entityRemoved(entity: Entity) {
	}
}