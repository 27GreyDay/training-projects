package org.example

data class Cell(
    var hasMine: Boolean = false,
    private var _showMine: Boolean = false,
    var isMarked: Boolean = false,
    var isExplored: Boolean = false,
    var adjacentMines: Int = 0
) {
    val display: Char
        get() = when {
            isMarked -> '*'
            _showMine && hasMine -> 'X'
            !isExplored -> '.'
            adjacentMines == 0 -> '/'
            else -> '0' + adjacentMines
        }
    fun revealMine(): Char {
        _showMine = true
        return display
    }
}
