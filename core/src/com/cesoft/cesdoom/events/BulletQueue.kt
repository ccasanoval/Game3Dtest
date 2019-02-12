package com.cesoft.cesdoom.events

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import java.util.PriorityQueue

class BulletQueue : Listener<BulletEvent> {

    private val eventQueue: PriorityQueue<BulletEvent> = PriorityQueue()

    val events: Array<BulletEvent>
        get() {
            val events = eventQueue.toTypedArray()
            eventQueue.clear()
            return events
        }

    // Implements Listener<BulletEvent>
    override fun receive(signal: Signal<BulletEvent>, event: BulletEvent) {
        eventQueue.add(event)
    }
}