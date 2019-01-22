package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.managers.EnemyFactory
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.entities.Player
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem(private val game: CesDoom) : EntitySystem(), EntityListener {
	private var enemies: ImmutableArray<Enemy>? = null
	private var player: Player? = null
	private val MAX = 3	//TODO: aumenta cuando supera x puntos...
	private val SPAWN_DELAY = 5*1000//TODO: si pausa o background, debe actualizar time!!!
	private val allEnemies = ArrayList<Enemy>()

	init {
		Log.e("EnemySystem", "INI ---------------------------------------------------------")
		EnemyFactory.init(game)
	}

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine) {
		enemies = e.getEntitiesFor(Family.all(EnemyComponent::class.java, StatusComponent::class.java).get()) as ImmutableArray<Enemy>?
		e.addEntityListener(Family.one(PlayerComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		//TODO: humo donde aparece bicho...
		enemies?.let { enemies ->

			for (entity in enemies) {
				if(entity.getStatus().isDead())return
				val posPlayer = player!!.getPosition()
				EnemyFactory.update(delta, entity, posPlayer.cpy(), game.assets)
			}

			spawIfNeeded()
		}
	}

	//https://blog.egorand.me/concurrency-primitives-in-kotlin/
	//@Volatile private var spawning = false
	private var lastSpawn = System.currentTimeMillis()
	private fun spawIfNeeded() {
		if(System.currentTimeMillis() < lastSpawn + SPAWN_DELAY)return
		lastSpawn = System.currentTimeMillis()
		spawnAllEnemies()
		enemies?.let { enemies ->

			var id=allEnemies.size
			if(enemies.size() < MAX) {
				if(enemies.size() == 0) {
					id=0
				}
				else {
					for(id0 in 0 until MAX) {
						Log.e("EnemySystem", "spawIfNeeded:---------------------------B $id0")
						if( ! isEnemyActive(id0)) {
							id = id0
							break
						}
					}
				}

				Log.e("EnemySystem", "spawIfNeeded:---------------------------id=$id  n enemies="+enemies.size()+" --- ")
				if(id >= allEnemies.size) {
					return//Max enemy number reached
				}

				val enemy = allEnemies[id]//TODO:  (pooling)
				enemy.reset()
				try {
					engine.addEntity(enemy)
				}
				catch(e: Throwable) {//TODO: on pause, reset timer...
					Log.e("EnemySystem", "spawIfNeeded:e:-------------------------------$e")
				}//TODO: check before fail
			}
		} ?: run { }
	}

	private fun isEnemyActive(id: Int) : Boolean {
		enemies?.let { enemies ->
			for(i in 0 until enemies.size()) {
				if(enemies[i].id == id)
					return true
			}
		}
		return false
	}

	private var z_ = -1
	private fun spawnAllEnemies() {
		if(allEnemies.size < MAX)//allEnemies.isEmpty() ||
		for(i in allEnemies.size until MAX) {
			z_ = if(z_ < 0) 1 else -1
			//Log.e("EnemySystem", "spawnAllEnemies:-------------------------------$i  "+allEnemies.size+" ")
			val enemy = EnemyFactory.create(i,
						game.assets.particleEffectPool!!,
						game.render,
						game.assets.getEnemy1(),
						EnemyComponent.TYPE.MONSTER1,
						Vector3(0f, 150f, z_*350f))
			allEnemies.add(enemy)
		}
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) { player = entity as Player }
	override fun entityRemoved(entity: Entity) { }
}
