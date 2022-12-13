package com.domgintoki.shosetsumobile.rikai.deinflector


/**
 * Deinflected words data class
 * @see Deinflector
 * */
data class DeinflectedWord(var word: String?, var type: Int, var reason: String?) {
    constructor(): this(null, 0, "")
}