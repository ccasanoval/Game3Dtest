package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.*
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.entities.*
import com.cesoft.cesdoom.events.BulletEvent
import com.cesoft.cesdoom.events.BulletQueue
import com.cesoft.cesdoom.events.GameEvent
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class BulletSystem(
		eventSignal: Signal<BulletEvent>,
		private val gameEventSignal: Signal<GameEvent>)
	: EntitySystem(), EntityListener {

	companion object {
		private val tag : String = BulletSystem::class.java.simpleName
		private const val GRAVITY = -500f
	}

	private val eventQueue = BulletQueue()
	init {
		eventSignal.add(eventQueue)
	}

	private val collisionConfig: btCollisionConfiguration = btDefaultCollisionConfiguration()
	private val dispatcher: btCollisionDispatcher = btCollisionDispatcher(collisionConfig)
	private val broadPhase = btDbvtBroadphase()
	private val solver: btConstraintSolver = btSequentialImpulseConstraintSolver()
	val collisionWorld: btDiscreteDynamicsWorld = btDiscreteDynamicsWorld(dispatcher, broadPhase, solver, collisionConfig)

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
		//
		processEvents()
	}

	//______________________________________________________________________________________________
	// Contact callbacks calls == Overhead, so use only when necessary
	inner class CesContactListener : ContactListener() {

		override fun onContactAdded(//SI usa filtros de colision, al tener los parametros matchX
			userValue0: Int, partId0: Int, index0: Int, match0: Boolean,
			userValue1: Int, partId1: Int, index1: Int, match1: Boolean) : Boolean {

//Log.e(tag, "CesContactListener:--***************************------------${BulletComponent.getFlag(userValue0)} : ${BulletComponent.getFlag(userValue1)} ---- ${BulletComponent.getIndex(userValue1)}")

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
				BulletComponent.AMMO_FLAG -> {
					collPlayerAmmo(BulletComponent.getIndex(userValue0))
				}
				BulletComponent.HEALTH_FLAG -> {
					collPlayerHealth(BulletComponent.getIndex(userValue0))
				}
				//BulletComponent.SHOT_FLAG -> collShotWall() //TODO: dejar marca de disparo en pared
			}
			return true
		}
	}

	//______________________________________________________________________________________________
	private fun collPlayerGate(iGate: Int) {
		gates[iGate]?.tryToOpen()
	}

	//______________________________________________________________________________________________
	private fun collPlayerYouWin() {
		gameEventSignal.dispatch(GameEvent.YouWin)
	}
	//______________________________________________________________________________________________
	private fun collPlayerAmmo(iAmmo: Int) {
		gameEventSignal.dispatch(GameEvent(GameEvent.Type.AMMO_PICKUP, AmmoComponent.MAGAZINE_CAPACITY, ammos[iAmmo]!!))
		val ammo = ammos[iAmmo] as Ammo
		removeBody(ammo)
	}
	//______________________________________________________________________________________________
	private fun collPlayerHealth(iHealth: Int) {
		gameEventSignal.dispatch(GameEvent(GameEvent.Type.HEALTH_PICKUP, HealthComponent.DRUG_CAPACITY, healths[iHealth]!!))
		val health = healths[iHealth] as Health
		removeBody(health)
	}
	//______________________________________________________________________________________________
	private fun collPlayerSwitch(iSwitch: Int) {
		val switch = switches[iSwitch]
		switch?.activate()
		for(gate in gates.values) {
			if(gate.id == switch?.id) {
				gate.unlock()
				return
			}
		}
	}

	//______________________________________________________________________________________________
	fun dispose() {
		collisionWorld.dispose()
		solver.dispose()
		broadPhase.dispose()
		dispatcher.dispose()
		collisionConfig.dispose()
	}

	//______________________________________________________________________________________________
	private var gateIndex = 0
	private val gates = mutableMapOf<Int, Gate>()
	private var switchIndex = 0
	private val switches = mutableMapOf<Int, Switch>()
	private var ammoIndex = 0
	private val ammos = mutableMapOf<Int, Ammo>()
	private var healthIndex = 0
	private val healths = mutableMapOf<Int, Health>()
	private lateinit var player : Player
	override fun entityAdded(entity: Entity) {
		val bullet = BulletComponent.get(entity)

		when(bullet.rigidBody.userValue) {
			BulletComponent.PLAYER_FLAG -> {
				player = entity as Player
			}
			BulletComponent.GATE_FLAG -> {
				gateIndex++
				bullet.rigidBody.userIndex = gateIndex
				bullet.rigidBody.userIndex2 = gateIndex
				gates[gateIndex] = entity as Gate
				bullet.rigidBody.userValue = BulletComponent.calcCode(bullet.rigidBody.userValue, bullet.rigidBody.userIndex)
			}
			BulletComponent.SWITCH_FLAG -> {
				switchIndex++
				bullet.rigidBody.userIndex = switchIndex
				bullet.rigidBody.userIndex2 = switchIndex
				switches[switchIndex] = entity as Switch
				bullet.rigidBody.userValue = BulletComponent.calcCode(bullet.rigidBody.userValue, bullet.rigidBody.userIndex)
			}
			BulletComponent.AMMO_FLAG -> {
				ammoIndex++
				bullet.rigidBody.userIndex = ammoIndex
				bullet.rigidBody.userIndex2 = ammoIndex
				ammos[ammoIndex] = entity as Ammo
				bullet.rigidBody.userValue = BulletComponent.calcCode(bullet.rigidBody.userValue, bullet.rigidBody.userIndex)
			}
			BulletComponent.HEALTH_FLAG -> {
				healthIndex++
				bullet.rigidBody.userIndex = healthIndex
				bullet.rigidBody.userIndex2 = healthIndex
				healths[healthIndex] = entity as Health
				bullet.rigidBody.userValue = BulletComponent.calcCode(bullet.rigidBody.userValue, bullet.rigidBody.userIndex)
			}
			//else -> Log.e(tag, "Collision else added: "+bullet.rigidBody.userValue)
		}
		collisionWorld.addRigidBody(bullet.rigidBody)
	}


	//______________________________________________________________________________________________
	private fun removeBody(entity: Entity) {
		val comp = BulletComponent.get(entity)
		//if(comp != null)
			collisionWorld.removeCollisionObject(comp.rigidBody)
	}
	//______________________________________________________________________________________________
	override fun entityRemoved(entity: Entity) {
		//Log.e(tag, "entityRemoved--------------------------- $entity")
	}


	//______________________________________________________________________________________________
	private fun processEvents() {
		for(event in eventQueue.events) {
			when(event.type) {
				BulletEvent.Type.REMOVE -> {
					removeBody(event.entity)
				}
				//else -> Unit
			}
		}
	}
}