package com.cesoft.cesdoom.map


data class Node(val index: Int, val point: Point, val isValido: Boolean) {
    //override fun toString() = String.format(Locale.ENGLISH, "Nodo: " + (if (isValido) "VALIDO" else "MURO") + " (%d, %d)", x, y)
}
