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
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.util.Log
import kotlinx.coroutines.*

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem(private val game: CesDoom) : EntitySystem(), EntityListener {
	private var entities: ImmutableArray<Entity>? = null
	private var player: Entity? = null

	init {
		Log.e("EnemySystem", "INI ---------------------------------------------------------")
	}

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
			EnemyFactory.update(delta, entity, posPlayer.cpy(), game.assets)
		}

		spawnIfNeeded()
	}

	private var waitToCreate = false
	fun pause() {
		waitToCreate = false
	}
	fun resume() {
		spawnIfNeeded()
	}
	private fun spawnIfNeeded() {
		if(waitToCreate)return
		waitToCreate = true
		GlobalScope.launch {
			while(entities!!.size() < 3) {
				delay(5000)
				if(Settings.paused)
					continue
				spawnEnemy()
			}
			waitToCreate = false
		}
	}

	//______________________________________________________________________________________________
	private fun spawnEnemy() {
		val enemy = EnemyFactory.create(
						game.assets.particleEffectPool!!,
						game.render,
						game.assets.getEnemy1(),
						EnemyComponent.TYPE.MONSTER1,
						Vector3(0f, 150f, -300f))
		engine.addEntity(enemy)
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) { player = entity }
	override fun entityRemoved(entity: Entity) { }

	//______________________________________________________________________________________________
	fun dispose() {
		Log.e("EnemySystem", "dispose----------------------------------------")
		for(entity in entities!!) {
			(entity as Enemy).reset()
		}
	}
}
