package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PlayerComponent : Component {

    companion object {
        private val mapper: ComponentMapper<PlayerComponent> = ComponentMapper.getFor(PlayerComponent::class.java)
        fun get(entity: Entity):PlayerComponent = mapper.get(entity)

        private const val MESSAGE_DURATION = 5000L

        const val TALL = 20f
        private const val HEALTH_FULL = 100
        const val IMPULSE = 4000f
        const val MASS = 65f
        const val RADIO = 14f

        ///TODO: most of this to GameStatus object...
        var isGodModeOn = false
        var ammo: Int = 0
        var isWinning = false
        //var isJumping = false
        var isWalking = false

        var eyes = TALL

        var isReloading = false


        /// Level -----------------
        var currentLevel: Int = 0

        /// Walk camera vibration -----------------
        var yFoot = 0f
        private var oFoot = 0f
        private var FOOT_MULTI = 12
        private var FOOT_MAX = 3f
        private var isFootUp = false
        fun animFootStep(delta: Float)
        {
            if(isFootUp) {
                yFoot += delta*FOOT_MULTI
                if(yFoot > +FOOT_MAX) {
                    isFootUp = false
                }
            }
            else {
                yFoot -= delta*FOOT_MULTI
                if(yFoot < -FOOT_MAX) {
                    isFootUp = true
                }
            }
            oFoot = yFoot
        }

        /// Score -----------------
        var score: Long = 0          //TODO: MessageSystem + dispatch signal
        fun addScore(pts: Int) { score += pts }


        /// Health -----------------
        var health: Int = 100       //TODO: MessageSystem + dispatch signal
            private set
        fun hurt(pain: Int) {
            health -= pain
            if(health < 0)
                health = 0
        }
        fun heal(pain: Int) {
            health += pain
            if(health > 150)
                health = 150
        }
        fun resetHealth() {
            health = HEALTH_FULL
        }
        fun isDead() = health < 1


        /// Messages -----------------
        private var lastMessage = 0L    //TODO: MessageSystem + dispatch signal
        var message: String = ""
            get() {
                val now = System.currentTimeMillis()
                if(now > lastMessage + MESSAGE_DURATION)
                    field = if(isGodModeOn)
                        "* GoD Mode On *"
                    else
                        ""
                return field
            }
            set(value) {
                field = value
                lastMessage = System.currentTimeMillis()
            }
    }

}
