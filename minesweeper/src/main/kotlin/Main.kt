package org.example

const val rows = 9
const val cols = 9

fun main() {
    print("How many mines do you want on the field? ")
    val totalMines = readln().toInt()
    val game = MinesweeperGame(rows, cols, totalMines)
    game.run()
}