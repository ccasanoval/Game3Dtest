package com.cesoft.cesdoom.events

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import java.util.PriorityQueue

class RenderQueue : Listener<RenderEvent> {

    private val eventQueue: PriorityQueue<RenderEvent> = PriorityQueue()

    val events: Array<RenderEvent>
        get() {
            val events = eventQueue.toTypedArray()
            eventQueue.clear()
            return events
        }

    // Implements Listener<RenderEvent>
    override fun receive(signal: Signal<RenderEvent>, event: RenderEvent) {
        eventQueue.add(event)
    }
}