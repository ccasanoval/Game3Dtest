package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesgame.components.*
import com.cesoft.cesgame.managers.EnemyFactory

import java.util.Random
import com.badlogic.gdx.ai.steer.behaviors.Seek
import com.cesoft.cesgame.bullet.BulletLocation


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem : EntitySystem(), EntityListener {
	private var entities: ImmutableArray<Entity>? = null
	private var player: Entity? = null

	private val xSpawns = floatArrayOf(+335f, +335f, -335f, -335f)
	private val zSpawns = floatArrayOf(+335f, -335f, +335f, -335f)
	//private var maper = ComponentMapper.getFor(StatusComponent::class.java)

	private val random = Random()
	private val randomSpawnIndex: Int
		get() = random.nextInt(xSpawns.size)


	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine) {
		entities = e.getEntitiesFor(Family.all(EnemyComponent::class.java, StatusComponent::class.java).get())
		e.addEntityListener(Family.one(PlayerComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		//TODO: humo donde aparece bicho...
		//if(entities!!.size() < 5) spawnEnemy(randomSpawnIndex)

		if(entities != null)
		for(entity in entities!!)
		{
			///----- MODEL (desapareciendo)
			val model = entity.getComponent(ModelComponent::class.java)
			val status = entity.getComponent(StatusComponent::class.java)
			if(status.isDead() && model.blendingAttribute != null)
			{
				model.blendingAttribute!!.opacity = 1 - status.deathProgres()
				//model.blendingAttribute!!.opacity -= delta / 3
				// model.update(delta)
			}

			///----- ANIMATION
			val animat = entity.getComponent(AnimationComponent::class.java)
			animat?.update(delta)
			/// Particulas
			updateParticulas(entity)

			///----- BULLET
			/// Player position
			val bulletPlayer = player!!.getComponent(BulletComponent::class.java)
			val transf = Matrix4()
			bulletPlayer.rigidBody.getWorldTransform(transf)
			val playerPosition = Vector3()
			transf.getTranslation(playerPosition)
			val orientation = 0f //TODO: Camara?
			//orientation = Math.atan2(-playerPosition.z.toDouble(), playerPosition.x.toDouble()).toFloat()

			//
			EnemyFactory.mover(entity, playerPosition.cpy(), delta)
		}
	}



	//______________________________________________________________________________________________
	var index = 0
	private fun spawnEnemy(randomSpawnIndex: Int) {
		engine!!.addEntity(EnemyFactory.create(
				EnemyComponent.TYPE.MONSTER1,
				Vector3(xSpawns[randomSpawnIndex], 5f, zSpawns[randomSpawnIndex]),
				100f))
				//EntityFactory.createEnemy(model, Vector3(xSpawns[randomSpawnIndex], 5f, zSpawns[randomSpawnIndex]), ++index))
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) { player = entity }
	override fun entityRemoved(entity: Entity) { }

	//______________________________________________________________________________________________
	fun dispose()
	{
		EnemyFactory.dispose()
	}



	//______________________________________________________________________________________________
	private fun updateParticulas(entity : Entity)//TODO: llamar desde Status?
	{
		/*if(entity.getComponent(StatusComponent::class.java).isDead()
			&& entity.getComponent(EnemyDieParticleComponent::class.java)?.used == true)
		{
			entity.getComponent(EnemyDieParticleComponent::class.java).used = true
			val effect = entity.getComponent(EnemyDieParticleComponent::class.java).originalEffect.copy()
			(effect.getControllers().first().emitter as RegularEmitter).emissionMode = RegularEmitter.EmissionMode.EnabledUntilCycleEnd
			effect.setTransform(entity.getComponent(ModelComponent::class.java).instance.transform)
			effect.scale(3.25f, 1f, 1.5f)
			effect.init()
			effect.start()
			RenderSystem.particleSystem.add(effect)
		}*/
	}
}
