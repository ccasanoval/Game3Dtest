package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesgame.components.*
import com.cesoft.cesgame.managers.EnemyFactory

import java.util.Random

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem : EntitySystem(), EntityListener {
	private var entities: ImmutableArray<Entity>? = null
	private var player: Entity? = null

	private val xSpawns = floatArrayOf(+135f, +135f, -135f, -135f)
	private val zSpawns = floatArrayOf(+135f, -135f, +135f, -135f)
	private var sm = ComponentMapper.getFor(StatusComponent::class.java)

	private val random = Random()
	private val randomSpawnIndex: Int
		get() = random.nextInt(xSpawns.size)

	//______________________________________________________________________________________________
	/*private val model : Model
	init {
		val modelLoader = G3dModelLoader(JsonReader())
		val modelData = modelLoader.loadModelData(Gdx.files.internal("monster/monster.g3dj"))
		model = Model(modelData, TextureProvider.FileTextureProvider())
		val nodes = model.nodes
		for(i in 0 until nodes.size-1)
			nodes[i].scale.scl(0.0039f)
		model.calculateTransforms()
	}*/

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine) {
		entities = e.getEntitiesFor(Family.all(EnemyComponent::class.java, StatusComponent::class.java).get())
		e.addEntityListener(Family.one(PlayerComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		//if(entities!!.size() < 2) spawnEnemy(randomSpawnIndex)

		for(i in 0 until entities!!.size()) {
			val playerPosition = Vector3()
			val enemyPosition = Vector3()
			val e = entities!!.get(i)
			if( ! sm.get(e).alive) return

			/// Animacion
			val animat = e.getComponent(AnimationComponent::class.java)
			animat.update(delta)

			/// Particulas
			updateParticulas(e)

			/// Movimiento
			val bulletPlayer = player!!.getComponent(BulletComponent::class.java)
			val transf = Matrix4()
			bulletPlayer.rigidBody.getWorldTransform(transf)
			transf.getTranslation(playerPosition)

			//TODO: user AI ¿?
			val model = e.getComponent(ModelComponent::class.java)
			model.instance.transform.getTranslation(enemyPosition)
			val dX = playerPosition.x - enemyPosition.x
			val dZ = playerPosition.z - enemyPosition.z

			// Fuerzas
			val bullet = e.getComponent(BulletComponent::class.java)
			val fuerza = 70f
			//limitar velocidad??? TODO
			bullet.rigidBody.applyCentralForce(Vector3(dX, 0f, dZ).nor().scl(fuerza))
			//System.err.println("ENEMY FORCE -----------------"+Vector3(dX, 0f, dZ).nor())

			// Orientacion
			val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
			val quat = Quaternion()
			val rot = quat.setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat() + 90)
			bullet.rigidBody.getWorldTransform(transf)
			transf.getTranslation(enemyPosition)
			model.instance.transform.set(enemyPosition.x, enemyPosition.y, enemyPosition.z, rot.x, rot.y, rot.z, rot.w)
		}
	}

	//______________________________________________________________________________________________
	private fun updateParticulas(e : Entity)
	{
		if( ! e.getComponent(StatusComponent::class.java).alive && !e.getComponent(EnemyDieParticleComponent::class.java).used) {
			e.getComponent(EnemyDieParticleComponent::class.java).used = true
			val effect = e.getComponent(EnemyDieParticleComponent::class.java).originalEffect.copy()
			(effect.getControllers().first().emitter as RegularEmitter).emissionMode = RegularEmitter.EmissionMode.EnabledUntilCycleEnd
			effect.setTransform(e.getComponent(ModelComponent::class.java).instance.transform)
			effect.scale(3.25f, 1f, 1.5f)
			effect.init()
			effect.start()
			RenderSystem.particleSystem.add(effect)
		}
	}

	//______________________________________________________________________________________________
	var index = 0
	private fun spawnEnemy(randomSpawnIndex: Int) {
		engine!!.addEntity(EnemyFactory.create(
				EnemyComponent.TYPE.ZOMBIE1,
				Vector3(xSpawns[randomSpawnIndex], 5f, zSpawns[randomSpawnIndex]),
				100f))
				//EntityFactory.createEnemy(model, Vector3(xSpawns[randomSpawnIndex], 5f, zSpawns[randomSpawnIndex]), ++index))
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) {
		player = entity
	}

	//______________________________________________________________________________________________
	override fun entityRemoved(entity: Entity)
	{
	}

	//______________________________________________________________________________________________
	fun dispose()
	{
		EnemyFactory.dispose()
	}
}
