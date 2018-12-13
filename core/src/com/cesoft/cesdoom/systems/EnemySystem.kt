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
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter
import com.cesoft.cesdoom.Assets
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

	private val posPlayerTemp = Vector3()
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
		//if(entities!!.size() < 5) spawnEnemy(randomSpawnIndex)

		if(entities == null)
			return

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
		for(entity in entities!!)
		{
			val status = entity.getComponent(StatusComponent::class.java)
			if(status.isDead())
			{
				val model = entity.getComponent(ModelComponent::class.java)
				if(model.blendingAttribute != null)
					model.blendingAttribute!!.opacity = 1 - status.deathProgres()

				val enemy = entity.getComponent(EnemyComponent::class.java)
				if( ! enemy.isShowingParticles) {
					enemy.isShowingParticles = true
					val effect = assets.getParticulas()
					(effect.controllers.first().emitter as RegularEmitter).emissionMode =
							//RegularEmitter.EmissionMode.Enabled
							RegularEmitter.EmissionMode.EnabledUntilCycleEnd
					effect.setTransform(model.instance.transform)
					effect.scale(5f, 8f, 5f)
					effect.init()
					effect.start()
					RenderSystem.particleSystem.add(effect)
				}
			}

			///----- ANIMATION
			val animat = entity.getComponent(AnimationComponent::class.java)
			animat?.update(delta)

			///----- BULLET
			/// Player position
			val bulletPlayer = player!!.getComponent(BulletComponent::class.java)
			val transf = Matrix4()
			bulletPlayer.rigidBody.getWorldTransform(transf)

			transf.getTranslation(posPlayerTemp)
			//val orientation = 0f //TODO: Camara?
			//orientation = Math.atan2(-playerPosition.z.toDouble(), playerPosition.x.toDouble()).toFloat()

			//
			EnemyFactory.mover(entity, posPlayerTemp.cpy(), delta)
		}
	}



	//______________________________________________________________________________________________
	private fun spawnEnemy() {
		/*engine!!.addEntity(EnemyFactory.create(
				EnemyComponent.TYPE.MONSTER1,
				Vector3(xSpawns[randomSpawnIndex], 5f, zSpawns[randomSpawnIndex]),
				100f))*/
				//SceneFactory.createEnemy(model, Vector3(xSpawns[randomSpawnIndex], 5f, zSpawns[randomSpawnIndex]), ++index))
		engine.addEntity(EnemyFactory.create(assets.getMonstruo1(), EnemyComponent.TYPE.MONSTER1, Vector3(0f, 150f, -300f)))
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) { player = entity }
	override fun entityRemoved(entity: Entity) { }

	//______________________________________________________________________________________________
	fun dispose()
	{
		timer.cancel()
		timer.purge()
		EnemyFactory.dispose()
		//assets.endMonstruo1()
	}
}
