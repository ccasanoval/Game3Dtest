package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.managers.EnemyFactory
import com.cesoft.cesdoom.Assets
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.util.Log
import java.util.*
import kotlin.concurrent.schedule


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem(private val assets: Assets) : EntitySystem(), EntityListener {
	private var entities: ImmutableArray<Entity>? = null
	private var player: Entity? = null

	//private val xSpawns = floatArrayOf(+335f, +335f, -335f, -335f)
	//private val zSpawns = floatArrayOf(+335f, -335f, +335f, -335f)
	//private var maper = ComponentMapper.getFor(StatusComponent::class.java)

	//private val random = Random()
	//private val randomSpawnIndex: Int
	//	get() = random.nextInt(xSpawns.size)


	private var waitToCreate = false

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine) {
		entities = e.getEntitiesFor(Family.all(EnemyComponent::class.java, StatusComponent::class.java).get())
		e.addEntityListener(Family.one(PlayerComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	private val timer = Timer("schedule", true)
	override fun update(delta: Float) {
		//TODO: humo donde aparece bicho...
		if(entities == null)
			return

		for(entity in entities!!) {
			val bulletPlayer = player!!.getComponent(BulletComponent::class.java)
			val transf = Matrix4()
			bulletPlayer.rigidBody.getWorldTransform(transf)
			val posPlayer = Vector3()
			transf.getTranslation(posPlayer)
			EnemyFactory.update(delta, entity, posPlayer.cpy(), assets)
		}

		spawnIfNeeded()
	}

	private fun spawnIfNeeded() {
		if(entities!!.size() < 2 && !waitToCreate) {
			waitToCreate = true
			timer.purge()
			timer.schedule(1500) {
				spawnEnemy()
				timer.schedule(4000) {
					spawnEnemy()
					timer.schedule(4000) {
						spawnEnemy()
						waitToCreate = false
					}
				}
			}
		}
	}

	//______________________________________________________________________________________________
	private fun spawnEnemy() {
		engine.addEntity(
				EnemyFactory.create(assets.getMonstruo1(),
				EnemyComponent.TYPE.MONSTER1,
				Vector3(0f, 150f, -300f)))
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) { player = entity }
	override fun entityRemoved(entity: Entity) { }

	//______________________________________________________________________________________________
	fun dispose() {
				Log.e("EnemySystem", "dispose----------------------------------------")
		timer.cancel()
		timer.purge()
		for(entity in entities!!) {
			(entity as Enemy).reset()
		}
	}
}
