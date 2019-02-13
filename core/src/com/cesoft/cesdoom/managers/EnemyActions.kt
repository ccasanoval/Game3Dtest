package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Entity
import com.cesoft.cesdoom.components.AnimationComponent
import com.cesoft.cesdoom.components.AnimationParams
import com.cesoft.cesdoom.components.EnemyComponent
import com.cesoft.cesdoom.util.Log

object EnemyActions {

    private val random = java.util.Random()

    object ActionDuration {
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
        val actionDuration = mapOf(//<EnemyComponent.TYPE, Map<EnemyComponent.ACTION, Float>>(
            EnemyComponent.TYPE.MONSTER0 to
                mapOf(
                    EnemyComponent.ACTION.WALKING to 4.8f,
                    EnemyComponent.ACTION.RUNNING to 2.4f,
                    EnemyComponent.ACTION.ATTACKING to 3.15f,
                    EnemyComponent.ACTION.IDLE to 0.88f,
                    EnemyComponent.ACTION.REINCARNATING to 26f,//TODO
                    EnemyComponent.ACTION.ACHING to 2.5f,
                    EnemyComponent.ACTION.DYING to 3.4f//3.4f
                ),
            EnemyComponent.TYPE.MONSTER1 to
                mapOf(
                    EnemyComponent.ACTION.WALKING to 4.8f,
                    EnemyComponent.ACTION.RUNNING to 2.4f,
                    EnemyComponent.ACTION.ATTACKING to 3.15f,
                    EnemyComponent.ACTION.IDLE to 0.88f,
                    EnemyComponent.ACTION.REINCARNATING to 26f,//TODO
                    EnemyComponent.ACTION.ACHING to 2.5f,
                    EnemyComponent.ACTION.DYING to 3.4f//3.4f
                )
        )
    }

    fun getActionDuration(action: EnemyComponent.ACTION, type: EnemyComponent.TYPE = EnemyComponent.TYPE.MONSTER1): Float {
        return ActionDuration.actionDuration.getValue(type).getValue(action)
    }


    private val actionParams0: Map<EnemyComponent.ACTION, AnimationParams> = mapOf(
            EnemyComponent.ACTION.WALKING to getAnimationParams(EnemyComponent.ACTION.WALKING, EnemyComponent.TYPE.MONSTER0),
            EnemyComponent.ACTION.RUNNING to getAnimationParams(EnemyComponent.ACTION.RUNNING, EnemyComponent.TYPE.MONSTER0),
            EnemyComponent.ACTION.ATTACKING to getAnimationParams(EnemyComponent.ACTION.ATTACKING, EnemyComponent.TYPE.MONSTER0),
            EnemyComponent.ACTION.IDLE to getAnimationParams(EnemyComponent.ACTION.IDLE, EnemyComponent.TYPE.MONSTER0),
            EnemyComponent.ACTION.REINCARNATING to getAnimationParams(EnemyComponent.ACTION.REINCARNATING, EnemyComponent.TYPE.MONSTER0),
            EnemyComponent.ACTION.ACHING to getAnimationParams(EnemyComponent.ACTION.ACHING, EnemyComponent.TYPE.MONSTER0),
            EnemyComponent.ACTION.DYING to getAnimationParams(EnemyComponent.ACTION.DYING, EnemyComponent.TYPE.MONSTER0)
    )
    private val actionParams1: Map<EnemyComponent.ACTION, AnimationParams> = mapOf(
            EnemyComponent.ACTION.WALKING to getAnimationParams(EnemyComponent.ACTION.WALKING, EnemyComponent.TYPE.MONSTER1),
            EnemyComponent.ACTION.RUNNING to getAnimationParams(EnemyComponent.ACTION.RUNNING, EnemyComponent.TYPE.MONSTER1),
            EnemyComponent.ACTION.ATTACKING to getAnimationParams(EnemyComponent.ACTION.ATTACKING, EnemyComponent.TYPE.MONSTER1),
            EnemyComponent.ACTION.IDLE to getAnimationParams(EnemyComponent.ACTION.IDLE, EnemyComponent.TYPE.MONSTER1),
            EnemyComponent.ACTION.REINCARNATING to getAnimationParams(EnemyComponent.ACTION.REINCARNATING, EnemyComponent.TYPE.MONSTER1),
            EnemyComponent.ACTION.ACHING to getAnimationParams(EnemyComponent.ACTION.ACHING, EnemyComponent.TYPE.MONSTER1),
            EnemyComponent.ACTION.DYING to getAnimationParams(EnemyComponent.ACTION.DYING, EnemyComponent.TYPE.MONSTER1)
    )
    private val typeActionParams = mapOf(
            EnemyComponent.TYPE.MONSTER0 to actionParams0,
            EnemyComponent.TYPE.MONSTER1 to actionParams1
    )

