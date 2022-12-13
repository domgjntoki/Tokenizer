/*
This file was based on the JadeReader implementation of rikai in java

If you are interested in the JadeReader app, visit https://play.google.com/store/apps/details?id=com.zyz.mobile&hl=pt_BR
*/

package com.domgintoki.shosetsumobile.rikai.deinflector

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


/**
 * Class used for word deinflection.
 * */
class Deinflector(stream: InputStream) {
    private var difReasons = arrayListOf<String>()
    private var difRuleGroups = arrayListOf<RuleGroup>()


    init {
        loadDeinflectionData(stream)
    }

    constructor(path: String): this(FileInputStream(File(path)))


    /**
     * Loads the deinflection data to memory <p>
     * See https://github.com/melink14/rikaikun/blob/master/data/deinflect.dat for input example
     * */
    private fun loadDeinflectionData(stream: InputStream): Boolean {
        val deinflectionData = arrayListOf<String>()
        stream
            .bufferedReader(charset("UTF-8"))
            .useLines {
                val iterator = it.iterator() // Jumping header
                iterator.next()
                iterator.forEach { s -> deinflectionData.add(s) }
            }
        if(deinflectionData.size == 0) return false

        var group = RuleGroup()
        var prevLen = -1
        for(line in deinflectionData) {
            val fields = line.split("\t")

            if(fields.size == 1) {
                difReasons.add(fields[0])
            } else if(fields.size == 4) {
                val rule =
                    Rule(fields)
                if(prevLen != rule.from.length) {
                    group =
                        RuleGroup()
                    group.flen = rule.from.length
                    prevLen = group.flen
                    difRuleGroups.add(group)
                }
                group.add(rule)
            }
        }

        return true
    }


    /**
     * returns a list of deinflected words, including the original word
     *
     * @param wordToDif the word to deinflect
     * @return a list of deinflected word
     */
    fun deinflect(wordToDif: String): ArrayList<DeinflectedWord> {
        val result = ArrayList<DeinflectedWord>()

        // All the words added to the result list so far.
        // (key, value) where key is the word, and value is the index
        // of the word in the result
        val dws: MutableMap<String, Int> = TreeMap()

        var dw = DeinflectedWord(wordToDif, 0xff, "")
        result.add(dw) // add the word itself to the result
        dws[wordToDif] = 0

        // note that result.size may increase after each iteration
        var i = 0
        while(i < result.size) {
            val word = result[i].word
            // check the word against each Rule in each RuleGroup
            for (j in difRuleGroups.indices) {
                val group = difRuleGroups[j]
                if (group.flen > word!!.length) {
                    continue
                }

                /* get the last part of the word (precisely the last group.flen
				 * characters of the word) and check if it's a valid inflection
				 */
                val tail = word.substring(word.length - group.flen)
                for (k in 0 until group.size()) {
                    val (from, to, type, reasonIndex) = group[k]
                    if ( (result[i].type and type) == 0 || tail != from) {
                        continue  // failed, go to the next rule
                    }
                    val newWord = word.substring(0, word.length - group.flen) + to
                    if (newWord.length <= 1) {
                        continue
                    }
                    dw = DeinflectedWord()
                    if (dws[newWord] != null) { // deinflected word is same as previous ones
                    // but under A different rule
                        dw = result[dws[newWord]!!]
                        dw.type = dw.type or (type shr 8)
                        continue
                    }
                    dws[newWord] = result.size
                    dw.word = newWord
                    dw.type = type shr 8
                    if (result[i].reason!!.isNotEmpty()) {
                        dw.reason = difReasons[reasonIndex] + " < " + result[i].reason
                    } else {
                        dw.reason = difReasons[reasonIndex]
                    }
                    result.add(dw)
                }
            }
            i++
        }
        return result
    }
}