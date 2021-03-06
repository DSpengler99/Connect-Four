/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package ConnectFour


import io.javalin.Javalin
import io.javalin.http.Context

class App {

    var connectFour: ConnectFour = ConnectFour.createNewGame(1) // Anfangswert nicht relevant
    var bussy = false
    val depth = 3 // Tiefe, welche für das Spiel verwendet wird. Wird diese hier geändert, so gilt das für alle Spiele

    init {
        val app = Javalin.create { config ->
config.addStaticFiles("/public")
        }.start(7070)

        app.get("/startGame") { ctx: Context ->
            val startingPlayer = ctx.queryParam("beginner")!!.toInt()
            connectFour = ConnectFour.createNewGame(startingPlayer)
            ctx.result(connectFour.toHTMLString())
        }

        app.get("/sendMove") { ctx: Context ->
            val col = ctx.queryParam("move")!!.toInt()
            if(connectFour.getPossibleMoves().contains(connectFour.height[col]) && !bussy) connectFour = connectFour.makeMove(col)
            ctx.result(connectFour.toHTMLString())
        }

        app.get("/action") { ctx: Context ->
            val action = ctx.queryParam("perform")!!
            if(action == "aiMove" && connectFour.turn == 1 && !connectFour.isGameOver()) {
                val duration = kotlin.system.measureTimeMillis {
                    connectFour = connectFour.makeBestMove(depth)
                }
                println("Die Ausfuehrung des Zuges hat etwa ${duration / 1000}s gedauert.")
            }
            if(action == "bestMove" && !bussy) {
                bussy = true
                val duration = kotlin.system.measureTimeMillis {
                    connectFour = connectFour.makeBestMove(depth)
                }
                println("Die Ausfuehrung des Zuges hat ${duration / 1000}s gedauert.")
                bussy = false
            }
            if(action == "undo" && !bussy) connectFour = connectFour.undoMove().undoMove()
            ctx.result(connectFour.toHTMLString())
        }

        app.get("/test") {ctx: Context ->
            val task = ctx.queryParam("task")!!
            var htmlString: String = ""
            if(task == "prepare") {
                htmlString = "<h2>Tests werden durchgeführt...</h2>\nDie Tests können auf der Konsole verfolgt werden.</p>\n"
            }
            if(task == "run") {
                val duration = kotlin.system.measureTimeMillis {
                    TST().runTests()
                }
                println("Die Ausfuehrung der Tests hat ${duration / 1000}s gedauert.")
                htmlString = "<h2>Tests beendet</h2>\nDie Testergebnisse werden auf der Konsole angezeigt.<br><br> Was möchtest du nun tun?</p>\n"+
                        "<table align=\"center\">\n<tr>\n"+
                        "<td><button class=\"optionButton first\" onclick=\"startGameWithPlayer()\">Ich beginne ein neues Spiel.</button></td>\n"+
                        "<td><button class=\"optionButton second\" onclick=\"startGameWithAI()\">Der Computer beginnt ein neues Spiel</button></td>\n"+
                        "<td><button class=\"optionButton testButton\" onclick=\"prepareTests()\">Tests starten</button></td>\n"+
                        "</tr>\n</table>\n"
            }
            ctx.result(htmlString)
        }
    }
}

fun main(args: Array<String>) {
    App()
}
