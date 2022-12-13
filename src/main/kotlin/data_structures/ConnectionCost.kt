package data_structures

interface ConnectionCost {
    fun lookup(rightId: Int, leftId: Int): Int
}