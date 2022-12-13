/*
This file was based on the JadeReader implementation of rikai in java

If you are interested in the JadeReader app, visit https://play.google.com/store/apps/details?id=com.zyz.mobile&hl=pt_BR
*/
package rikai.dictionaries

import org.sqlite.SQLiteException
import java.sql.Connection
import java.sql.DriverManager


/**
 * Class used to find words based in sentences
 * */
abstract class Dictionary<T> {
    protected var database: Connection? = null


    /**
     * Search a word in the dictionary
     *
     * @param word The word to search for
     * */
    abstract fun findWords(word: String?): ArrayList<T>


    /**
     * find all variants of the specified word, if any
     *
     * @param word the base word
     * @return all the variants of the specified word
     */
    fun wordSearch(word: String): ArrayList<T> {
        return findVariants(word, DEFAULT_MAX_COUNT)
    }


    /**
     * finds all possible deinflected words of the given word
     *
     * @param _word       the word to search for
     * @param _maxCount   maximum number of variants to return, default to DEFAULT_MAX_COUNT
     * @return an Entries encompassing the results.
     */
    fun findVariants(_word: String, _maxCount: Int): ArrayList<T> {return arrayListOf()}


    /**
     * Loads the dictionary database from a filepath <p>
     * Data type: .db, .sqlite etc
     *
     * @param dictionary_path The path for the dictionary file
     * @return Whether the loading was a success
     * */
    fun loadData(dictionary_path: String): Boolean {
        try {
            database = DriverManager.getConnection("jdbc:sqlite:$dictionary_path")
        } catch (_: SQLiteException) {}
        return database != null
    }


    /**
     * converts all katakana and half-width kana to full-width hiragana
     *
     * @param word the word to convert
     * @return the hiragana
     */
    fun toHiragana(word: String): String? {
        return toHiragana(word, false, null)
    }


    /**
     * convert katakana and half-width kana to full-width hiragana
     *
     * @param word    the word to convert
     * @param discard if true, conversion is stopped when a non-japanese char is encountered
     * @return the hiragana
     */
    fun toHiragana(word: String, discard: Boolean, trueLen: IntArray?): String {
        var u: Char
        var v: Char
        var previous = 0.toChar()
        var result = ""

        for (i in word.indices) {
            v = word[i]
            u = v
            if (u.toInt() <= 0x3000) {
                if (discard) {
                    break
                }
            } else if (u.toInt() in 0x30A1..0x30F3) {
                u = (u - 0x60.toChar()).toChar()
            } else if (u.toInt() in 0xFF66..0xFF9D) {
                u = ch[u.toInt() - 0xFF66]
            } else if (u.toInt() == 0xFF9E) {
                if (previous.toInt() in 0xFF73..0xFF8E) {
                    result = result.substring(0, result.length - 1)
                    u = cv[previous.toInt() - 0xFF73]
                }
            } else if (u.toInt() == 0xFF9F) {
                if (previous.toInt() in 0xFF8A..0xFF8E) {
                    result = result.substring(0, result.length - 1)
                    u = cs[previous.toInt() - 0xFF8A]
                }
            } else if (u.toInt() == 0xFF5E) {
                previous = 0.toChar()
                continue
            }
            result += u
            if (trueLen != null) {
                trueLen[result.length - 1] = i + 1
            }
            previous = v
        }
        return result
    }


    /**
     * Arrays used for katakana->hiragana or half-width->hiragana conversions
     * */
    companion object {
        @JvmStatic private val ch: CharArray = charArrayOf(
            '\u3092', '\u3041', '\u3043', '\u3045', '\u3047', '\u3049', '\u3083', '\u3085', '\u3087', '\u3063', '\u30FC', '\u3042', '\u3044', '\u3046',
            '\u3048', '\u304A', '\u304B', '\u304D', '\u304F', '\u3051', '\u3053', '\u3055', '\u3057', '\u3059', '\u305B', '\u305D', '\u305F', '\u3061',
            '\u3064', '\u3066', '\u3068', '\u306A', '\u306B', '\u306C', '\u306D', '\u306E', '\u306F', '\u3072', '\u3075', '\u3078', '\u307B', '\u307E',
            '\u307F', '\u3080', '\u3081', '\u3082', '\u3084', '\u3086', '\u3088', '\u3089', '\u308A', '\u308B', '\u308C', '\u308D', '\u308F', '\u3093')

        @JvmStatic private val cv = charArrayOf(
            '\u30F4', '\uFF74', '\uFF75', '\u304C', '\u304E', '\u3050', '\u3052', '\u3054', '\u3056', '\u3058', '\u305A', '\u305C', '\u305E', '\u3060',
            '\u3062', '\u3065', '\u3067', '\u3069', '\uFF85', '\uFF86', '\uFF87', '\uFF88', '\uFF89', '\u3070', '\u3073', '\u3076', '\u3079', '\u307C')

        @JvmStatic private val cs = charArrayOf(
            '\u3071', '\u3074', '\u3077', '\u307A', '\u307D')

        @JvmStatic protected val DEFAULT_MAX_COUNT = 10
    }
}