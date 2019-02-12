package com.cesoft.cesdoom.events

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import java.util.PriorityQueue

//TODO: make generics
//https://kotlinlang.org/docs/reference/generics.html
class EnemyQueue : Listener<EnemyEvent> {

    private val eventQueue: PriorityQueue<EnemyEvent> = PriorityQueue()

    val events: Array<EnemyEvent>
        get() {
            val events = eventQueue.toTypedArray()
            eventQueue.clear()
            return events
        }

    fun poll(): EnemyEvent {
        return eventQueue.poll()
    }

    // Implements Listener<EnemyEvent>
    override fun receive(signal: Signal<EnemyEvent>, event: EnemyEvent) {
        eventQueue.add(event)
    }
}