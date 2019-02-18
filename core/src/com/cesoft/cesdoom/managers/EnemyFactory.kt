package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
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
        private const val SPAWN_DELAY = 5*1000	//TODO: si pausa o background, debe actualizar time!!!
    }

    private val MAX_ENEMIES = 4 + (PlayerComponent.currentLevel)*3
    private val random = java.util.Random()

    lateinit var enemies: ImmutableArray<Entity>
    private val allEnemies = ArrayList<Enemy>()
    init {
        Log.e(tag, "INIT---------------------------------------------------------------- MAX_ENEMIES=$MAX_ENEMIES")
        if(allEnemies.size < MAX_ENEMIES) {
            for(i in allEnemies.size until MAX_ENEMIES) {
                val type = if(PlayerComponent.currentLevel > 0)
                                when(random.nextInt(2)) {
                                    0 -> EnemyComponent.TYPE.MONSTER0
                                    else -> EnemyComponent.TYPE.MONSTER1
                                }
                            else
                                EnemyComponent.TYPE.MONSTER1
                val enemy = createEnemy(i, assets, type)
                allEnemies.add(enemy)
            }
        }
    }

    //private var countSpawnPosition = 0
    private fun getNextEnemy(id: Int): Enemy {
        val enemy = allEnemies[id]
        //val pos = when(countSpawnPosition++ % MAX_ENEMIES) {
        val pos = when(random.nextInt(MAX_ENEMIES)) {
            0 ->    Vector3(+250f, 150f, +250f)
            1 ->    Vector3(+250f, 150f, -250f)
            2 ->    Vector3(-250f, 150f, +250f)
            3 ->    Vector3(-250f, 150f, -250f)

            4 ->    Vector3(+150f, 150f, +150f)
            5 ->    Vector3(+150f, 150f, -150f)
            6 ->    Vector3(-150f, 150f, +150f)
            7 ->    Vector3(-150f, 150f, -150f)

            8 ->    Vector3(+150f, 150f, +150f)
            9 ->    Vector3(+150f, 150f, -150f)
            10 ->   Vector3(-150f, 150f, +150f)
            11 ->   Vector3(-150f, 150f, -150f)

            12 ->   Vector3(+250f, 150f, +250f)
            13 ->   Vector3(+150f, 150f, +150f)
            14 ->   Vector3(+150f, 150f, +150f)
            15 ->   Vector3(-250f, 150f, -250f)

            else -> Vector3(+250f, 150f, +250f)
        }
        Log.e(tag, "getNextEnemy id=$id ------------------------------ pos=$pos")
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
        val enemy = EnemyComponent.get(entity)
        enemy.currentAnimation = EnemyComponent.ACTION.WALKING
        enemy.isAccessLevelPath = false
        enemy.player2D.set(Vector2.Zero)
        enemy.path = null
        enemy.pathIndex = 0

        val status = StatusComponent.get(entity)
        status.alive = false
        //status.isSaltando = true
        status.estado = EnemyComponent.ACTION.WALKING
        status.deadStateTime = 0f
        status.achingStateTime = 0f
        status.health = if(enemy.type == EnemyComponent.TYPE.MONSTER0) 100f else 80f
    }


    private fun createEnemy(
            id: Int,
            assets: Assets,
            type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1
            ): Enemy {

        val entity = Enemy(id)//enemyPool.obtain()

        /// Enemy Component
        val enemy = EnemyComponent(type, id)
        entity.add(enemy)

        /// Status Component
        val stat = StatusComponent()
        entity.add(stat)

        /// Model
        val model = assets.getEnemy(type)
        val modelComponent: ModelComponent
        modelComponent = ModelComponent(model, Vector3.Zero)
        //modelComponent.frustumCullingData =FrustumCullingData.create(Vector3(0f,0f,0f), Vector3(RADIO,RADIO,RADIO), modelComponent.instance)
        entity.add(modelComponent)

        /// ANIMATION
        entity.add(AnimationComponent(modelComponent.instance))
        ///for(anim in model.animations)Log.e(tag, "ANIMATION:-------------- ${anim.id} / ${anim.duration}")


        // Evanesce Effect
        if(modelComponent.instance.materials.size > 0) {
            val material = modelComponent.instance.materials.get(0)
            val blendingAttribute = BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            material.set(blendingAttribute)
            modelComponent.blendingAttribute = blendingAttribute
        }

        // Particle Effect
        enemy.particleEffect = assets.newParticleEffect()//particleEffect

        /// Collision
        val mass: Float = if(type == EnemyComponent.TYPE.MONSTER0) EnemyComponent.MASS else EnemyComponent.MASS/3
        val shape = btSphereShape(EnemyComponent.RADIO - 1)////btCylinderShape(Vector3(RADIO/2f,12f,14f))//btBoxShape(Vector3(diametro, diametro, diametro))//btCylinderShape(Vector3(14f,5f,14f))// btCapsuleShape(3f, 6f)
        shape.calculateLocalInertia(mass, enemy.position)
        val rigidBodyInfo = btRigidBody.btRigidBodyConstructionInfo(mass,null, shape, enemy.position)
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