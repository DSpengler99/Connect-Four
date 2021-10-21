package ConnectFour

import java.util.*
import kotlin.math.max
import kotlin.random.Random

class ConnectFour private constructor(val bitboards: Pair<Long, Long>, val height: List<Int>, val moveHistory: List<Int>, val counter: Int, val turn: Int): ConnectFourGame     {

    companion object {
        fun createNewGame(beginner: Int): ConnectFour {
            return ConnectFour(
                    Pair(0L, 0L),
                    listOf(0, 7, 14, 21, 28, 35, 42),
                    listOf(), 0, beginner
            )
        }
    }

    // Ternary operator: https://stackoverflow.com/questions/16336500/kotlin-ternary-conditional-operator
    public override fun makeMove(col: Int): ConnectFour {
        val move: Long = 1L shl height[col]
        val newHeight: List<Int> = height.take(col) + listOf(height[col] + 1) + height.takeLast(6 - col)
        val newBitboards: Pair<Long, Long> =
                if (counter == 0 || counter % 2 == 0) Pair(bitboards.first xor move, bitboards.second)
                else Pair(bitboards.first, bitboards.second xor move)
        val newMoveHistory: List<Int> = moveHistory + listOf(col)
        return ConnectFour(newBitboards, newHeight, newMoveHistory, counter + 1, turn * -1)
    }

    override fun hasWon(): Boolean {
        val directions: List<Int> = listOf(1, 6, 7, 8)
        for (direction in directions) {
            if (bitboards.first and (bitboards.first shr direction) and (bitboards.first shr (2 * direction)) and (bitboards.first shr (3 * direction)) != 0L) return true
            if (bitboards.second and (bitboards.second shr direction) and (bitboards.second shr (2 * direction)) and (bitboards.second shr (3 * direction)) != 0L) return true
        }
        return false
    }

    override fun isGameOver(): Boolean {
        if (counter == 42 || hasWon()) return true
        return false
    }

    override fun makeRandomMove(): ConnectFour {
        val possibleMoves = getPossibleMoves()
        val m: Int = Random.nextInt(possibleMoves.size)
        return makeMove(m)
    }

    override fun makeBestMove(depth: Int): ConnectFour {
        val moves = getPossibleMoves()
        val bestMove = minimax(this, moves, depth = depth)
        return makeMove(bestMove)
    }

    private fun minimax(connectFour: ConnectFour, moves: List<Int>, memory: Map<Triple<Long, Long, Int>, Int> = mapOf(),
                        value: Int = Int.MIN_VALUE, bestMoves: List<Int> = listOf(), depth: Int,
    neededMoves: Int = 0, shortestPath: Int = Int.MAX_VALUE
    ): Int {
        if(moves.size == 0) {
            return if(bestMoves.isEmpty()) makeRandomMove().moveHistory[moveHistory.lastIndex] else bestMoves[Random.nextInt(bestMoves.size)]
        }
        val result = min(connectFour.makeMove(connectFour.height.indexOf(moves[moves.lastIndex])), depth - 1, memory = memory,
                neededMoves = neededMoves+1, shortestPath = shortestPath)
        if(result.first > value || (result.first >= value && result.third < shortestPath)) return minimax(connectFour, moves.take(moves.size-1),
                result.second, result.first,
                if(result.first == value)bestMoves + listOf(height.indexOf(moves[moves.lastIndex]))
                else listOf(height.indexOf(moves[moves.lastIndex])),
                depth, shortestPath = result.third)
        return minimax(connectFour, moves.take(moves.size-1),
                result.second, value, bestMoves, depth, shortestPath = shortestPath)
    }

    private fun max(connectFour: ConnectFour, depth: Int, maxValue: Int = Int.MIN_VALUE,
                    moves: List<Int> = connectFour.getPossibleMoves(), memory: Map<Triple<Long, Long, Int>, Int>,
                    neededMoves: Int, shortestPath: Int
    ): Triple<Int, Map<Triple<Long, Long, Int>, Int>, Int> {
        val boardKey: Triple<Long, Long, Int> = Triple(connectFour.bitboards.first, connectFour.bitboards.second, connectFour.turn)
        if(memory.containsKey(boardKey)) return Triple(memory[boardKey]!!, memory, neededMoves)
        if(depth == 0 || connectFour.isGameOver()) return Triple(evaluateGame(connectFour, turn), memory, neededMoves)
        if(moves.isEmpty()) return Triple(maxValue, memory+ mapOf(Pair(boardKey, maxValue)), shortestPath)
        val result = min(connectFour.makeMove(connectFour.height.indexOf(moves[moves.lastIndex])), depth-1, memory = memory,
        neededMoves = neededMoves+1, shortestPath = shortestPath)
        return max(connectFour, depth,
                if(result.first>maxValue) result.first else if(result.third < shortestPath && result.first >= maxValue) result.first+1 else maxValue,
                moves.take(moves.size-1), result.second, neededMoves = neededMoves,
                shortestPath = if((result.third < shortestPath && result.first >= maxValue) || result.first > maxValue) result.third else shortestPath)
    }

