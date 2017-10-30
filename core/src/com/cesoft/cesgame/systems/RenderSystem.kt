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
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.components.GunComponent
import com.cesoft.cesgame.components.ModelComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RenderSystem(colorAmbiente: ColorAttribute, assets: Assets) : EntitySystem() {

	private lateinit var entities: ImmutableArray<Entity>
	private var batch: ModelBatch = ModelBatch()
	private val environment: Environment = Environment()
	var perspectiveCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	private var gunCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	lateinit var gun: Entity
	private var isDisposed = false
	//var particleSystem = ParticleSystem()

	//______________________________________________________________________________________________
	init {
		/// Camaras
		//perspectiveCamera.far = Math.sqrt((2*largoMundo*largoMundo).toDouble()).toFloat()+1	// Lado mundo = 4000 => SQRT(4000*4000+4000*4000) = 5700
		perspectiveCamera.far = 12000f // Para que vea el cielo (dome)
		perspectiveCamera.near = 1f
		gunCamera.far = 50f

		/// Particulas
		val billboardParticleBatch = BillboardParticleBatch()
		billboardParticleBatch.setCamera(perspectiveCamera)
		particleSystem.add(billboardParticleBatch)
		assets.iniParticulas(particleSystem.batches)

		/// Ambiente
		//colorAmbiente.color.set( 0.7f, 0.4f, 0.4f, 1f)
		environment.set(colorAmbiente)
		environment.add(DirectionalLight().set(0.7f, 0.4f, 0.2f, -1f, -0.8f, -0.4f))
		//environment.set(ColorAttribute(ColorAttribute.Fog, 0.7f, 0.4f, 0.0f, 1f))
	}

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine?) {
		entities = e!!.getEntitiesFor(Family.all(ModelComponent::class.java).get())
	}

	//______________________________________________________________________________________________
	private var countMax = 0
	override fun update(delta: Float) {
		if(isDisposed)return

		batch.begin(perspectiveCamera)
		var countDrawn = 0
		for(it in entities)
		{
			if(it.getComponent(GunComponent::class.java) == null)
			{
				val model = it.getComponent(ModelComponent::class.java)
				if(model.frustumCullingData.isVisible(perspectiveCamera))
				{
					batch.render(model.instance, environment)
					countDrawn++
				}
			}
		}
		if(countDrawn > countMax)countMax = countDrawn
//System.err.println("-------------------------------RENDER---"+countDrawn+"  / "+countMax)
		batch.end()

		renderParticleEffects()
		drawGun(delta)
	}

	//______________________________________________________________________________________________
	private fun renderParticleEffects() {
		batch.begin(perspectiveCamera)
		particleSystem.update()
		particleSystem.begin()
		particleSystem.draw()
		particleSystem.end()
		batch.render(particleSystem)
		batch.end()
	}

	//______________________________________________________________________________________________
	private var isDrawGunUp = true
	private var yDrawGunOrg = -999f
	private fun drawGun(delta: Float) {
		//TODO: guardar posicion ondulatoria al andar
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
		batch.begin(gunCamera)
		val modelo = gun.getComponent(ModelComponent::class.java)

		animGunRespiracion(modelo, delta)

		batch.render(modelo.instance)
		batch.end()
	}
	//______________________________________________________________________________________________
	private fun animGunRespiracion(modelo: ModelComponent, delta: Float)
	{
		val pos = Vector3()
		modelo.instance.transform.getTranslation(pos)
		if(yDrawGunOrg == -999f)yDrawGunOrg=pos.y
		if(isDrawGunUp) {
			pos.y += delta*2
			if(pos.y > yDrawGunOrg+2.5f)
				isDrawGunUp = false
		}
		else
		{
			pos.y -= delta*2
			if(pos.y < yDrawGunOrg-2.5f)
				isDrawGunUp = true
		}
		modelo.instance.transform.setTranslation(pos)
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

	//______________________________________________________________________________________________
	companion object {
		private val FOV = 67f
		var particleSystem = ParticleSystem()
	}
}