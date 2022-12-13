import com.domgintoki.shosetsumobile.rikai.dictionaries.WordEntry
import data_structures.ConnectionCost
import data_structures.ConnectionCostImpl
import data_structures.LatticeNode
import data_structures.Lattices
import rikai.dictionaries.WordsDictionary
import java.io.File
import java.io.FileInputStream
import java.sql.DriverManager
import java.sql.Statement

fun tokenize(text: String): List<String> {
    val dictionary = WordsDictionary(null)
    dictionary.loadData("src/main/resources/mecab/database/terms.db")
    val lattice = Lattices.createLattice(ConnectionCostImpl(), text.length)
    for (i in text.indices) {
        if (!lattice.hasNodeEndingAtIndex(i))
            continue

        val terms = dictionary.findAllTermStartingAt(text, i)
        for (term in terms) {
            lattice.addNode(
                LatticeNode(
                    term = term.word,
                    startIndex = i,
                    endIndex = i+term.word.length,
                    leftId=term.leftId,
                    rightId = term.rightId,
                    cost = term.cost))
        }
    }
    val results = lattice.findPath()
    if (results == null) {
        println("No result")
        return arrayListOf()
    }
    val tokenizedResult = results.map {r-> r.term}
    println(tokenizedResult)
    return tokenizedResult
}
fun main(args: Array<String>) {
    tokenize("私は日本食が大好きです！")
    tokenize("東京都に住む")
    tokenize("無職転生異世界行ったら本気出す")
    tokenize("彼はこの先なにを思い、なにを為すのか")
    tokenize("すもももももももものうち")
}