package org.example

import kotlin.random.Random

class MinesweeperGame(
    private val rows: Int,
    private val cols: Int,
    private val mines: Int
) {
    private val field = List(rows) { List(cols) { Cell() } }
    private var correctlyMarkedMines = 0
    private var firstMove = true
    private var loss = false
    private var exploredCells = 0

    fun run() {
        while (!checkWin()) {
            printField()
            handleUserInput()
        }
    }

    private fun checkWin(): Boolean {
        return correctlyMarkedMines == mines || loss || exploredCells + mines == rows * cols
    }

    private fun handleUserInput() {
        while (true) {
            print("Set/unset mines marks or claim a cell as free: ")
            val input = readln().split(' ')

            try {
                val x = input[0].toInt() - 1
                val y = input[1].toInt() - 1

                when(input[2]) {
                    "free" -> free(x, y)
                    "mine" -> putOrRemoveMarker(field[y][x])
                    else -> println("Invalid input")
                }
                break
            } catch (e: Exception) {
                println("Invalid input")
            }
        }
    }
    private fun free(x: Int, y: Int) {
        if (firstMove) {
            // Генерируем ячейку без мины
            generateMinefield(x, y)
            calculateAdjacency()
            firstMove = false
        }
        if (field[y][x].hasMine) {
            printField(showMines = true)
            println("You stepped on a mine and failed!")
            loss = true
            return
        } else {
            explore(x, y)
            if (checkWin()) {
                println("Congratulations! You found all the mines!")
                printField(showMines = true)
            }
        }
    }

    private fun explore(x: Int, y: Int) {
        if (x !in 0 until cols || y !in 0 until rows) return

        val cell = field[y][x]
        if (cell.isExplored || cell.isMarked || cell.hasMine) return

        cell.isExplored = true
        exploredCells++

        if (cell.adjacentMines == 0) {
            explore(x, y - 1) // вверх
            explore(x, y + 1) // вниз
            explore(x - 1, y) // влево
            explore(x + 1, y) // вправо
        }
    }

    private fun putOrRemoveMarker(cell: Cell) {
        if (cell.isExplored) {
            println("The cell has already been investigated")
            return
        }

        cell.isMarked = !cell.isMarked

        correctlyMarkedMines += when {
            cell.hasMine && cell.isMarked -> +1
            cell.hasMine && !cell.isMarked -> -1
            !cell.hasMine && cell.isMarked -> -1
            else -> +1
        }

        if (checkWin()) {
            printField()
            println("Congratulations! You found all the mines!")
        }
    }

    private fun printField(showMines: Boolean = false) {
        println("\n |${(1..rows).joinToString("")}|")
        println("—|${"—".repeat(rows)}|")

        for (rowIndex in 0 until rows) {
            val rowString = buildString {
                for (cell in field[rowIndex])
                    append(if (showMines) cell.revealMine() else cell.display)
            }
            println("${rowIndex + 1}│$rowString│")
        }

        println("—|${"—".repeat(rows)}|")
    }

    private fun generateMinefield(x: Int, y: Int) {
        var totalMines = mines

        while (totalMines > 0) {
            val row = Random.nextInt(rows)
            val col = Random.nextInt(cols)
            val cell = field[row][col]
            if (!cell.hasMine && row != y && col != x) {
                cell.hasMine = true
                totalMines--
            }
        }
    }

    private  fun calculateAdjacency() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (!field[i][j].hasMine) continue

                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if (dx == 0 && dy == 0) continue  // не трогаем саму мину

                        val ni = i + dy
                        val nj = j + dx

                        if (ni in 0 until rows && nj in 0 until cols) {
                            val neighbor = field[ni][nj]
                            if (!neighbor.hasMine) {
                                neighbor.adjacentMines++
                            }
                        }
                    }
                }
            }
        }
    }
}