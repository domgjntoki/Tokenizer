package rikai.dictionaries

import com.domgintoki.shosetsumobile.rikai.deinflector.Deinflector
import com.domgintoki.shosetsumobile.rikai.dictionaries.WordEntry
import java.sql.SQLException
import kotlin.math.min


/**
 * Class used to handle general dictionaries, with verbs, adjectives, etc
 * <p>
 * Needs a deinflector for searching purposes
 * @see Deinflector
 * @see Dictionary
 * */
class WordsDictionary(df: Deinflector?) : Dictionary<WordEntry>() {
    private var deinflector: Deinflector? = df

    /**
     * Searches for words in the dictionary
     *
     * @param word The word to be searched
     * @return Any word or reading that matches the query
     * @see WordEntry
     * */
    override fun findWords(word: String?): ArrayList<WordEntry> {
        if(database == null) return arrayListOf()
        val selectSQL = "select * from DICTIONARY where word=\"$word\""
        val pst = database!!.prepareStatement(selectSQL)
        val result = pst.executeQuery()
        val results = ArrayList<WordEntry>()

        try {
            while(result.next()) {
                var w = WordEntry(
                    word = result.getString("word"),
                    leftId = result.getInt("leftId"),
                    rightId = result.getInt("rightId"),
                    cost = result.getInt("cost")
                )
                results.add(w)
            }
            return results
        } catch (e1: SQLException) {
            e1.printStackTrace()
            return arrayListOf()
        } finally {
            pst.close()
        }
    }


    /**
     * finds all possible deinflected words of the given word
     *
     * @param sentence the sentence for searching
     * @param i The index to starting searching for
     * @param maxSearchSize
     * @return a list with the results.
     * @see WordEntry
     */
    fun findAllTermStartingAt(sentence: String, i: Int, maxSearchSize: Int = 6): ArrayList<WordEntry> {
        val results = ArrayList<WordEntry>()
        for (j in 1 until maxSearchSize) {
            if (i+j > sentence.length)
                break
            val end = i+j

            val searchWord = sentence.substring(i, end)
            val foundWords = findWords(searchWord)
            results.addAll(findWords(searchWord))
        }
        return results
    }


    /**
     * Index information for the database request
     * */
    companion object TableInfo {
        private const val WORD_COLUMN = "word"
        private const val READING_COLUMN = "reading"
        private const val PART_OF_SPEECH_COLUMN = "pos"
        private const val GLOSSARY_COLUMN = "glossary"
    }
}