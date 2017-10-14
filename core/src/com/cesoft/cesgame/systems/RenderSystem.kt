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
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.components.GunComponent
import com.cesoft.cesgame.components.ModelComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RenderSystem : EntitySystem() {

	private lateinit var entities: ImmutableArray<Entity>
	private var batch: ModelBatch = ModelBatch()
	private val environment: Environment = Environment()
	var perspectiveCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	private var gunCamera: PerspectiveCamera = PerspectiveCamera(FOV, CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
	lateinit var gun: Entity

	init {
		/// Camaras
		perspectiveCamera.far = 50000f
		perspectiveCamera.near = 1f
		gunCamera.far = 100f

		/// Luz
		environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.7f, 0.7f, 0.7f, 1f))
		environment.add(DirectionalLight().set(0.4f, 0.7f, 0.4f, -1f, -0.8f, -0.2f))

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
		batch.begin(perspectiveCamera)
		entities
			.filter { it.getComponent(GunComponent::class.java) == null }
			.map { it.getComponent(ModelComponent::class.java) }
			.forEach { batch.render(it.instance, environment) }
		batch.end()
		renderParticleEffects()
		drawGun()
		//drawLaser()
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
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
		batch.begin(gunCamera)
		batch.render(gun.getComponent(ModelComponent::class.java).instance)
		batch.end()
	}

	//______________________________________________________________________________________________
	//______________________________________________________________________________________________
	/*private var shapeRenderer: ShapeRenderer
	private var spriteBatch: SpriteBatch

	private var startBackground: TextureRegion
	private var startOverlay: TextureRegion
	private var midBackground: TextureRegion
	private var midOverlay: TextureRegion
	private var endBackground: TextureRegion
	private var endOverlay: TextureRegion
	private var animation: TextureRegion

	private var rotation: Float = 0.toFloat()
	private var distance: Float = 0.toFloat()
	var isFiring: Boolean = false
	private var laserDrawnTill: Long = 0
	private var duration: Long = 0
	private var tracker = 0f

	init{
		shapeRenderer = ShapeRenderer()
		spriteBatch = SpriteBatch()
		startBackground = TextureRegion(Texture("laser/start/background.png"))
		startOverlay = TextureRegion(Texture("laser/start/overlay.png"))

		midBackground = TextureRegion(Texture("laser/middle/background.png"))
		midOverlay = TextureRegion(Texture("laser/middle/overlay.png"))

		endBackground = TextureRegion(Texture("laser/end/background.png"))
		endOverlay = TextureRegion(Texture("laser/end/overlay.png"))

		animation = TextureRegion(Texture("laser/overlay-animation.png"))

		isFiring = true
		duration = 4000
		laserDrawnTill = 0
	}
	private fun drawLaser()
	{
		shapeRenderer.setProjectionMatrix(perspectiveCamera.combined)
		spriteBatch.setProjectionMatrix(perspectiveCamera.combined)

		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)

		spriteBatch.begin()

//        y=Asin(2π(k+o)/p)+b
//        A is the amplitude of the sine wave.
//        p is the number of time samples per sine wave period.
//                k is a repeating integer value that ranges from 0 to p–1.
//        o is the offset (phase shift) of the signal.
//                b is the signal bias.
//        float val = (float)( Math.sin(tracker * 0.1) + 1) / 2;
//        val = MathUtils.clamp(val, 0.1f, 0.9f);

		if(System.currentTimeMillis() > laserDrawnTill) {
			isFiring = false
		}

		if(isFiring) {

			val posGun = Vector3()
			gun!!.getComponent(ModelComponent::class.java).instance.transform.getTranslation(posGun)
            val posGun2D = gunCamera.project(posGun)
			val posEye = perspectiveCamera.direction
			val posEye2D = perspectiveCamera.project(posEye)

			val timeleft = laserDrawnTill - System.currentTimeMillis()
			var decay = 1.0f

			if(timeleft < duration - 100)
				decay = Interpolation.linear.apply(0.0f, 1.0f, timeleft.toFloat() / (duration - 100))

			// overlay
			spriteBatch.setColor(1f, 0.78f, 0f, decay)
			spriteBatch.draw(startOverlay,
					posGun2D.x - startBackground.getRegionWidth(), posGun2D.y - startBackground.getRegionHeight(),
					(startOverlay.getRegionWidth() / 2).toFloat(), (startOverlay.getRegionHeight() / 2).toFloat(),
					startOverlay.getRegionWidth().toFloat(), startOverlay.getRegionHeight().toFloat(),
					1.0f, 1.0f, rotation)

			spriteBatch.draw(midOverlay,
					posGun2D.x - startBackground.getRegionWidth(), posGun2D.y + startBackground.getRegionHeight() - startBackground.getRegionHeight(),
					(midOverlay.getRegionWidth() / 2).toFloat(), (-(midOverlay.getRegionHeight() / 2)).toFloat(),
					midOverlay.getRegionWidth().toFloat(), distance,
					1.0f, 1.0f, rotation)

			spriteBatch.draw(endOverlay,
					posGun2D.x - startBackground.getRegionWidth(), posGun2D.y + endOverlay.getRegionHeight().toFloat() + distance - startBackground.getRegionHeight(),
					(endOverlay.getRegionWidth() / 2).toFloat(), -(endOverlay.getRegionHeight() / 2 + distance),
					endOverlay.getRegionWidth().toFloat(), endOverlay.getRegionHeight().toFloat(),
					1.0f, 1.0f, rotation)

			tracker += 0.6f
			val foo = MathUtils.lerp(0.5f, 1.0f, tracker)
			var fade = Interpolation.sine.apply(0.9f, 1.0f, foo)

			if(fade - (1 - decay) < 0.01f) {
				fade = decay
			}
			else {
				fade -= (1 - decay)
			}

			// beam
			spriteBatch.setColor(0f, 0f, 1f, fade)
			spriteBatch.draw(startBackground,
					posGun2D.x - startBackground.getRegionWidth(), posGun2D.y - startBackground.getRegionHeight(),
					(startBackground.getRegionWidth() / 2).toFloat(), (startBackground.getRegionHeight() / 2).toFloat(),
					startBackground.getRegionWidth().toFloat(), startBackground.getRegionHeight().toFloat(),
					1.0f, 1.0f, rotation)
			spriteBatch.draw(midBackground,
					posGun2D.x - startBackground.getRegionWidth(), posGun2D.y + startBackground.getRegionHeight() - startBackground.getRegionHeight(),
					(midBackground.getRegionWidth() / 2).toFloat(), (-(startBackground.getRegionHeight() / 2)).toFloat(),
					midBackground.getRegionWidth().toFloat(), distance,
					1.0f, 1.0f, rotation)
			spriteBatch.draw(endBackground,
					posGun2D.x - startBackground.getRegionWidth(), posGun2D.y + endBackground.getRegionHeight().toFloat() + distance - startBackground.getRegionHeight(),
					(endBackground.getRegionWidth() / 2).toFloat(), -(endBackground.getRegionHeight() / 2 + distance),
					endBackground.getRegionWidth().toFloat(), endBackground.getRegionHeight().toFloat(),
					1.0f, 1.0f, rotation)

		}

		spriteBatch.end()
	}*/
	//______________________________________________________________________________________________


	//______________________________________________________________________________________________
	fun resize(width: Int, height: Int) {
		perspectiveCamera.viewportHeight = height.toFloat()
		perspectiveCamera.viewportWidth = width.toFloat()
		gunCamera.viewportHeight = height.toFloat()
		gunCamera.viewportWidth = width.toFloat()
	}

	//______________________________________________________________________________________________
	fun dispose() {
		batch.dispose()
		//spriteBatch.dispose()
	}

	companion object {
		private val FOV = 67f
		var particleSystem: ParticleSystem = ParticleSystem.get()
	}
}