fun calculateEmptyPlaces(numLines: Int, numColumns: Int) = numLines * numColumns - 2

fun calculateNumMinesForGameConfiguration(numLines: Int, numColumns: Int): Int? {
    val emptyPlaces = calculateEmptyPlaces(numLines, numColumns)

    if (emptyPlaces <= 0) return null

    val numMines = when (emptyPlaces) {
        1 -> 1
        in 2..6 -> 2
        in 6..11 -> 3
        in 11..21 -> 6
        in 21..51 -> 10
        else -> 15
    }
    return numMines
}

// fun makeTerrain(numLines: Int, numColumns: Int, numMines: Int, showLegend: Boolean = true, withColor: Boolean = false): String {
//
// }

fun createLegend(numColumns: Int): String {
    var letter = 'A'
    var count = 0
    var legend = ""
    while (count < numColumns) {
        legend += letter
        letter++
        count++
    }
    return legend
}

fun isValidGameMinesConfiguration(numLines: Int, numColumns: Int, numMines: Int): Boolean {
    val emptyPlaces = calculateEmptyPlaces(numLines, numColumns)
    return !(numMines <= 0 || emptyPlaces < numMines)
}