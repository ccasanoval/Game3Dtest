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
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.util.Log
import java.util.*
import kotlin.concurrent.schedule


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem(private val game: CesDoom) : EntitySystem(), EntityListener {
	private var entities: ImmutableArray<Entity>? = null
	private var player: Entity? = null

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine) {
		entities = e.getEntitiesFor(Family.all(EnemyComponent::class.java, StatusComponent::class.java).get())
		e.addEntityListener(Family.one(PlayerComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
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
			EnemyFactory.update(delta, entity, posPlayer.cpy(), game.assets, game.render)
		}

		spawnIfNeeded()
	}

	private var waitToCreate = false
	fun pause() {
		timer.cancel()
		waitToCreate = false
	}
	fun resume() {
		spawnIfNeeded()
	}
	private val timer = Timer("schedule", true)
	private fun spawnIfNeeded() {
		if(entities!!.size() < 2 && !waitToCreate) {
			waitToCreate = true
			timer.purge()
			timer.schedule(4000) {
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
		val enemy = EnemyFactory.create(
						game.assets.getEnemy1(),
						EnemyComponent.TYPE.MONSTER1,
						Vector3(0f, 150f, -300f))
		engine.addEntity(enemy)
		//game.addEnemy(enemy)
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
