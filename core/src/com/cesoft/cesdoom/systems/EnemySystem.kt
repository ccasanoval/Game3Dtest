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
import kotlinx.coroutines.*

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem(private val game: CesDoom) : EntitySystem(), EntityListener {
	private var enemies: ImmutableArray<Enemy>? = null
	private var player: Entity? = null

	init {
		Log.e("EnemySystem", "INI ---------------------------------------------------------")
	}

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine) {
		enemies = e.getEntitiesFor(Family.all(EnemyComponent::class.java, StatusComponent::class.java).get()) as ImmutableArray<Enemy>?
		e.addEntityListener(Family.one(PlayerComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		//TODO: humo donde aparece bicho...
		if(enemies == null)
			return

		for(entity in enemies!!) {
			val bulletPlayer = player!!.getComponent(BulletComponent::class.java)
			val transf = Matrix4()
			bulletPlayer.rigidBody.getWorldTransform(transf)
			val posPlayer = Vector3()
			transf.getTranslation(posPlayer)
			EnemyFactory.update(delta, entity, posPlayer.cpy(), game.assets)
		}

		spawIfNeeded()
	}

	//https://blog.egorand.me/concurrency-primitives-in-kotlin/
	//@Volatile private var spawning = false
	private var lastSpawn = System.currentTimeMillis()
	private fun spawIfNeeded() {
		if(System.currentTimeMillis() < lastSpawn + 5*1000)return
		lastSpawn = System.currentTimeMillis()
		spawnAllEnemies()
		enemies?.let { enemies ->
			if(enemies.size() < MIN) {
						Log.e("tag", "--------------spawIfNeeded 3 ")

				var id = 0
				for(i in 0 until enemies.size())
					if(enemies[i].id == id)
						id++
				if(id >= allEnemies.size) {
					return//Max enemy number reached
				}

				val enemy = allEnemies[id]//TODO:  (pooling)
				enemy.reset()
				try {
					engine.addEntity(enemy)
				}
				catch(e: Exception) {//TODO: on pause, reset timer...
					Log.e("EnemySystem", "-------------------------------$e")
				}//TODO: check before fail
			}
		} ?: run { }
Log.e("tag", "--------------spawIfNeeded zzz: ")
	}
	private val MIN = 3
	private val MAX = 7
	private val allEnemies = ArrayList<Enemy>()
	private fun spawnAllEnemies() {
		if(allEnemies.isEmpty())
		for(i in 0 until MAX) {
			val enemy = EnemyFactory.create(i,
						game.assets.particleEffectPool!!,
						game.render,
						game.assets.getEnemy1(),
						EnemyComponent.TYPE.MONSTER1,
						Vector3(0f, 150f, -300f))
			allEnemies.add(enemy)
		}
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) { player = entity }
	override fun entityRemoved(entity: Entity) { }

	//______________________________________________________________________________________________
	fun dispose() {
		Log.e("EnemySystem", "dispose--------------------------------------------------")
	}
}
