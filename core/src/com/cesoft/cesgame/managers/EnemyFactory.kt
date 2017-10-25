package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.*
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.cesoft.cesgame.CesGame

import com.cesoft.cesgame.components.EnemyComponent.ACTION.*


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object EnemyFactory
{
	private val RADIO = 15f
	private val FUERZA_DESKTOP = 2000f
	private val FUERZA_MOBILE = 0f

	//private val modelLoaderJSON = G3dModelLoader(JsonReader())
	private val modelLoader = G3dModelLoader(UBJsonReader())
	private val models = mutableMapOf<EnemyComponent.TYPE, Model>()

	private val modelDataMonster1 = modelLoader.loadModelData(Gdx.files.internal("foes/monster1/a.g3db"))


	//______________________________________________________________________________________________
	fun dispose()
	{
		for((_, model) in models)
			model.dispose()
		models.clear()
	}

	//______________________________________________________________________________________________
	private fun createModel(type: EnemyComponent.TYPE): Model {
		val model: Model
		when(type) {
			EnemyComponent.TYPE.MONSTER1 -> {
				model = Model(modelDataMonster1)
				//model = modelLoaderJSON.loadModel(Gdx.files.getFileHandle("foes/monster1/monster.g3dj", Files.FileType.Internal))
//				for(i in 0 until model.nodes.size - 1)
//					model.nodes[i].scale.scl(0.01f)
			}
		}
		return model
	}

	//______________________________________________________________________________________________
	fun create(type: EnemyComponent.TYPE, pos: Vector3, mase: Float = 100f) : Entity
	{
		val entity = Entity()

		/// ENEMY
		val enemy = EnemyComponent(type)
		entity.add(enemy)

		/// MODEL
		if(models[type] == null)
			models[type] = createModel(type)
		//
		val modelComponent: ModelComponent
		when(type) {
			EnemyComponent.TYPE.MONSTER1 -> {
				modelComponent = ModelComponent(models[type]!!, pos)
				modelComponent.frustumCullingData =
					FrustumCullingData.create(Vector3(0f,0f,0f), Vector3(RADIO,RADIO,RADIO), modelComponent.instance)
				entity.add(modelComponent)
				/// ANIMATION
				entity.add(AnimationComponent(modelComponent.instance))
				setAnimation(entity, EnemyComponent.ACTION.WALKING)
				//TODO: Peta al reiniciar
//				val am = AssetManager()
//				entity.add(EnemyDieParticleComponent(RenderSystem.particleSystem, am))
//				am.dispose()
			}
		}
		// (desaparecer)
		if(modelComponent.instance.materials.size > 0) {
			val material = modelComponent.instance.materials.get(0)
			val blendingAttribute = BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
			material.set(blendingAttribute)
			modelComponent.blendingAttribute = blendingAttribute
		}

		/// STATUS
		val stat = StatusComponent(entity)
		stat.setWalking()
		entity.add(stat)
		//setWalkin()

		/// COLLISION
		val localInertia = Vector3()
		val shape = btSphereShape(RADIO)//btBoxShape(Vector3(diametro, diametro, diametro))//btCylinderShape(Vector3(14f,5f,14f))// btCapsuleShape(3f, 6f)
		shape.calculateLocalInertia(mase, localInertia)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(mase, null, shape, localInertia)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter = BulletComponent.SHOT_FLAG or BulletComponent.PLAYER_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.ENEMY_FLAG
		rigidBody.userValue = BulletComponent.ENEMY_FLAG
		//rigidBody.userIndex = index
		rigidBody.friction = 0f
		rigidBody.rollingFriction = 1000000f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		/// STEERIN
		entity.add(SteeringComponent(rigidBody, RADIO))

		return entity
	}

	//______________________________________________________________________________________________
	fun setAnimation(entity: Entity, action: EnemyComponent.ACTION)
	{
		val type = entity.getComponent(EnemyComponent::class.java).type
		val animParams = getAnimationParams(type, action)
		if(animParams.id.isEmpty())return
		entity.getComponent(AnimationComponent::class.java).animate(animParams)
	}
	//______________________________________________________________________________________________
	private val random = java.util.Random()
	private fun getAnimationParams(type: EnemyComponent.TYPE, action: EnemyComponent.ACTION) : AnimationParams
	{
		val loop = -1	//Continuously = -1 , OnlyOnce = 0
		val speed = 1f
		val time = getActionDuration(type, action)
		when(type) {
			EnemyComponent.TYPE.MONSTER1 ->
				return when(action) {//TODO: Acer estos objetos staticos!!! asi no tienes que crearlos....
					EnemyComponent.ACTION.WALKING ->
						AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 0f, time)
					EnemyComponent.ACTION.RUNNING ->
						AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 6f, time)
					EnemyComponent.ACTION.ATTACKING -> {
						when(random.nextInt(2))
						{
							0 -> AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 10f, time)
							else -> AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 12.8f, time)
						}
					}
					EnemyComponent.ACTION.IDLE ->
						AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 19.12f, time)
					EnemyComponent.ACTION.REINCARNATING ->
						AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 0f, time)
					EnemyComponent.ACTION.ACHING -> {
						if(random.nextInt(2) == 0)
							AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 15.6f, time)
						else
							AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 20f, time)
					}
					EnemyComponent.ACTION.DYING -> {
						AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 22.6f, time)

						/*if(random.nextInt(2) == 0) {
							System.err.println("----******************--------- Enemy Factory DYING 1")
							AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 15.6f, time)
						}
						else {
							System.err.println("-----*********************-------- Enemy Factory DYING 2")
							AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 20f, time)
						}*/
					}
				}
				/*
				0-30  walk				0-1.2
				0-120 walk				0-4.8
				150-190 run				6-7.6
				150-210 run				6-8.4
				250-333 attack-01		10-13.32
				320-400 attack-02		12.8-16
				390-418 death-01		15.6-16.72
				478-500 growl			19.12-20
				500-550 death-02		20-22
				565-650 death-03		22.6-26
				//
				650 --> 26s   ==> 25 fps */
		}
	}
	//______________________________________________________________________________________________
	private object ActDuration
	{
		val actionDuration = mapOf(
				WALKING to 4.8f,
				RUNNING to 2.4f,
				ATTACKING to 3.15f,
				IDLE to 0.88f,
				REINCARNATING to 26f,//TODO
				ACHING to 2.5f,
				DYING to 3.4f
		)
	}
	private val typeActionDuration = mapOf(EnemyComponent.TYPE.MONSTER1 to ActDuration)
	fun getActionDuration(type: EnemyComponent.TYPE, action: EnemyComponent.ACTION) =
		typeActionDuration[type]!!.actionDuration[action]!!


	//______________________________________________________________________________________________
	//TODO: IA !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	fun mover(entity: Entity, playerPosition: Vector3, playerOrientation: Vector3, delta: Float)
	{
		val bullet = entity.getComponent(BulletComponent::class.java)
		val status = entity.getComponent(StatusComponent::class.java)
//System.err.println("-------------------------MOVER: ")

		/*if(status.isDead() || status.isAching()) {
			//bullet.rigidBody.linearVelocity = Vector3(0f,0f,0f)
			return
		}*/

		/// Steering
		/*val steering = entity.getComponent(SteeringComponent::class.java)
		val orientation = Math.atan2(-playerOrientation.z.toDouble(), playerOrientation.x.toDouble()).toFloat()
		val target = BulletLocation(playerPosition, orientation)
		val seekSB = Seek<Vector3>(steering, target)
		val res : SteeringAcceleration<Vector3> = steering.procesar(seekSB)
		System.err.println("-------------- PlayerSystem: update: res.linear="+res.linear)
		System.err.println("-------------- PlayerSystem: update: res.angular="+res.angular)
*/
		///
		val enemyPosition = Vector3()
		val model = entity.getComponent(ModelComponent::class.java)
		model.instance.transform.getTranslation(enemyPosition)
		val dX = playerPosition.x - enemyPosition.x
		val dZ = playerPosition.z - enemyPosition.z

		var fuerza = 0f
		val distanciaConPlayer = enemyPosition.dst(playerPosition)

		/// No está en condiciones de atacar
		if(status.isAching() || status.isDead())
			fuerza = 0f
		/// Esta al lado, atacale (Las colisiones no valen, porque aqui ignoro el estado)
		else if(distanciaConPlayer < RADIO+PlayerComponent.RADIO+2)
		{
			status.setAttacking()
			val pain = 20f
			PlayerComponent.hurt(delta * pain)
		}
		/// Esta cerca, corre a por el
		else if(distanciaConPlayer < 180f)
		{
			fuerza = if(CesGame.isMobile) 1800f else 2200f
			status.setRunning()
		}
		/// Esta lejos, camina buscando
		else
		{
			//TODO: Wandering ?
			fuerza = if(CesGame.isMobile) 600f else 800f
			status.setWalking()
		}

		val dir = playerPosition.add(enemyPosition.scl(-1f)).nor().scl(fuerza*delta)
		dir.y = bullet.rigidBody.linearVelocity.y
		bullet.rigidBody.linearVelocity = dir

		val transf = Matrix4()
		bullet.rigidBody.getWorldTransform(transf)
		transf.getTranslation(enemyPosition)

		//if( !status.isAching() && !status.isDead())	{
			// Orientacion
			val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
			val rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat())
			model.instance.transform.set(enemyPosition, rot)
		/*}
		else
		{
			// Orientacion
			val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
			val rot = Quaternion().setFromAxis(0f, 1f, 0f, 0f)
			model.instance.transform.set(enemyPosition, rot)
		}*/
	}


	//______________________________________________________________________________________________
	fun playRunning(entity: Entity) {
		setAnimation(entity, EnemyComponent.ACTION.RUNNING)
	}
	//______________________________________________________________________________________________
	fun playWalking(entity: Entity) {
		setAnimation(entity, EnemyComponent.ACTION.WALKING)
	}
	//______________________________________________________________________________________________
	fun playAttack(entity: Entity) {
		setAnimation(entity, EnemyComponent.ACTION.ATTACKING)
	}
	//______________________________________________________________________________________________
	fun playAching(entity: Entity) {
		setAnimation(entity, EnemyComponent.ACTION.ACHING)
	}
	//______________________________________________________________________________________________
	fun playDying(entity: Entity) {
		setAnimation(entity, EnemyComponent.ACTION.DYING)
	}

}