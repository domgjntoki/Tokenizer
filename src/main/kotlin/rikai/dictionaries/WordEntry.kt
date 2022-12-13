package com.domgintoki.shosetsumobile.rikai.dictionaries


/**
 * The entry type for queries in the WordDictionary
 * @see WordsDictionary
 * */
data class WordEntry (
    var word: String,
    var leftId: Int,
    var rightId: Int,
    var cost: Int)
