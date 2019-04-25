package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Entity
import com.cesoft.cesdoom.components.AnimationComponent
import com.cesoft.cesdoom.components.AnimationParams
import com.cesoft.cesdoom.components.EnemyComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
object EnemyActions {

    fun getActionDuration(action: EnemyComponent.ACTION, type: EnemyComponent.TYPE): Float {
        return when(type) {
            EnemyComponent.TYPE.MONSTER0 ->
                when(action) {
                    EnemyComponent.ACTION.ACHING -> 2f
                    EnemyComponent.ACTION.DYING -> 3.4f
                    else -> throw Exception() //999f
                }
            EnemyComponent.TYPE.MONSTER1 ->
                when(action) {
                    EnemyComponent.ACTION.ACHING -> 1.366f
                    EnemyComponent.ACTION.DYING -> 2.999f
                    else -> throw Exception() //999f
                }
        }
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

            //--------------------------------------------------------------------------------------
            EnemyComponent.TYPE.MONSTER0 -> {
                when(action) {
                    EnemyComponent.ACTION.WALKING ->
                        AnimationParams(ACTION_NAME0, loop, speed, 0f, 4.8f)
                    EnemyComponent.ACTION.RUNNING ->
                        AnimationParams(ACTION_NAME0, loop, speed, 6f, 2.4f)
                    EnemyComponent.ACTION.ATTACKING ->
                        AnimationParams(ACTION_NAME0, loop, speed, 12.8f, 3.2f)
                    EnemyComponent.ACTION.IDLE ->
                        AnimationParams(ACTION_NAME0, loop, speed, 19.12f, 0.88f)
                    EnemyComponent.ACTION.REINCARNATING ->
                        AnimationParams(ACTION_NAME0, loop, speed, 0f, 26f)//19.12f, 0.88f)//TEST!!!!!
                    EnemyComponent.ACTION.ACHING ->
                        AnimationParams(ACTION_NAME0, loop, speed, 20f, 2f)
                    EnemyComponent.ACTION.DYING ->
                        AnimationParams(ACTION_NAME0, loop, speed, 22.6f, 3.4f)
                    /*
                0-30  walk				0-1.2   =
                0-120 walk				0-4.8   = 4.8f
                150-190 run				6-7.6   =
                150-210 run				6-8.4   = 2.4f
                250-333 attack-01		10-13.32= 3.32f
                320-400 attack-02		12.8-16 = 3.2f
                390-418 death-01		15.6-16.72
                478-500 growl			19.12-20
                500-550 death-02		20-22   = 2f
                565-650 death-03		22.6-26 = 3.4f
                //
                650 --> 26s   ==> 25 fps */
                }
            }

            //--------------------------------------------------------------------------------------
            EnemyComponent.TYPE.MONSTER1 -> {
                val offset = 0f
                when(action) {
                    EnemyComponent.ACTION.WALKING ->        AnimationParams("02_Spinnen Armature|walk_ani_vor", loop, speed, offset, 1.233f)
                    EnemyComponent.ACTION.RUNNING ->        AnimationParams("02_Spinnen Armature|run_ani_vor", loop, speed, offset, 0.625f)
                    EnemyComponent.ACTION.ATTACKING ->      AnimationParams("02_Spinnen Armature|Attack", loop, speed, offset, 1.125f)
                    EnemyComponent.ACTION.IDLE ->           AnimationParams("02_Spinnen Armature|warte_pose", loop, speed, offset, 3.75f)
                    EnemyComponent.ACTION.REINCARNATING ->  AnimationParams("02_Spinnen Armature|walk_ani_back", 1, speed, offset, 1.233f)
                    EnemyComponent.ACTION.ACHING ->         AnimationParams("02_Spinnen Armature|die 2", 1, speed, offset, 1.366f, 1f)
                    EnemyComponent.ACTION.DYING ->          AnimationParams("02_Spinnen Armature|die", 1, speed, offset, 2.999f)

//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|Attack / 1.125
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|die / 2.9999988
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|die 2 / 1.3666669
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|fall / 0.791
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|Jump / 1.2
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|run_ani_back / 0.625
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|run_ani_vor / 0.625
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|run_left / 0.625
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|run_right / 0.625
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|walk_ani_back / 1.2333333
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|walk_ani_vor / 1.2333333
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|walk_left / 1.2333333
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|walk_right / 1.2333333
//                    EnemyFactory: ANIMATION:-------------- 02_Spinnen Armature|warte_pose / 3.75


//                    EnemyComponent.ACTION.WALKING ->        AnimationParams("Spider_Armature|walk_ani_vor")
//                    EnemyComponent.ACTION.RUNNING ->        AnimationParams("Spider_Armature|run_ani_vor")
//                    EnemyComponent.ACTION.ATTACKING ->      AnimationParams("Spider_Armature|Attack")
//                    EnemyComponent.ACTION.IDLE ->           AnimationParams("Spider_Armature|warte_pose")
//                    EnemyComponent.ACTION.REINCARNATING ->  AnimationParams("Spider_Armature|walk_ani_back")
//                    EnemyComponent.ACTION.ACHING ->         AnimationParams("Spider_Armature|die")
//                    EnemyComponent.ACTION.DYING ->          AnimationParams("Spider_Armature|die 2")
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