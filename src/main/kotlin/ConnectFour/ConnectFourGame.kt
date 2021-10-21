package ConnectFour

interface ConnectFourGame {
    fun makeMove(col: Int): ConnectFourGame
    fun makeBestMove(depth: Int): ConnectFourGame
    fun makeRandomMove(): ConnectFourGame
    fun undoMove(): ConnectFourGame
    fun getPossibleMoves(): List<Int>
    fun isGameOver(): Boolean
    fun hasWon(): Boolean
    override fun toString(): String
}