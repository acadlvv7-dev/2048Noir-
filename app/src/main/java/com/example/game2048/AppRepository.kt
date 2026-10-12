package com.asadbek.noir2048

import android.content.Context
import com.asadbek.noir2048.SideEnum

object AppRepository {
    private const val PREFS_NAME = "game2048_new_v1"
    var matrix = Array(4) { IntArray(4) { 0 } }
    var score = 0
    private val history = ArrayList<Array<IntArray>>()
    private val scoreHistory = ArrayList<Int>()
    private var isGameActive = false

    private fun saveHistory() {
        val snapshot = Array(4) { i -> matrix[i].copyOf() }
        history.add(snapshot)
        scoreHistory.add(score)
    }

    fun addNewElement() {
        val list = ArrayList<Pair<Int, Int>>()
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                if (matrix[i][j] == 0) list.add(Pair(i, j))
            }
        }
        if (list.isEmpty()) return
        val random = list.random()
        matrix[random.first][random.second] = if (Math.random() < 0.9) 2 else 4
    }

    fun moveToLeft() {
        saveHistory()
        val temp = Array(4) { IntArray(4) { 0 } }
        var isChanged = false
        for (i in 0 until 4) {
            val ls = ArrayList<Int>()
            var isAdded = false
            for (j in 0 until 4) {
                if (matrix[i][j] == 0) continue
                if (ls.isEmpty()) {
                    ls.add(matrix[i][j])
                } else {
                    if (ls.last() == matrix[i][j] && !isAdded) {
                        ls[ls.lastIndex] = 2 * ls.last()
                        score += ls[ls.lastIndex]
                        isAdded = true
                    } else {
                        ls.add(matrix[i][j])
                        isAdded = false
                    }
                }
            }
            for (k in 0 until ls.size) temp[i][k] = ls[k]
        }
        for (i in 0 until 4) for (j in 0 until 4) if (matrix[i][j] != temp[i][j]) isChanged = true
        matrix = temp
        if (isChanged) addNewElement() else {
            history.removeAt(history.size - 1)
            scoreHistory.removeAt(scoreHistory.size - 1)
        }
    }

    fun moveToUp() {
        saveHistory()
        val temp = Array(4) { IntArray(4) { 0 } }
        var isChanged = false
        for (i in 0 until 4) {
            val ls = ArrayList<Int>()
            var isAdded = false
            for (j in 0 until 4) {
                if (matrix[j][i] == 0) continue
                if (ls.isEmpty()) {
                    ls.add(matrix[j][i])
                } else {
                    if (ls.last() == matrix[j][i] && !isAdded) {
                        ls[ls.lastIndex] = 2 * ls.last()
                        score += ls[ls.lastIndex]
                        isAdded = true
                    } else {
                        ls.add(matrix[j][i])
                        isAdded = false
                    }
                }
            }
            for (k in 0 until ls.size) temp[k][i] = ls[k]
        }
        for (i in 0 until 4) for (j in 0 until 4) if (matrix[i][j] != temp[i][j]) isChanged = true
        matrix = temp
        if (isChanged) addNewElement() else {
            history.removeAt(history.size - 1)
            scoreHistory.removeAt(scoreHistory.size - 1)
        }
    }

    fun moveToRight() {
        saveHistory()
        val temp = Array(4) { IntArray(4) { 0 } }
        var isChanged = false
        for (i in 0 until 4) {
            val ls = ArrayList<Int>()
            var isAdded = false
            for (j in 3 downTo 0) {
                if (matrix[i][j] == 0) continue
                if (ls.isEmpty()) {
                    ls.add(matrix[i][j])
                } else {
                    if (ls.last() == matrix[i][j] && !isAdded) {
                        ls[ls.lastIndex] = 2 * ls.last()
                        score += ls[ls.lastIndex]
                        isAdded = true
                    } else {
                        ls.add(matrix[i][j])
                        isAdded = false
                    }
                }
            }
            for (k in 0 until ls.size) temp[i][3 - k] = ls[k]
        }
        for (i in 0 until 4) for (j in 0 until 4) if (matrix[i][j] != temp[i][j]) isChanged = true
        matrix = temp
        if (isChanged) addNewElement() else {
            history.removeAt(history.size - 1)
            scoreHistory.removeAt(scoreHistory.size - 1)
        }
    }

    fun moveToDown() {
        saveHistory()
        val temp = Array(4) { IntArray(4) { 0 } }
        var isChanged = false
        for (i in 0 until 4) {
            val ls = ArrayList<Int>()
            var isAdded = false
            for (j in 3 downTo 0) {
                if (matrix[j][i] == 0) continue
                if (ls.isEmpty()) {
                    ls.add(matrix[j][i])
                } else {
                    if (ls.last() == matrix[j][i] && !isAdded) {
                        ls[ls.lastIndex] = 2 * ls.last()
                        score += ls[ls.lastIndex]
                        isAdded = true
                    } else {
                        ls.add(matrix[j][i])
                        isAdded = false
                    }
                }
            }
            for (k in 0 until ls.size) temp[3 - k][i] = ls[k]
        }
        for (i in 0 until 4) for (j in 0 until 4) if (matrix[i][j] != temp[i][j]) isChanged = true
        matrix = temp
        if (isChanged) addNewElement() else {
            history.removeAt(history.size - 1)
            scoreHistory.removeAt(scoreHistory.size - 1)
        }
    }

    fun restartGame(context: Context) {
        history.clear()
        scoreHistory.clear()
        score = 0
        isGameActive = true
        for (i in 0 until 4) for (j in 0 until 4) matrix[i][j] = 0
        addNewElement()
        addNewElement()
        saveGameState(context)
    }

    fun finishGame(context: Context) {
        isGameActive = false
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        val recordsStr = prefs.getString("top_records", "")
        val records = if (recordsStr.isNullOrEmpty()) mutableListOf<Int>() 
                      else recordsStr.split(",").map { it.toInt() }.toMutableList()
        
        if (!records.contains(score)) {
            records.add(score)
            val top3 = records.distinct().sortedDescending().take(3)
            prefs.edit().putString("top_records", top3.joinToString(",")).apply()
        }
        
        val savedRecord = prefs.getInt("record", 0)
        if (score > savedRecord) {
            prefs.edit().putInt("record", score).apply()
        }
        saveGameState(context)
    }

    fun getRecord(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedRecord = prefs.getInt("record", 0)
        if (score > savedRecord) {
            prefs.edit().putInt("record", score).apply()
            return score
        }
        return savedRecord
    }

    fun getTop3Records(context: Context): List<Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val recordsStr = prefs.getString("top_records", "")
        return if (recordsStr.isNullOrEmpty()) emptyList() 
               else recordsStr.split(",").map { it.toInt() }
    }

    fun hasSavedGame(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("is_active", false)
    }

    fun saveGameState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("is_active", isGameActive)
        editor.putInt("score", score)
        val matrixStr = StringBuilder()
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                matrixStr.append(matrix[i][j]).append(",")
            }
        }
        editor.putString("matrix", matrixStr.toString())
        editor.apply()
    }

    fun loadGameState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isGameActive = prefs.getBoolean("is_active", false)
        score = prefs.getInt("score", 0)
        val matrixStr = prefs.getString("matrix", "")
        if (!matrixStr.isNullOrEmpty()) {
            val vals = matrixStr.split(",")
            var k = 0
            for (i in 0 until 4) {
                for (j in 0 until 4) {
                    if (k < vals.size - 1) matrix[i][j] = vals[k].toInt()
                    k++
                }
            }
        }
    }

    fun setMusicState(context: Context, state: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("music", state).apply()
    }

    fun getMusicState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("music", true)
    }

    fun undoMove(context: Context) {
        if (history.isNotEmpty()) {
            matrix = history.removeAt(history.size - 1)
            score = scoreHistory.removeAt(scoreHistory.size - 1)
            saveGameState(context)
        }
    }
}