    private fun min(connectFour: ConnectFour, depth: Int, minValue: Int = Int.MAX_VALUE,
                    moves: List<Int> = connectFour.getPossibleMoves(), memory: Map<Triple<Long, Long, Int>, Int>,
                    neededMoves: Int, shortestPath: Int
    ): Triple<Int, Map<Triple<Long, Long, Int>, Int>, Int> {
        val boardKey: Triple<Long, Long, Int> = Triple(connectFour.bitboards.first, connectFour.bitboards.second, connectFour.turn)
        if(memory.containsKey(boardKey)) return Triple(memory[boardKey]!!, memory, neededMoves)
        if(depth == 0 || connectFour.isGameOver()) return Triple(evaluateGame(connectFour, turn), memory, neededMoves)
        if(moves.isEmpty()) return Triple(minValue, memory+ mapOf(Pair(boardKey, minValue)), shortestPath)
        val result = max(connectFour.makeMove(connectFour.height.indexOf(moves[moves.lastIndex])), depth-1, memory = memory,
                neededMoves = neededMoves+1, shortestPath = shortestPath)
        return min(connectFour, depth,
                if(result.first< minValue) result.first else if(result.third < shortestPath && result.first <= minValue) result.first-1 else minValue,
                moves.take(moves.size-1), result.second, neededMoves = neededMoves,
                shortestPath = if((result.third < shortestPath && result.first <= minValue) || result.first < minValue) result.third else shortestPath)
    }

    // playRandomly() und monteCarloTreeSearch() stellen gemeinsam die Monte-Carlo-Tree-Search dar
    // Die Idee für die statistische Bewertung kam von: https://www.youtube.com/watch?v=CjldSexfOuU
    private fun playRandomly(connectFour: ConnectFour, wantsToWin: Int): Int {
        if(!connectFour.isGameOver()) {
            return playRandomly(connectFour.makeRandomMove(), wantsToWin)
        } else {
            if(-1 * wantsToWin == connectFour.turn && connectFour.hasWon()) return 1
            else if(connectFour.hasWon()) return -1
            else return 0
        }
    }

    private fun monteCarloTreeSearch(connectFour: ConnectFour, wantsToWin: Int, numberOfGames: Int, scores: Triple<Int, Int, Int> = Triple(0, 0, 0)): Triple<Int, Int, Int> {
        if(numberOfGames == 0) return scores
        val winner = playRandomly(connectFour, wantsToWin)
        val newTriple: Triple<Int, Int, Int> = if(winner == 1) {
            Triple(scores.first, scores.second, scores.third+1)
        } else if(winner == -1) {
            Triple(scores.first+1, scores.second, scores.third)
        } else {
            Triple(scores.first, scores.second+1, scores.third)
        }
        return monteCarloTreeSearch(connectFour, wantsToWin, numberOfGames-1, newTriple)
    }

    private fun evaluateGame(connectFour: ConnectFour, wantsToWin: Int, numberOfGames: Int = 75): Int {
        if(connectFour.isGameOver() && connectFour.hasWon() && connectFour.turn != wantsToWin) return numberOfGames+1
        if(connectFour.isGameOver() && connectFour.hasWon() && connectFour.turn == wantsToWin) return -numberOfGames
        if(connectFour.isGameOver() && connectFour.hasWon() == false) return 0
        return monteCarloTreeSearch(connectFour, wantsToWin, numberOfGames).third // Das Spiel war nicht direkt vorbei, also finden des Scores mit Monte-Carlo
    }

    override fun getPossibleMoves(): List<Int> {
        val top: Long = 0b1000000_1000000_1000000_1000000_1000000_1000000_1000000L
        return height.filter { (top and (1L shl it)) == 0L }
    }

