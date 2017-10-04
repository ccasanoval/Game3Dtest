package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.components.GunComponent
import com.cesoft.cesgame.components.ModelComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RenderSystem : EntitySystem() {

	private var entities: ImmutableArray<Entity>? = null
	private var batch: ModelBatch = ModelBatch()
	private val environment: Environment = Environment()
	var perspectiveCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	private var gunCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	var gun: Entity? = null

	init {
		perspectiveCamera.far = 10000f
		perspectiveCamera.near = 1f

		gunCamera.far = 100f

		environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f))
	}

	// Event called when an entity is added to the engine
	override fun addedToEngine(e: Engine?) {
		// Grabs all entities with desired components
		entities = e!!.getEntitiesFor(Family.all(ModelComponent::class.java).get())
	}

	override fun update(delta: Float) {
		batch.begin(perspectiveCamera)
		for(i in 0 until entities!!.size()) {
			if(entities!!.get(i).getComponent(GunComponent::class.java) == null) {
				val mod = entities!!.get(i).getComponent(ModelComponent::class.java)
				batch.render(mod.instance, environment)
			}
		}
		batch.end()
		drawGun()
	}

	private fun drawGun() {
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
		batch.begin(gunCamera)
		batch.render(gun!!.getComponent(ModelComponent::class.java).instance)
		batch.end()
	}

	fun resize(width: Int, height: Int) {
		perspectiveCamera.viewportHeight = height.toFloat()
		perspectiveCamera.viewportWidth = width.toFloat()
		gunCamera.viewportHeight = height.toFloat()
		gunCamera.viewportWidth = width.toFloat()
	}

	fun dispose() {
		batch.dispose()
	}

	companion object {
		private val FOV = 67f
	}
}