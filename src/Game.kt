fun calculateEmptyPlaces(numLines: Int, numColumns: Int) = numLines * numColumns - 2

fun calculateNumMinesForGameConfiguration(numLines: Int, numColumns: Int): Int? {
    val emptyPlaces = calculateEmptyPlaces(numLines, numColumns)

    if (emptyPlaces <= 0) return null

    val numMines = when (emptyPlaces) {
        in 14..20 -> 6
        in 21..40 -> 9
        in 41..60 -> 12
        in 61..79 -> 19
        else -> null
    }
    return numMines
}

fun drawTerrainWithLegend(numLines: Int, numColumns: Int, numMines: Int, withColor: Boolean = false): String {
    var terrain = ""

    val esc: String = "\u001B"
    var legendColor = "$esc[97;44m"
    var endLegendColor = "$esc[0m"

    if (!withColor) {
        legendColor = ""
        endLegendColor = ""
    }

    // Draw Legend
    terrain += "$legendColor    ${createLegend(numColumns)}    $endLegendColor\n"

    var lineCounter = 1
    while(lineCounter <= numLines) {
        var columnCounter = numColumns
        var mineCounter = numMines

        // Draw game artifacts by column for each line
        while (columnCounter > 0) {
            // If its 1st column, draw player
            if (columnCounter == numColumns) {
                terrain += "$legendColor $lineCounter $endLegendColor P "
                columnCounter--

            // If its last column, draw finish
            } else if (columnCounter == 1) {
                terrain += "| f $legendColor   $endLegendColor\n"

                // Add spaces below terrain
                var spacesBelow = "$legendColor "
                var spacesCounter = 0
                while (spacesCounter < (numColumns + 1) * 4) {
                    spacesBelow += " "
                    spacesCounter++
                }
                spacesBelow += "$endLegendColor"
                terrain += spacesBelow

                columnCounter--
            } else {
                // Draw mines
                if (mineCounter > 0) {
                    terrain += "| * "
                    mineCounter--
                    columnCounter--

                // Draw empty places
                } else {
                    terrain += "|   "
                    columnCounter--
                }
            }
            lineCounter++
        }
    }

    return terrain
}

fun drawTerrainWithNoLegend(numLines: Int, numColumns: Int, numMines: Int): String {
    var terrain = ""

    var lineCounter = 1
    while (lineCounter <= numLines) {
        var columnCounter = numColumns
        var mineCounter = numMines

        // Draw game artifacts by column for each line
        while (columnCounter > 0) {
            // If its 1st column, draw player
            if (columnCounter == numColumns) {
                terrain += " P "
                columnCounter--

            // If its last column, draw finish
            } else if (columnCounter == 1) {
                terrain += "| f "
                columnCounter--
            } else {
                // Draw mines
                if (mineCounter > 0) {
                    terrain += "| * "
                    mineCounter--
                    columnCounter--

                // Draw empty places
                } else {
                    terrain += "|   "
                    columnCounter--
                }
            }
        }
        lineCounter++
    }

    return terrain
}

fun makeTerrain(matrixTerrain: Array<Array<Pair<String,Boolean>>>, showLegend: Boolean = true,
                withColor: Boolean = false, showEverything: Boolean): String {
    return ""
}

fun createMatrixTerrain(numLines: Int, numColumns: Int, numMines: Int, ensurePathToWin: Boolean): Array<Array<Pair<String,Boolean>>> {
    return Array(2){ Array(2){ Pair("", false) } }
}

fun countNumberOfMinesCloseToCurrentCell(matrixTerrain: Array<Array<Pair<String, Boolean>>>, centerY: Int, centerX: Int): Int {
    return 0
}

fun fillNumberOfMines(matrixTerrain: Array<Array<Pair<String, Boolean>>>) {

}

fun revealMatrix(matrixTerrain: Array<Array<Pair<String, Boolean>>>, coordY: Int, coordX: Int, endGame: Boolean = false) {

}

fun isEmptyAround(matrixTerrain: Array<Array<Pair<String, Boolean>>>, centerY: Int, centerX: Int, yl: Int, xl: Int, yr: Int, xr: Int): Boolean {
    return false
}

fun isMovementPValid(currentCoord: Pair<Int,Int>, targetCoord: Pair<Int,Int>): Boolean {
    return false
}

fun isCoordinateInsideTerrain(coord: Pair<Int,Int>, numColumns: Int, numLines: Int): Boolean {
    return false
}

fun getSquareAroundPoint(linha: Int, coluna: Int, numLines: Int, numColumns: Int): Pair<Pair<Int,Int>, Pair<Int,Int>> {
    return Pair(Pair(0,0), Pair(0,0))
}

fun createLegend(numColumns: Int): String {
    var letter = 'A'
    var count = 0
    var legend = ""
    while (count < numColumns) {
        if (count == numColumns - 1) {
            legend += letter
        } else {
            legend += "$letter   "
        }
        letter++
        count++
    }
    return legend
}

fun isValidGameMinesConfiguration(numLines: Int, numColumns: Int, numMines: Int): Boolean {
    val emptyPlaces = calculateEmptyPlaces(numLines, numColumns)
    return !(numMines <= 0 || emptyPlaces < numMines)
}