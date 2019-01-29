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
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.entities.Player
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem(private val game: CesDoom) : EntitySystem(), EntityListener {

	companion object {
		private const val MAX = 4				//TODO: aumenta cuando supera x puntos...
		private const val SPAWN_DELAY = 5*1000	//TODO: si pausa o background, debe actualizar time!!!
	}

	private var enemies: ImmutableArray<Enemy>? = null
	private var player: Player? = null
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
	private var countSpawnPosition = 0
	private var lastSpawn = System.currentTimeMillis()
	private fun spawIfNeeded() {
        if(Status.paused)lastSpawn = System.currentTimeMillis()
		if(System.currentTimeMillis() < lastSpawn + SPAWN_DELAY)return
		lastSpawn = System.currentTimeMillis()
		spawnAllEnemies()
		enemies?.let { enemies ->

			if(enemies.size() < MAX) {
                val id = getNextEnemyId()
				if(id >= allEnemies.size)return//Max enemy number reached
				try {
					engine.addEntity(getNextEnemy(id))
				}
				catch(e: Throwable) {//TODO: on pause, reset timer...
					Log.e("EnemySystem", "spawnIfNeeded:e:-------------------------------$e")
				}//TODO: check before fail
			}
		} ?: run { }
	}
	private fun getNextEnemyId() : Int {
		var id=allEnemies.size
		if(enemies?.size() == 0) {
			id=0
		}
		else {
			for(id0 in 0 until MAX) {
				if( ! isEnemyActive(id0)) {
					id = id0
					break
				}
			}
		}
		return id
	}
	private fun getNextEnemy(id: Int) : Enemy {
		val enemy = allEnemies[id]
		val pos = when(countSpawnPosition++ % 4) {
			0 -> Vector3(+250f, 150f, -250f)
			1 -> Vector3(-250f, 150f, -250f)
			2 -> Vector3(+250f, 150f, +250f)
			else -> Vector3(-250f, 150f, -250f)
		}
		enemy.reset(pos)
		return enemy
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

	private fun spawnAllEnemies() {
		if(allEnemies.size < MAX)//allEnemies.isEmpty() ||
		for(i in allEnemies.size until MAX) {
			//Log.e("EnemySystem", "spawnAllEnemies:-------------------------------$i  "+allEnemies.size+" ")
			val enemy = EnemyFactory.create(i,
						game.assets.particleEffectPool!!,
						game.render,
						game.assets.getEnemy(),
						EnemyComponent.TYPE.MONSTER1,
						Vector3())//La posicion se resetea en Enemy.reset  (0f, 150f, 350f)
			allEnemies.add(enemy)
		}
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) { player = entity as Player }
	override fun entityRemoved(entity: Entity) { }
}
