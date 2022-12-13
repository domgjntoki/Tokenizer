package data_structures

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConnectionCostImpl() : ConnectionCost {
    private var dbConnection: Connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/mecab/database/costs.db")

    override fun lookup(rightId: Int, leftId: Int): Int {
        val selectSQL = "select cost from COSTS where leftId = $leftId and rightId=$rightId"
        val pst = dbConnection.prepareStatement(selectSQL)
        val result = pst.executeQuery()
        var cost = 99999
        try {
            if(!result.next()) return 99999
            cost = result.getInt("cost")
        } catch (e1: SQLException) {
            e1.printStackTrace()
        } finally {
            pst.close()
        }
        return cost
    }

}