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
import com.cesoft.cesgame.components.*
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.cesoft.cesgame.RenderUtils.OcclusionCuller
import com.cesoft.cesgame.RenderUtils.OcclusionBuffer



////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RenderSystem(colorAmbiente: ColorAttribute, assets: Assets, private val bulletSystem: BulletSystem) : EntitySystem() {

	private lateinit var entities: ImmutableArray<Entity>
	private var batch: ModelBatch = ModelBatch()
	private val environment: Environment = Environment()
	var perspectiveCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	private var gunCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	lateinit var gun: Entity
	private var isDisposed = false
	//private val shadowLight: DirectionalShadowLight

	// For occlusion culling
	private var oclBuffer: OcclusionBuffer
	private var occlusionCuller: OcclusionCuller
	//private var frustumCam: PerspectiveCamera
	private val OCL_BUFFER_EXTENTS = intArrayOf(128, 256, 512, 32, 64)
	val visibleEntities = arrayListOf<Entity?>()

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
		environment.set(colorAmbiente)
		environment.add(DirectionalLight().set(0.7f, 0.4f, 0.2f, -1f, -0.8f, -0.4f))
		/// Sombras
//		shadowLight = DirectionalShadowLight(1024 * 5, 1024 * 5, 200f, 200f, 1f, 300f)
//		shadowLight.set(0.9f, 0.9f, 0.9f, 0f, -0.1f, 0.1f)
//		environment.add(shadowLight)
//		environment.shadowMap = shadowLight

		oclBuffer = OcclusionBuffer(OCL_BUFFER_EXTENTS[0], OCL_BUFFER_EXTENTS[0])
		occlusionCuller = object : OcclusionCuller() {
			override fun isOccluder(obj: btCollisionObject): Boolean {
				//System.err.println("OcclusionCuller : isOccluder ---"+(obj.collisionFlags and CF_OCCLUDER_OBJECT != 0))
				return obj.collisionFlags and CF_OCCLUDER_OBJECT != 0
			}
			override fun onObjectVisible(obj: btCollisionObject) {
				val entity = obj.userData as Entity
				//val model = entity.getComponent(ModelComponent::class.java)
				visibleEntities.add(entity)
				//System.err.println("OcclusionCuller : onObjectVisible : -----------")
			}
		}
//		frustumCam = PerspectiveCamera(FRUSTUM_CAMERA_FOV, perspectiveCamera.viewportWidth, perspectiveCamera.viewportHeight)
//		frustumCam.far = FRUSTUM_CAMERA_FAR
//		frustumCam.update(true)
	}

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine?) {
		entities = e!!.getEntitiesFor(Family.all(ModelComponent::class.java).get())
	}

	//______________________________________________________________________________________________
	private var countMax = 0
	override fun update(delta: Float) {
		if(isDisposed)return

		/// Occlusion Culling
		visibleEntities.clear()
		oclBuffer.clear()
		occlusionCuller.performOcclusionCulling(bulletSystem.broadphase, oclBuffer, perspectiveCamera)

		batch.begin(perspectiveCamera)
		var countDrawn = 0
		System.err.println("RenderSystem:update:NUM-----------"+entities.size()+"-------------"+visibleEntities.size)
		//for(it in entities)
		for(it in visibleEntities)
		{
			//if(it == null)continue
			if(it!!.getComponent(GunComponent::class.java) == null)
			{
				val model = it.getComponent(ModelComponent::class.java) ?: continue
				try
				{
					if(model.frustumCullingData.isVisible(perspectiveCamera)) {
						batch.render(model.instance, environment)
						countDrawn++
					}
				}catch(e: Exception){System.err.println("RenderSystem:update:e:-----------"+model+"-------------"+e)}
			}
		}
		if(countDrawn > countMax)countMax = countDrawn
System.err.println("-------------------------------RENDER---"+countDrawn+"  / "+countMax)
		batch.end()

		//drawShadows(delta)
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
	private val posTemp = Vector3()
	private fun animGunRespiracion(modelo: ModelComponent, delta: Float)
	{
		modelo.instance.transform.getTranslation(posTemp)
		if(yDrawGunOrg == -999f)yDrawGunOrg=posTemp.y
		if(isDrawGunUp) {
			posTemp.y += delta*2
			if(posTemp.y > yDrawGunOrg+2.5f)
				isDrawGunUp = false
		}
		else
		{
			posTemp.y -= delta*2
			if(posTemp.y < yDrawGunOrg-2.5f)
				isDrawGunUp = true
		}
		modelo.instance.transform.setTranslation(posTemp)
	}

	//______________________________________________________________________________________________
//	private fun drawShadows(delta: Float) {
//		shadowLight.begin(Vector3.Zero, perspectiveCamera.direction)
//		batch.begin(shadowLight.camera)
//		for(x in 0 until entities.size()) {
//			//if(entities.get(x).getComponent(PlayerComponent::class.java) != null ||
//			if(entities.get(x).getComponent(EnemyComponent::class.java) != null)
//			{
//				val model = entities.get(x).getComponent(ModelComponent::class.java)
//				if(model.frustumCullingData.isVisible(perspectiveCamera))
//					batch.render(model.instance)
//			}
//			//if(entities.get(x).getComponent(AnimationComponent::class.java) != null && !Settings.paused)
//			//	entities.get(x).getComponent(AnimationComponent::class.java).update(delta)
//		}
//		batch.end()
//		shadowLight.end()
//	}

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
		visibleEntities.clear()
	}

	//______________________________________________________________________________________________
	companion object {
		private val FOV = 67f
		var particleSystem = ParticleSystem()

		val CF_OCCLUDER_OBJECT: Int = 512
	}
}