/*
This file was based on the JadeReader implementation of rikai in java

If you are interested in the JadeReader app, visit https://play.google.com/store/apps/details?id=com.zyz.mobile&hl=pt_BR
*/


package com.domgintoki.shosetsumobile.rikai

import com.domgintoki.shosetsumobile.rikai.deinflector.Deinflector
import rikai.dictionaries.WordsDictionary
import com.domgintoki.shosetsumobile.rikai.dictionaries.WordEntry


/**
 * Class used to communicate with the dictionaries
 * */
class RikaiDroid {
    private var deinflector: Deinflector? = null
    private var mecabDict: WordsDictionary? = null

    private var maxCount = 10


    /**
     * Loads deinflector
     * @param deinflector_path The file path to the deinflector.
     * @see Deinflector
     * */
    fun loadDeinflector(deinflector_path: String) {
        deinflector = Deinflector(deinflector_path)
    }


    /**
     * Loads JMdict
     *
     * @param jmdict_path The file path to JMdict
     * @see WordsDictionary
     * */
    fun loadJMdict(mecabDictPath: String): Boolean {
        if(deinflector != null) {
            mecabDict = WordsDictionary(deinflector!!)
        }
        return deinflector != null && mecabDict!!.loadData(mecabDictPath)
    }


    /**
     * Search a generic word in the JMDICT, with deinflection for adjective, verb, and others
     *
     * Usage example:
     * - searchWords("振り下ろそうとしていた拳を").
     *
     * @param sentence The sentence starting at the first character
     * @return List of possible words
     * */
    fun searchJMdict(sentence: String): ArrayList<WordEntry> {
        return if(mecabDict != null)
            mecabDict!!.findVariants(sentence, maxCount)
        else // If the jmdict was not initialized, return empty list
            arrayListOf()
    }
}