    private fun getActionParams(action: EnemyComponent.ACTION, type: EnemyComponent.TYPE) =// = EnemyComponent.TYPE.MONSTER1) =
            typeActionParams.getValue(type)[action]

    private const val ACTION_NAME0 = "MilkShape3D Skele|DefaultAction"
    private fun getAnimationParams(action: EnemyComponent.ACTION, type: EnemyComponent.TYPE): AnimationParams {// = EnemyComponent.TYPE.MONSTER1): AnimationParams {
        val loop = -1
        val speed = 1f
        return when(type) {
            EnemyComponent.TYPE.MONSTER0 -> {
                val time = getActionDuration(action, type)
                when(action) {
                    EnemyComponent.ACTION.WALKING ->
                        AnimationParams(ACTION_NAME0, loop, speed, 0f, time)
                    EnemyComponent.ACTION.RUNNING ->
                        AnimationParams(ACTION_NAME0, loop, speed, 6f, time)
                    EnemyComponent.ACTION.ATTACKING -> {
                        AnimationParams(ACTION_NAME0, loop, speed, 12.8f, time)
//						when(random.nextInt(3)) {
//							0 -> AnimationParams(ACTION_NAME0, loop, speed, 12.8f, time)
//							else -> AnimationParams(ACTION_NAME, loop, speed, 10f, time)
//						}
                    }
                    EnemyComponent.ACTION.IDLE ->
                        AnimationParams(ACTION_NAME0, loop, speed, 19.12f, time)
                    EnemyComponent.ACTION.REINCARNATING ->
                        AnimationParams(ACTION_NAME0, loop, speed, 0f, time)
                    EnemyComponent.ACTION.ACHING -> {
//							if (random.nextInt(2) == 0)
//								AnimationParams(ACTION_NAME, loop, speed, 15.6f, time)
//							else
                        AnimationParams(ACTION_NAME0, loop, speed, 20f, time)
                    }
                    EnemyComponent.ACTION.DYING -> {
                        AnimationParams(ACTION_NAME0, loop, speed, 22.6f, time)
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
            EnemyComponent.TYPE.MONSTER1 -> {
                when(action) {
                    EnemyComponent.ACTION.WALKING ->        AnimationParams("02_Spinnen Armature|walk_ani_vor")
                    EnemyComponent.ACTION.RUNNING ->        AnimationParams("02_Spinnen Armature|run_ani_vor")
                    EnemyComponent.ACTION.ATTACKING ->      AnimationParams("02_Spinnen Armature|Attack")
                    EnemyComponent.ACTION.IDLE ->           AnimationParams("02_Spinnen Armature|warte_pose")
                    EnemyComponent.ACTION.REINCARNATING ->  AnimationParams("02_Spinnen Armature|walk_ani_back")
                    EnemyComponent.ACTION.ACHING ->         AnimationParams("02_Spinnen Armature|die")
                    EnemyComponent.ACTION.DYING ->          AnimationParams("02_Spinnen Armature|die 2")

//                    02_Spinnen Armature|Attack
//                    02_Spinnen Armature|die
//                    02_Spinnen Armature|die 2
//                    02_Spinnen Armature|fall
//                    02_Spinnen Armature|Jump
//                    02_Spinnen Armature|run_ani_back
//                    02_Spinnen Armature|run_ani_vor
//                    02_Spinnen Armature|run_left
//                    02_Spinnen Armature|run_right
//                    02_Spinnen Armature|walk_ani_back
//                    02_Spinnen Armature|walk_ani_vor
//                    02_Spinnen Armature|walk_left
//                    02_Spinnen Armature|walk_right
//                    02_Spinnen Armature|warte_pose
                }
            }
        }
    }

    fun setAnimation(entity: Entity, action: EnemyComponent.ACTION) {
        val enemy = EnemyComponent.get(entity)
        enemy.currentAnimation = action
        val animParams = getActionParams(action, enemy.type)
        animParams?.let { params ->
            if(params.id.isEmpty()) return
            AnimationComponent.get(entity).animate(params)
        }
    }
}