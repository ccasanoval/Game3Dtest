package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemyFactory(assets: Assets) {

    companion object {
        private val tag: String = EnemyFactory::class.java.simpleName
        private const val MAX_ENEMIES = 4
        private const val SPAWN_DELAY = 5*1000	//TODO: si pausa o background, debe actualizar time!!!
        private val random = java.util.Random()
    }

    lateinit var enemies: ImmutableArray<Entity>
    private val allEnemies = ArrayList<Enemy>()
    init {
//        val models = arrayListOf<Model>()
//        models.add(assets.getEnemy(EnemyComponent.TYPE.MONSTER0))
//        models.add(assets.getEnemy(EnemyComponent.TYPE.MONSTER1))
        if(allEnemies.size < MAX_ENEMIES) {
            for(i in allEnemies.size until MAX_ENEMIES) {
                //val enemy = createEnemy(i, models[random.nextInt(2)], assets.newParticleEffect())
                val type = when(random.nextInt(2)) {
                    //1 -> EnemyComponent.TYPE.MONSTER1
                    else -> EnemyComponent.TYPE.MONSTER1
                }
                val enemy = createEnemy(i, assets, type)
                Log.e(tag, "ini -------------------------------------------------- $enemy : $i $type")
                allEnemies.add(enemy)
            }
        }
    }

    private fun getNextEnemy(id: Int): Enemy {
        val enemy = allEnemies[id]
        Log.e(tag, "getNextEnemy $id -------------------------- $enemy")
        val pos = when(countSpawnPosition++ % 4) {//TODO:Random
            0 ->    Vector3(+250f, 150f, +250f)
            1 ->    Vector3(+250f, 150f, -250f)
            2 ->    Vector3(-250f, 150f, +250f)
            else -> Vector3(-250f, 150f, -250f)
        }
        resetEntity(enemy, pos)
        return enemy
    }

    private fun isEnemyActive(id: Int): Boolean {
        for(i in 0 until enemies.size()) {
            if((enemies[i] as Enemy).id == id)
                return true
        }
        return false
    }

    //https://blog.egorand.me/concurrency-primitives-in-kotlin/
    //@Volatile private var spawning = false
    private var countSpawnPosition = 0
    private var lastSpawn = System.currentTimeMillis()
    fun spawnIfNeeded(engine: Engine) {
        if(Status.paused) lastSpawn = System.currentTimeMillis()
        if(System.currentTimeMillis() < lastSpawn + SPAWN_DELAY) return
        lastSpawn = System.currentTimeMillis()

        if(enemies.size() < MAX_ENEMIES) {
            val id = getNextEnemyId()
            if(id >= allEnemies.size) return//Max enemy number reached
            try {
                val enemy = getNextEnemy(id)
                engine.addEntity(enemy)
            } catch (e: Throwable) {//TODO: on pause, reset timer...
                Log.e(tag, "spawnIfNeeded:e:$e")
            }//TODO: check before fail
        }
    }

    private fun getNextEnemyId(): Int {
        var id = allEnemies.size
        if(enemies.size() == 0) {
            id = 0
        }
        else {
            for(id0 in 0 until MAX_ENEMIES) {
                if( ! isEnemyActive(id0)) {
                    id = id0
                    break
                }
            }
        }
        return id
    }


    private fun resetEntity(entity: Entity, position: Vector3) {
        /// Components
        resetComponents(entity)
        /// Model
        val model = ModelComponent.get(entity)
        if (model.blendingAttribute != null)
            model.blendingAttribute!!.opacity = 1f
        val rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(0.0).toFloat())
        model.instance.transform.set(position, rot)

        /// Collision
        val bullet = BulletComponent.get(entity)
        bullet.rigidBody.linearVelocity = Vector3.Zero
        val transf = Matrix4()
        transf.setTranslation(position)
        bullet.rigidBody.worldTransform = transf
        /// Animation
        EnemyActions.setAnimation(entity, EnemyComponent.ACTION.WALKING)
    }


    private fun resetComponents(entity: Entity) {
        val status = StatusComponent.get(entity)
        status.alive = false
        status.isSaltando = true
        status.estado = EnemyComponent.ACTION.WALKING
        status.health = 100f
        status.deadStateTime = 0f
        status.achingStateTime = 0f

        val enemy = EnemyComponent.get(entity)
        enemy.currentAnimation = EnemyComponent.ACTION.WALKING
        enemy.posTemp
    }


    private fun createEnemy(
            id: Int,
            assets: Assets,
            //model: Model,
            //particleEffect: ParticleEffect,
            type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1,
            mass: Float = EnemyComponent.MASS
            ): Enemy {

        val entity = Enemy(id)//enemyPool.obtain()

        /// Enemy Component
        val enemy = EnemyComponent(type)
        entity.add(enemy)

        /// Status Component
        val stat = StatusComponent()
        entity.add(stat)

        /// Model
        val model = assets.getEnemy(type)
        val modelComponent: ModelComponent

//        Log.e(tag, "CREATE-------------- ANIMS:")
//        for(anim in model.animations)
//            Log.e(tag, "CREATE-------------- ${anim.id} / ${anim.duration}")

        //when (type) {
        //    EnemyComponent.TYPE.MONSTER1 -> {
                modelComponent = ModelComponent(model, Vector3.Zero)
                //modelComponent.frustumCullingData =
                //	FrustumCullingData.create(Vector3(0f,0f,0f), Vector3(RADIO,RADIO,RADIO), modelComponent.instance)
                entity.add(modelComponent)
                /// ANIMATION
                entity.add(AnimationComponent(modelComponent.instance))
        //    }
        //}

        // Evanesce Effect
        if (modelComponent.instance.materials.size > 0) {
            val material = modelComponent.instance.materials.get(0)
            val blendingAttribute = BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            material.set(blendingAttribute)
            modelComponent.blendingAttribute = blendingAttribute
        }

        // Particle Effect
        enemy.particleEffect = assets.newParticleEffect()//particleEffect

        // Position and Shape
        val shape = btSphereShape(EnemyComponent.RADIO - 1)////btCylinderShape(Vector3(RADIO/2f,12f,14f))//btBoxShape(Vector3(diametro, diametro, diametro))//btCylinderShape(Vector3(14f,5f,14f))// btCapsuleShape(3f, 6f)
        shape.calculateLocalInertia(mass, enemy.posTemp)

        /// Collision
        val rigidBodyInfo = btRigidBody.btRigidBodyConstructionInfo(mass,null, shape, enemy.posTemp)
        val rigidBody = btRigidBody(rigidBodyInfo)
        rigidBody.userData = entity
        rigidBody.motionState = MotionState(modelComponent.instance.transform)
        rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT//CF_CUSTOM_MATERIAL_CALLBACK
        rigidBody.contactCallbackFilter = 0//BulletComponent.PLAYER_FLAG //BulletComponent.SHOT_FLAG or
        rigidBody.contactCallbackFlag = BulletComponent.ENEMY_FLAG
        rigidBody.userValue = BulletComponent.ENEMY_FLAG
        rigidBody.friction = 0f
        rigidBody.rollingFriction = 1000000f
        entity.add(BulletComponent(rigidBody, rigidBodyInfo))

        /// STEERING
        //entity.add(SteeringComponent(rigidBody, RADIO))
        //https://www.gamedevelopment.blog/full-libgdx-game-tutorial-ashley-steering-behaviors/

        return entity
    }
}