    override fun undoMove(): ConnectFour {
        val col = moveHistory[counter - 1]
        val move: Long = 1L shl height[col] - 1
        val newBitboards: Pair<Long, Long> =
                if (counter - 1 == 0 || (counter - 1) % 2 == 0) {
                    Pair(bitboards.first xor move, bitboards.second)
                } else {
                    Pair(bitboards.first, bitboards.second xor move)
                }
        val newHeight: List<Int> = height.take(col) + listOf(height[col] - 1) + height.takeLast(6 - col)
        val newMoveHistory = moveHistory.dropLast(1)
        return ConnectFour(newBitboards, newHeight, newMoveHistory, counter - 1, turn * -1)
    }

    override fun toString(): String {
        return toConsoleString()
    }

    private fun toConsoleString(row: Int = 5, column: Int = 0): String {
        val cellOwner = getOwner((column*7)+row)
        val cell: String = if(cellOwner == "first") "X" else if(cellOwner == "second") "O" else " "
        if(row == 0 && column == 6) return " $cell |\n"
        if(column < 6) return if(column == 0 && row == 5)"| $cell |"+toConsoleString(row, column+1) else " $cell |"+toConsoleString(row, column+1)
        return " $cell |\n|"+toConsoleString(row-1, 0)
    }

    fun toHTMLString(): String {
        val gameTable = "<table align=\"center\" id=\"board\">\n"+generateBoardTableRows()+"</table>\n"
        if(isGameOver()) {
            val winner = if(turn == 1 && hasWon()) {
                "<h2>Du hast gewonnen! Herzlichen Glückwunsch.</h2>\n"
            } else if (turn == -1 && hasWon()) {
                "<h2>Der Computer hat gewonnen! Viel Erfolg beim nächsten Mal.</h2>\n"
            } else "<h2>Das Spiel ist unentschieden ausgegangen!</h2>"
            val restart = "<p>Wer soll das nächste Spiel beginnen?</p>\n"
            val options = "<table align=\"center\">\n <tr>\n <td><button class=\"optionButton first\" onclick=\"startGameWithPlayer()\">Ich beginne.</button></td>\n"+
                    "<td><button class=\"optionButton second\" onclick=\"startGameWithAI()\">Der Computer beginnt</button></td>\n"+
            "<td><button class=\"optionButton testButton\" onclick=\"prepareTests()\">Tests starten</button></td>"+
            "</tr>\n </table>\n"
            return winner+restart+options+gameTable
        }
        val playerInfo = if (turn == -1) {
            "<h2>Du bist am Zug</h2>\n" +
                    "<p>Mache einen Zug oder wähle eine untenstehende Option aus.</p>\n"
        } else {
            "<h2>Der Computer ist am Zug</h2>\n" +
                    "<p>Warte bis der Computer gezogen hat.</p>\n"
        }
        val columns: List<String> = if (turn == -1) {
            List(7) {
                if(getPossibleMoves().contains(height[it])) {
                    "<td><button class=\"columnButton\" onclick=\"sendColumn($it)\">${it + 1}</button></td>\n"
                } else {
                    "<td></td>"
                }
            }
        } else {
            List(7) { "<td></td>\n" }
        }
        val columnButtons = "<table align=\"center\" id=\"columnButtons\">\n <tr>\n"+columns.joinToString(separator = "")+"</tr>\n </table>\n"
        val undoButton: String = if(counter >= 2) {
            "<td><button class=\"optionButton undoButton\" onclick=\"requestUndo()\">Rückgängig</button></td>\n"
        } else {
            "<td></td>\n"
        }
        val bestButton = "<td><button class=\"optionButton bestButton\" onclick=\"requestBestMove()\">Besten Zug machen</button></td>\n"
        val optionBar: String = if(turn == -1) {
            "<table align=\"center\">\n <tr> \n $undoButton $bestButton </tr>\n </table>\n"
        } else {
            ""
        }
        return playerInfo + columnButtons+gameTable+optionBar

    }

    private fun  generateBoardTableRows(row: Int = 5): String {
        val currentRow: List<String> = List(7) {
            String.format("<td><button class=\"gameButton %s\"></button></td>\n", getOwner((it * 7) + row))
        }
        if(row == 0) return "<tr>\n"+currentRow.joinToString(separator = "")+"</tr>\n"
        return "<tr>\n"+currentRow.joinToString(separator = "")+"</tr>\n"+generateBoardTableRows(row-1)
    }

    private fun getOwner(position: Int): String {
        if (bitboards.first and (1L shl position) != 0L) return "first"
        else if (bitboards.second and (1L shl position) != 0L) return "second"
        else return ""
    }
} 