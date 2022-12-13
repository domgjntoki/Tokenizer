import java.io.File
import java.io.FileInputStream
import java.sql.DriverManager
import java.sql.Statement

class CreateDatabase {
    fun createCostFile(filename: String, path: String) {
        val command = "INSERT INTO COSTS (leftId, rightId, cost)  VALUES (?,?,?)"
        val url = "jdbc:sqlite:F:/Desktop/Python/PDS/Trab 1/dicts/dict_db/${filename}"
        val table = """
        CREATE TABLE IF NOT EXISTS COSTS (
            leftId INT,
            rightId INT,
            cost INT
        );
        CREATE INDEX ix_leftId ON COSTS (leftId ASC);
        CREATE INDEX ix_rightId ON COSTS (rightId ASC);
    """.trimIndent()
        var st : Statement?
        DriverManager.getConnection(url)?.use { con ->

        st = con.createStatement()
        st!!.execute(table)
        con.autoCommit = false
            FileInputStream(File(path))
                .bufferedReader(charset("UTF-8"))
                .useLines {
                    val pst = con.prepareStatement(command)
                    var count = 0
                    for (s in it) {
                        val fields = s.split(' ')
                        if (fields.size < 3) continue
                        fields.forEachIndexed {i, str ->
                            pst.setInt(i+1, str.toInt())
                        }
                        pst.addBatch()
                        if(count++ % 999 == 0)
                            pst.executeBatch()
                    }
                    pst.executeBatch()
                }
            con.commit()
        }

    }
    fun createSqlFile(filename: String, dictpaths: List<String>) {
        val command = "INSERT INTO DICTIONARY (word, leftId, rightId, cost)  VALUES (?,?,?,?)"
        val url = "jdbc:sqlite:F:/Desktop/Python/PDS/Trab 1/dicts/dict_db/${filename}"
        val table = """
        CREATE TABLE IF NOT EXISTS DICTIONARY (
            word VARCHAR(150),
            leftId INT,
            rightId INT,
            cost INT
        );
        CREATE INDEX ix_word ON DICTIONARY (word ASC);
    """.trimIndent()
        var st : Statement?
        DriverManager.getConnection(url)?.use { con ->

            st = con.createStatement()
            st!!.execute(table)
            con.autoCommit = false
            dictpaths.forEach { dictpath ->
                FileInputStream(File(dictpath))
                    .bufferedReader(charset("EUC-JP"))
                    .useLines {
                        val pst = con.prepareStatement(command)
                        var count = 0
                        for (s in it) {
                            val fields = s.split(',').subList(0, 4)
                            fields.forEachIndexed {i, str ->
                                if(i == 0) { // if the reading space is empty, and the word does not contain hiragana
                                    pst.setString(i + 1, str)
                                } else {
                                    pst.setInt(i+1, str.toInt())
                                }
                            }
                            pst.addBatch()
                            if(count++ % 999 == 0)
                                pst.executeBatch()
                        }
                        pst.executeBatch()
                    }
            }

            con.commit()
        }

    }

    fun toHiragana(word: String): String {
        return toHiragana(word, false, null)
    }

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

    val ch: CharArray = charArrayOf(
        '\u3092', '\u3041', '\u3043', '\u3045', '\u3047', '\u3049', '\u3083', '\u3085', '\u3087', '\u3063', '\u30FC', '\u3042', '\u3044', '\u3046',
        '\u3048', '\u304A', '\u304B', '\u304D', '\u304F', '\u3051', '\u3053', '\u3055', '\u3057', '\u3059', '\u305B', '\u305D', '\u305F', '\u3061',
        '\u3064', '\u3066', '\u3068', '\u306A', '\u306B', '\u306C', '\u306D', '\u306E', '\u306F', '\u3072', '\u3075', '\u3078', '\u307B', '\u307E',
        '\u307F', '\u3080', '\u3081', '\u3082', '\u3084', '\u3086', '\u3088', '\u3089', '\u308A', '\u308B', '\u308C', '\u308D', '\u308F', '\u3093')

    val cv = charArrayOf(
        '\u30F4', '\uFF74', '\uFF75', '\u304C', '\u304E', '\u3050', '\u3052', '\u3054', '\u3056', '\u3058', '\u305A', '\u305C', '\u305E', '\u3060',
        '\u3062', '\u3065', '\u3067', '\u3069', '\uFF85', '\uFF86', '\uFF87', '\uFF88', '\uFF89', '\u3070', '\u3073', '\u3076', '\u3079', '\u307C')

    val cs = charArrayOf(
        '\u3071', '\u3074', '\u3077', '\u307A', '\u307D')
}