/*
This file was based on the JadeReader implementation of rikai in java

If you are interested in the JadeReader app, visit https://play.google.com/store/apps/details?id=com.zyz.mobile&hl=pt_BR
*/

package com.domgintoki.shosetsumobile.rikai.deinflector

import java.util.*


class RuleGroup {
    // the "from" field of each Rule in a RuleGroup must have the same length
    var flen = 0
    private val rules =
        ArrayList<Rule>()

    fun add(rule: Rule) {
        rules.add(rule)
    }

    operator fun get(index: Int): Rule {
        return rules[index]
    }

    fun size(): Int {
        return rules.size
    }
}