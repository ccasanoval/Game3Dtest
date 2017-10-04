package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesgame.GameWorld
import com.cesoft.cesgame.components.BulletComponent
import com.cesoft.cesgame.components.ModelComponent
import com.cesoft.cesgame.components.ShotComponent
import com.cesoft.cesgame.components.StatusComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by ccasanova on 04/10/2017.
//
class ShotSystem(private val gameWorld: GameWorld) : EntitySystem() {
	private var entities: ImmutableArray<Entity>? = null

	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(Family.all(ShotComponent::class.java).get())
	}

	override fun update(delta: Float) {

		for(entity in entities!!) {

			val shot = entity.getComponent(ShotComponent::class.java)
			shot.update(delta)
			if(shot.aliveTime >= 4f) {
				gameWorld.remove(entity)
				continue
			}

			val world = Matrix4()
			val pos = Vector3()
			val model = entity.getComponent(ModelComponent::class.java)
			val bullet = entity.getComponent(BulletComponent::class.java)

			bullet.rigidBody.getWorldTransform(world)
			world.getTranslation(pos)
			model.instance.transform.translate(pos)


		}
	}
}