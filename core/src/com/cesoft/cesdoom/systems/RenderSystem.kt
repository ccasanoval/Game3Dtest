package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.entities.Gun
import com.cesoft.cesdoom.events.RenderEvent
import com.cesoft.cesdoom.events.RenderQueue
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RenderSystem(eventSignal: Signal<RenderEvent>, color: ColorAttribute, private val assets: Assets): EntitySystem() {

	companion object {
	    private val tag: String = RenderSystem::class.java.simpleName
		private const val FOV = 67f
	}
	private val renderQueue = RenderQueue()

	private lateinit var entities: ImmutableArray<Entity>
	private var batch: ModelBatch = ModelBatch()
	private val environment: Environment = Environment()
	var perspectiveCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
	private var gunCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
	lateinit var gun: Gun
	private var isDisposed = false
	//private val shadowLight: DirectionalShadowLight


	//______________________________________________________________________________________________
	init {
		Log.e(tag, "INI ---------------------------------------------------------")

		/// Events
		eventSignal.add(renderQueue)

		/// Camaras
		perspectiveCamera.far = 12000f // Para que vea el cielo (dome)
		perspectiveCamera.near = 1f

		gunCamera.far = 50f

		/// Particulas
		assets.iniParticleEffectPool(perspectiveCamera)

		/// Ambiente
		environment.set(color)
		environment.add(DirectionalLight().set(0.7f, 0.3f, 0.1f, -1f, -0.8f, -0.4f))
		//environment.set(ColorAttribute(ColorAttribute.Fog, 0.1f, 0.3f, 0.1f, 1f))
		/// Sombras
//		shadowLight = DirectionalShadowLight(1024 * 5, 1024 * 5, 200f, 200f, 1f, 300f)
//		shadowLight.set(0.9f, 0.9f, 0.9f, 0f, -0.1f, 0.1f)
//		environment.add(shadowLight)
//		environment.shadowMap = shadowLight

	}

	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine?) {
		entities = e!!.getEntitiesFor(Family.all(ModelComponent::class.java).get())
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		if(isDisposed)return

		processEvents()

		batch.begin(perspectiveCamera)
		for(entity in entities) {
			if(entity is Gun)continue
			val model = ModelComponent.get(entity)
			try {
				if(model.frustumCullingData.isVisible(perspectiveCamera)) {
					batch.render(model.instance, environment)
					//countDrawn++
				}
			}
			catch(e: Exception) {
				Log.e(tag, "RenderSystem:update:e:$model:$e")
			}
		}
		batch.end()

		if( ! Status.paused) {
			//drawShadows(delta)
			renderParticleEffects()
		}

		drawGun(delta)
	}

	//______________________________________________________________________________________________
	private fun renderParticleEffects() {
		batch.begin(perspectiveCamera)
		assets.getParticleSystem()?.let {
			it.update()
			it.begin()
			it.draw()
			it.end()
			batch.render(it)
		}
		batch.end()
	}

	//______________________________________________________________________________________________
	private var isDrawGunUp = true
	private var yDrawGunOrg = -999f
	private var isDrawGunSwitch = true
	private var xDrawGunOrg = Float.MIN_VALUE
	private fun drawGun(delta: Float) {
		if(PlayerComponent.isDead())return
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
		batch.begin(gunCamera)
		val model = ModelComponent.get(gun)
		if(PlayerComponent.isWalking) {
			animGunWalking(model, delta)
		}
		else {
			animGunBreathing(model, delta)
			restoreGunPosition(model, delta)
		}
		batch.render(model.instance)
		batch.end()
	}
	//______________________________________________________________________________________________
	private val posTemp = Vector3()
	private fun animGunBreathing(model: ModelComponent, delta: Float) {
		model.instance.transform.getTranslation(posTemp)
		if(yDrawGunOrg == -999f)yDrawGunOrg=posTemp.y
		if(isDrawGunUp) {
			posTemp.y += delta*2
			if(posTemp.y > yDrawGunOrg+2.5f)
				isDrawGunUp = false
		}
		else {
			posTemp.y -= delta*2
			if(posTemp.y < yDrawGunOrg-2.5f)
				isDrawGunUp = true
		}
		model.instance.transform.setTranslation(posTemp)
	}
	private fun animGunWalking(model: ModelComponent, delta: Float) {
		model.instance.transform.getTranslation(posTemp)
		if(xDrawGunOrg == Float.MIN_VALUE)
			xDrawGunOrg = posTemp.x
		if(isDrawGunSwitch) {
			posTemp.x += delta*7
			if(posTemp.x > xDrawGunOrg+4f)
				isDrawGunSwitch = false
		}
		else {
			posTemp.x -= delta*7
			if(posTemp.x < xDrawGunOrg-4f)
				isDrawGunSwitch = true
		}
		model.instance.transform.setTranslation(posTemp)
	}
	private fun restoreGunPosition(model: ModelComponent, delta: Float) {
		if(xDrawGunOrg == Float.MIN_VALUE || Math.abs(xDrawGunOrg - posTemp.x) < 0.2) return
		animGunWalking(model, delta/2)
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
//			//if(entities.get(x).getComponent(AnimationComponent::class.java) != null && !Status.paused)
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
		gun.dispose()
		batch.dispose()
		isDisposed = true
	}

	//______________________________________________________________________________________________
	private fun processEvents() {
		for(event in renderQueue.events) {
			when(event.type) {
				RenderEvent.Type.SET_AMBIENT_COLOR -> setAmbientColor(event.param as ColorAttribute)
				RenderEvent.Type.ADD_PARTICLE_FX -> addParticleEffect(event.param as ParticleEffect)
				//else -> Unit
			}
		}
	}
	private fun setAmbientColor(color: ColorAttribute) {
		environment.set(color)
	}
	private fun addParticleEffect(effect: ParticleEffect) {
		effect.init()
		effect.start()
		assets.getParticleSystem()?.add(effect)
	}
}