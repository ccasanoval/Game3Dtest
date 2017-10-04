package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.JsonReader
import com.cesoft.cesgame.components.*
import com.cesoft.cesgame.managers.EntityFactory

import java.util.Random

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem : EntitySystem(), EntityListener {
	private var entities: ImmutableArray<Entity>? = null
	private var player: Entity? = null

	private val xSpawns = floatArrayOf(+10f, +10f, -10f, -10f)
	private val zSpawns = floatArrayOf(+10f, -10f, +10f, -10f)
	private var sm = ComponentMapper.getFor(StatusComponent::class.java)

	private val random = Random()
	private val randomSpawnIndex: Int
		get() = random.nextInt(xSpawns.size)

	//______________________________________________________________________________________________
	private val model : Model
	init {
		val modelLoader = G3dModelLoader(JsonReader())
		val modelData = modelLoader.loadModelData(Gdx.files.internal("data/monster.g3dj"))
		model = Model(modelData, TextureProvider.FileTextureProvider())
		val nodes = model.nodes
		for(i in 0 until nodes.size-1)
			nodes[i].scale.scl(0.0039f)
		model.calculateTransforms()
	}

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine) {
		entities = e.getEntitiesFor(Family.all(EnemyComponent::class.java, StatusComponent::class.java).get())
		e.addEntityListener(Family.one(PlayerComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		if(entities!!.size() < 1) spawnEnemy(randomSpawnIndex)

		for(i in 0 until entities!!.size()) {
			val playerPosition = Vector3()
			val enemyPosition = Vector3()
			val e = entities!!.get(i)
			if( ! sm.get(e).alive) return

			val model = e.getComponent(ModelComponent::class.java)
			model.instance.transform.getTranslation(enemyPosition)

			val animat = e.getComponent(AnimationComponent::class.java)
			animat.update(delta)

			val bulletPlayer = player!!.getComponent(BulletComponent::class.java)
			val transf = Matrix4()
			bulletPlayer.rigidBody.getWorldTransform(transf)
			transf.getTranslation(playerPosition)

			//TODO: user AI ¿?
			val dX = playerPosition.x - enemyPosition.x
			val dZ = playerPosition.z - enemyPosition.z

			val bullet = e.getComponent(BulletComponent::class.java)
			bullet.rigidBody.applyCentralForce(Vector3(dX, 0f, dZ))

			val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()

			//Calculate the transforms
			val quat = Quaternion()
			val rot = quat.setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat() + 90)

			bullet.rigidBody.getWorldTransform(transf)
			transf.getTranslation(enemyPosition)
			model.instance.transform.set(enemyPosition.x, enemyPosition.y, enemyPosition.z, rot.x, rot.y, rot.z, rot.w)
			//model.instance.transform.set(enemyPosition.x, enemyPosition.y, enemyPosition.z, rot.x, rot.y, rot.z, rot.w)
		}
	}

	//______________________________________________________________________________________________
	private fun spawnEnemy(randomSpawnIndex: Int) {
		engine!!.addEntity(EntityFactory.createEnemy(model, xSpawns[randomSpawnIndex], 5f, zSpawns[randomSpawnIndex]))
	}

	//______________________________________________________________________________________________
	override fun entityAdded(entity: Entity) {
		player = entity
	}

	//______________________________________________________________________________________________
	override fun entityRemoved(entity: Entity) {}

	//______________________________________________________________________________________________
	fun dispose()
	{
		model.dispose()
	}
}
