package com.cesoft.cesdoom.events

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import java.util.PriorityQueue

//https://kotlinlang.org/docs/reference/generics.html
class GameQueue : Listener<GameEvent> {

    private val eventQueue: PriorityQueue<GameEvent> = PriorityQueue()

    val events: Array<GameEvent>
        get() {
            val events = eventQueue.toTypedArray()
            eventQueue.clear()
            return events
        }

//    fun poll(): GameEvent {
//        return eventQueue.poll()
//    }

    // Implements Listener<GameEvent>
    override fun receive(signal: Signal<GameEvent>, event: GameEvent) {
        eventQueue.add(event)
    }
}
