package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Entity
import com.cesoft.cesdoom.components.AnimationComponent
import com.cesoft.cesdoom.components.AnimationParams
import com.cesoft.cesdoom.components.EnemyComponent

object EnemyActions {

    private val random = java.util.Random()

    object ActionDuration1 {
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
        val actionDuration = mapOf(
                EnemyComponent.ACTION.WALKING to 4.8f,
                EnemyComponent.ACTION.RUNNING to 2.4f,
                EnemyComponent.ACTION.ATTACKING to 3.15f,
                EnemyComponent.ACTION.IDLE to 0.88f,
                EnemyComponent.ACTION.REINCARNATING to 26f,//TODO
                EnemyComponent.ACTION.ACHING to 2.5f,
                EnemyComponent.ACTION.DYING to 3.4f//3.4f
        )
    }

    private val typeActionDuration = mapOf(
            EnemyComponent.TYPE.MONSTER1 to ActionDuration1
            //EnemyComponent.TYPE.MONSTER2 to ActionDuration2
    )
    fun getActionDuration(action: EnemyComponent.ACTION, type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1) =
            typeActionDuration.getValue(type).actionDuration.getValue(action)


    private val actionParams1: Map<EnemyComponent.ACTION, AnimationParams> = mapOf(
            EnemyComponent.ACTION.WALKING to getAnimationParams(EnemyComponent.ACTION.WALKING),
            EnemyComponent.ACTION.RUNNING to getAnimationParams(EnemyComponent.ACTION.RUNNING),
            EnemyComponent.ACTION.ATTACKING to getAnimationParams(EnemyComponent.ACTION.ATTACKING),
            EnemyComponent.ACTION.IDLE to getAnimationParams(EnemyComponent.ACTION.IDLE),
            EnemyComponent.ACTION.REINCARNATING to getAnimationParams(EnemyComponent.ACTION.REINCARNATING),
            EnemyComponent.ACTION.ACHING to getAnimationParams(EnemyComponent.ACTION.ACHING),
            EnemyComponent.ACTION.DYING to getAnimationParams(EnemyComponent.ACTION.DYING)
    )
    private val typeActionParams = mapOf(
            EnemyComponent.TYPE.MONSTER1 to actionParams1
            //EnemyComponent.TYPE.MONSTER2 to ActionDuration2
    )

    fun getActionParams(action: EnemyComponent.ACTION, type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1) =
            typeActionParams.getValue(type)[action]

    private const val ACTION_NAME = "MilkShape3D Skele|DefaultAction"
    private fun getAnimationParams(action: EnemyComponent.ACTION, type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1): AnimationParams {
        val loop = -1
        val speed = 1f
        val time = getActionDuration(action, type)
        when (type) {
            EnemyComponent.TYPE.MONSTER1 ->
                return when(action) {
                    EnemyComponent.ACTION.WALKING ->
                        AnimationParams(ACTION_NAME, loop, speed, 0f, time)
                    EnemyComponent.ACTION.RUNNING ->
                        AnimationParams(ACTION_NAME, loop, speed, 6f, time)
                    EnemyComponent.ACTION.ATTACKING -> {
                        AnimationParams(ACTION_NAME, loop, speed, 12.8f, time)
//						when(random.nextInt(3)) {
//							0 -> AnimationParams(ACTION_NAME, loop, speed, 12.8f, time)
//							else -> AnimationParams(ACTION_NAME, loop, speed, 10f, time)
//						}
                    }
                    EnemyComponent.ACTION.IDLE ->
                        AnimationParams(ACTION_NAME, loop, speed, 19.12f, time)
                    EnemyComponent.ACTION.REINCARNATING ->
                        AnimationParams(ACTION_NAME, loop, speed, 0f, time)
                    EnemyComponent.ACTION.ACHING -> {
//							if (random.nextInt(2) == 0)
//								AnimationParams(ACTION_NAME, loop, speed, 15.6f, time)
//							else
                        AnimationParams(ACTION_NAME, loop, speed, 20f, time)
                    }
                    EnemyComponent.ACTION.DYING -> {
                        AnimationParams(ACTION_NAME, loop, speed, 22.6f, time)
                        /*if(random.nextInt(2) == 0) {
                            Log.e("----******************--------- Enemy Factory DYING 1")
                            AnimationParams(ACTION_NAME, loop, speed, 15.6f, time)
                        }
                        else {
                            Log.e("-----*********************-------- Enemy Factory DYING 2")
                            AnimationParams(ACTION_NAME, loop, speed, 20f, time)
                        }*/
                    }
                }
        }
    }




    fun setAnimation(entity: Entity, action: EnemyComponent.ACTION) {
        val enemy = EnemyComponent.get(entity)
        enemy.currentAnimation = action
        val animParams = EnemyActions.getActionParams(action, enemy.type)
        animParams?.let { params ->
            if(params.id.isEmpty()) return
            AnimationComponent.get(entity).animate(params)
        }
    }
}