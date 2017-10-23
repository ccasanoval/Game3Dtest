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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.components.GunComponent
import com.cesoft.cesgame.components.ModelComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RenderSystem(colorAmbiente: ColorAttribute) : EntitySystem() {

	private lateinit var entities: ImmutableArray<Entity>
	private var batch: ModelBatch = ModelBatch()
	private val environment: Environment = Environment()
	var perspectiveCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	private var gunCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	lateinit var gun: Entity
	private var isDisposed = false
	//val colorAmbiente = ColorAttribute(ColorAttribute.AmbientLight, 0.7f, 0.4f, 0.4f, 1f)

	init {
		/// Camaras
		perspectiveCamera.far = 50000f
		perspectiveCamera.near = 1f
		gunCamera.far = 100f

		/// Luz
		//colorAmbiente.color.set( 0.7f, 0.4f, 0.4f, 1f)
		environment.set(colorAmbiente)
		environment.add(DirectionalLight().set(0.7f, 0.4f, 0.2f, -1f, -0.8f, -0.4f))

		/// Particulas TODO:
		val billboardParticleBatch = BillboardParticleBatch()
		billboardParticleBatch.setCamera(perspectiveCamera)
		particleSystem.add(billboardParticleBatch)
	}

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine?) {
		entities = e!!.getEntitiesFor(Family.all(ModelComponent::class.java).get())
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		if(isDisposed)return
		batch.begin(perspectiveCamera)
		/*entities
			.filter {
				it.getComponent(GunComponent::class.java) == null && isVisible()
			}
			.map {
				it.getComponent(ModelComponent::class.java) }
			.forEach {
				if(true)
				batch.render(it.instance, environment)
			}*/
		var countDrawn = 0
		for(it in entities)
		{
			if(it.getComponent(GunComponent::class.java) == null)
			{
				val model = it.getComponent(ModelComponent::class.java)
				if(isVisible(perspectiveCamera, model))
				{
					batch.render(model.instance, environment)
					countDrawn++
				}
			}
		}
		System.err.println("-------------------------------RENDER---"+countDrawn)
		batch.end()
		renderParticleEffects()
		drawGun()
	}
	//______________________________________________________________________________________________
	// Frustrum culling
	private val pos = Vector3()
	private fun isVisible(cam: PerspectiveCamera, model: ModelComponent): Boolean {
		if(model.isMustShow)return true
		model.instance.transform.getTranslation(pos)
		pos.add(model.center)
		return cam.frustum.sphereInFrustum(pos, model.radius)
		/*model.instance.transform.getTranslation(pos)
		pos.add(model.center)
		return cam.frustum.boundsInFrustum(pos, model.dimensions)*/
	}

	//______________________________________________________________________________________________
	private fun renderParticleEffects() {
		batch.begin(perspectiveCamera)
		particleSystem.update() // technically not necessary for rendering
		particleSystem.begin()
		particleSystem.draw()
		particleSystem.end()
		batch.render(particleSystem)
		batch.end()
	}

	//______________________________________________________________________________________________
	private fun drawGun() {
		//TODO: guardar posicion ondulatoria al andar
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
		batch.begin(gunCamera)
		batch.render(gun.getComponent(ModelComponent::class.java).instance)
		batch.end()
	}

	//______________________________________________________________________________________________
	fun resize(width: Int, height: Int) {
		perspectiveCamera.viewportHeight = height.toFloat()
		perspectiveCamera.viewportWidth = width.toFloat()
		gunCamera.viewportHeight = height.toFloat()
		gunCamera.viewportWidth = width.toFloat()
	}

	//______________________________________________________________________________________________
	fun dispose() {
		isDisposed = true
		batch.dispose()
	}

	companion object {
		private val FOV = 67f
		var particleSystem: ParticleSystem = ParticleSystem.get()
	}
}