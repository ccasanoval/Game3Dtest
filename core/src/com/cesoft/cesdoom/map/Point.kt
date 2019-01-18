package com.cesoft.cesdoom.map

data class Point(val x: Int, val y: Int) {
    fun dist2(point: Point) = (x - point.x)*(x - point.x) + (y - point.y)*(y - point.y)
}