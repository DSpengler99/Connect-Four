package ConnectFour

class TST {

    fun runTests() {
        println("Starte Testszenarios...")
        test1()
        test2()
        test3()
        test4()
        test5()
        println("Alle Tests beendet. \nKehre nun auf die Browseroberflaeche zurueck.")
    }

    fun test1() {
        var connectFour = ConnectFour.createNewGame(1)

        println("Generiere Spielfeld fuer Test #1...")
        connectFour = connectFour.makeMove(3).makeMove(0).makeMove(3).makeMove(0)
                .makeMove(3).makeMove(0)
        println("Generiertes Board: \n${connectFour.toString()}\n")
        connectFour = connectFour.makeBestMove(1)
        println("Spieler ${-connectFour.turn} spielt in Spalte ${connectFour.moveHistory[connectFour.moveHistory.lastIndex]}")
        println("Spielfeld nach Ablauf des Tests:\n${connectFour.toString()}")
        if(connectFour.isGameOver() && connectFour.hasWon()) {
            println("Spieler ${-connectFour.turn} hat gewonnen.\nTest erfolgreich.\n \n")
        } else {
            println("Spieler ${-connectFour.turn} hat nicht gewonnen.\nTest fehlgeschlagen.\n \n")
        }
    }

    fun test2() {
        var connectFour = ConnectFour.createNewGame(1)
        println("Generiere Spielfeld fuer Test #2")
        connectFour = connectFour.makeMove(3).makeMove(0).makeMove(4).makeMove(0)
        println("GeneriertesBoard:\n${connectFour.toString()}")

        // Spiel soll in drei Zügen gewonnen sein
        for(move in 1..3) {
            connectFour = connectFour.makeBestMove(3) // Tiefe 3
            println("Spieler ${-connectFour.turn} spielt in Spalte ${connectFour.moveHistory[connectFour.moveHistory.lastIndex]}.")
            println("Board nach Zug ${move}:\n${connectFour.toString()}\n")
        }
        if(-connectFour.turn == 1 && connectFour.isGameOver() && connectFour.hasWon()) {
            println("Spieler ${-connectFour.turn} hat gewonnen.\nTest erfolgreich.\n\n")
        } else {
            println("Spieler ${-connectFour.turn} hat nicht gewonnen.\nTest fehlgeschlagen.\n\n")
        }
    }

    fun test3() {
        var connectFour = ConnectFour.createNewGame(1)
        println("Generiere Spielfeld fuer Test #3...")
        connectFour = connectFour.makeMove(3).makeMove(5).makeMove(2).makeMove(6)
                .makeMove(5).makeMove(5).makeMove(5).makeMove(1).makeMove(6).makeMove(2)
        println("Generiertes Board:\n${connectFour.toString()}\n")
        for(move in 1..5) {
            connectFour = connectFour.makeBestMove(5)
            println("Spieler ${-connectFour.turn} spielt in Spalte ${connectFour.moveHistory[connectFour.moveHistory.lastIndex]}.")
            println("Board nach Zug ${move}:\n${connectFour.toString()}\n")
        }
        if(-connectFour.turn == 1 && connectFour.isGameOver() && connectFour.hasWon()) {
            println("Spieler ${-connectFour.turn} hat gewonnen.\nTest erfolgreich.\n\n")
        } else {
            println("Spieler ${-connectFour.turn} hat nicht gewonnen.\nTest fehlgeschlagen.\n \n")
        }
    }

    fun test4() {
        var connectFour = ConnectFour.createNewGame(-1) // Spieler beginnt Spiel
        println("Generiere Spielfeld fuer Test #4...")
        connectFour = connectFour.makeMove(3).makeMove(0).makeMove(3).makeMove(0).makeMove(3)
        println("Generiertes Board:\n${connectFour.toString()}")
        for(move in 1..2) {
            connectFour = connectFour.makeBestMove(2)
            println("Spieler ${-connectFour.turn} spielt in Spalte ${connectFour.moveHistory[connectFour.moveHistory.lastIndex]}.")
            println("Board nach Zug ${move}:\n${connectFour.toString()}\n")
        }
        if(!connectFour.hasWon()) {
            println("Siegeszug von Spieler ${-connectFour.turn} wurde blockiert.\nTest erfolgreich.\n\n")
        } else {
            println("Der Siegeszug des Spielers ${-connectFour.turn} wurde nicht blockiert.\nTest fehlgeschlagen.\n\n")
        }
    }

    fun test5() {
        var connectFour = ConnectFour.createNewGame(-1)
        println("Generiere Spielfeld fuer Test #5")
        connectFour = connectFour.makeMove(3).makeMove(0).makeMove(4)
        println("Generiertes Board:\n${connectFour.toString()}")
        for(move in 1..4) {
            connectFour = connectFour.makeBestMove(4)
            println("Spieler ${-connectFour.turn} spielt in Spalte ${connectFour.moveHistory[connectFour.moveHistory.lastIndex]}.")
            println("Board nach Zug ${move}:\n${connectFour.toString()}\n")
        }
        if(!connectFour.hasWon()) {
            println("Siegeszug von ${-connectFour.turn} blockiert.\nTest erfolgreich.\n\n")
        } else {
            println("Siegeszug von ${-connectFour.turn} wurde nicht blockiert.\nTest fehlgeschlagen.")
        }
    }
}