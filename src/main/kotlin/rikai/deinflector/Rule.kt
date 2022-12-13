package com.domgintoki.shosetsumobile.rikai.deinflector


/**
 * Rules for deinflection data class
 * */
data class Rule(val from: String, val to: String, val type: Int, val reasonIndex: Int) {
    constructor(rule: List<String>): this(rule[0], rule[1], rule[2].toInt(), rule[3].toInt())
}