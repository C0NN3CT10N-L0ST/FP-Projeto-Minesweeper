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

fun drawTerrainWithLegend(matrixTerrain: Array<Array<Pair<String, Boolean>>>, showEverything: Boolean = false, withColor: Boolean = false): String {
    var terrain = ""
    val numLines = matrixTerrain.size
    val numColumns = matrixTerrain[0].size

    val esc: String = "\u001B"
    var legendColor = "$esc[97;44m"
    var endLegendColor = "$esc[0m"

    if (!withColor) {
        legendColor = ""
        endLegendColor = ""
    }

    // Draw Legend
    terrain += "$legendColor    ${createLegend(numColumns)}    $endLegendColor\n"

    for (line in 0 until numLines) {
        // Draw line number
        terrain += "$legendColor ${line + 1} $endLegendColor"

        // Draw game artifacts by column for each line
        for (col in 0 until numColumns) {
            // If its last column, don't draw pipe
            if (col == numColumns - 1) {
                if (showEverything || matrixTerrain[line][col].second) {
                    terrain += " ${matrixTerrain[line][col].first} $legendColor   $endLegendColor\n"
                } else {
                    terrain += "   $legendColor   $endLegendColor\n"
                }
            } else {
                if (showEverything || matrixTerrain[line][col].second) {
                    terrain += " ${matrixTerrain[line][col].first} |"
                } else {
                    terrain += "   |"
                }
            }
        }

        // Draw separator below column, except if its last line
        if (line != numLines - 1) {
            terrain += "$legendColor   $endLegendColor" + "---+".repeat(numColumns - 1) + "---$legendColor   $endLegendColor\n"
        }
    }

    // Add spaces below terrain
    var spacesBelow = "$legendColor "
    var spacesCounter = 0
    while (spacesCounter < (numColumns + 1) * 4) {
        spacesBelow += " "
        spacesCounter++
    }
    spacesBelow += "$endLegendColor"
    terrain += spacesBelow

    return terrain
}

fun drawTerrainWithNoLegend(matrixTerrain: Array<Array<Pair<String, Boolean>>>, showEverything: Boolean = false): String {
    var terrain = ""
    val numLines = matrixTerrain.size
    val numColumns = matrixTerrain[0].size

    for (line in 0 until numLines) {
        // Draw game artifacts by column for each line
        for (col in 0 until numColumns) {
            // If its last column, don't draw pipe
            if (col == numColumns - 1) {
                if (showEverything || matrixTerrain[line][col].second) {
                    if (line == numLines - 1) {
                        terrain += " ${matrixTerrain[line][col].first} "
                    } else {
                        terrain += " ${matrixTerrain[line][col].first} \n"
                    }
                } else {
                    if (line == numLines - 1) {
                        terrain += "   "
                    } else {
                        terrain += "   \n"
                    }
                }
            } else {
                if (showEverything || matrixTerrain[line][col].second) {
                    terrain += " ${matrixTerrain[line][col].first} |"
                } else {
                    terrain += "   |"
                }
            }
        }

        // Draw separator below column, except if its last line
        if (line != numLines - 1) {
            terrain += "---+".repeat(numColumns - 1) + "---\n"
        }
    }
    return terrain
}

fun makeTerrain(matrixTerrain: Array<Array<Pair<String,Boolean>>>, showLegend: Boolean = true,
                withColor: Boolean = false, showEverything: Boolean = false): String {
    if (showLegend) return drawTerrainWithLegend(matrixTerrain, showEverything, withColor)

    return drawTerrainWithNoLegend(matrixTerrain, showEverything)
}

// Returns the coordinates of the square around the given point
fun getSquareAroundPoint(linha: Int, coluna: Int, numLines: Int, numColumns: Int): Pair<Pair<Int,Int>, Pair<Int,Int>> {
    // Guaranties that the square does not exceed the matrix limits
    val yTopLeft = if (linha - 1 < 0) linha else linha - 1
    val yBottomRight = if (linha + 1 > numLines - 1) linha else linha + 1
    val xTopLeft = if (coluna - 1 < 0) coluna else coluna - 1
    val xBottomRight = if (coluna + 1 > numColumns - 1) coluna else coluna + 1

    return Pair(Pair(yTopLeft,xTopLeft), Pair(yBottomRight,xBottomRight))
}

fun createMatrixTerrain(numLines: Int, numColumns: Int, numMines: Int, ensurePathToWin: Boolean = false):
        Array<Array<Pair<String,Boolean>>> {
    // Creates the matrix
    val matrix = Array(numLines) { Array(numColumns) { Pair(" ", false) } }
    // Places the player 'P' and finish 'f'
    matrix[0][0]= Pair("P", true)
    matrix[numLines-1][numColumns-1]= Pair("f", true)
    var mines = numMines

    if (ensurePathToWin) {
        // Place mines semi-randomly, always possible to win
        while (mines > 0) {
            val randomLine = (0 until numLines).random()
            val randomColumn = (0 until numColumns).random()
            val square = getSquareAroundPoint(randomLine, randomColumn, numLines, numColumns)
            val emptyAroundPoint = isEmptyAround(matrix, randomLine, randomColumn,
                square.first.first, square.first.second, square.second.first, square.second.second)

            // Verify if the randomly generated point is not the player place 'P' or finish 'f' and all places around it
            // are empty
            if (matrix[randomLine][randomColumn].first != "P" && matrix[randomLine][randomColumn].first != "f"
                && matrix[randomLine][randomColumn].first != "*" && emptyAroundPoint) {
                matrix[randomLine][randomColumn] = Pair("*", false)
                mines--
            }
        }
    } else {
        // Place mines randomly, could result in impossible game
        while(mines > 0){
            val randomLine = (0 until numLines).random()
            val randomColumn = (0 until numColumns).random()
            if (matrix[randomLine][randomColumn].first == " "){
                matrix[randomLine][randomColumn] = Pair("*", false)
                mines--
            }
        }
    }

    return matrix
}

// Counts the number of mines close to a certain matrix cell using the 'getSquareAroundPoint' function to get the square
// around the cell and iterates through it in the matrix, skipping the center cell since we only want the mines around it
fun countNumberOfMinesCloseToCurrentCell(matrixTerrain: Array<Array<Pair<String, Boolean>>>, centerY: Int, centerX: Int): Int {
    val numLines = matrixTerrain.size
    val numColumns = matrixTerrain[0].size
    val square = getSquareAroundPoint(centerY,centerX,numColumns,numLines)

    // Get square coordinates
    val yTopLeft = square.first.first
    val yBottomRight = square.second.first
    val xTopLeft = square.first.second
    val xBottomRight = square.second.second

    var mines = 0

    for (line in yTopLeft..yBottomRight){
        for (column in xTopLeft..xBottomRight){
            if(!(line == centerY && column == centerX) && matrixTerrain[line][column].first == "*"){
                mines++
            }
        }
    }
    return mines
}

// Places number of mines around each cell
fun fillNumberOfMines(matrixTerrain: Array<Array<Pair<String, Boolean>>>) {
    val numLines = matrixTerrain.size
    val numColumns = matrixTerrain[0].size
    for(line in 0 until numLines){
        for(column in 0 until numColumns){
            val mines = countNumberOfMinesCloseToCurrentCell(matrixTerrain, line, column)
            if(matrixTerrain[line][column].first == " " && mines > 0){
                matrixTerrain[line][column] = Pair("$mines", false)
            }
        }
    }
}

fun revealMatrix(matrixTerrain: Array<Array<Pair<String, Boolean>>>, coordY: Int, coordX: Int, endGame: Boolean = false) {
    val numLines = matrixTerrain.size
    val numColumns = matrixTerrain[0].size

    val square = getSquareAroundPoint(coordY, coordX, numLines, numColumns)
    val yTopLeft = square.first.first
    val xTopLeft = square.first.second
    val yBottomRight = square.second.first
    val xBottomRight = square.second.second

    for (line in yTopLeft..yBottomRight) {
        for (col in xTopLeft..xBottomRight) {
            if (endGame) {
                matrixTerrain[line][col] = Pair(matrixTerrain[line][col].first, true)
            } else if (matrixTerrain[line][col].first != "*") {
                matrixTerrain[line][col] = Pair(matrixTerrain[line][col].first, true)
            }
        }
    }
}

fun isEmptyAround(matrixTerrain: Array<Array<Pair<String, Boolean>>>, centerY: Int, centerX: Int, yl: Int, xl: Int, yr: Int, xr: Int): Boolean {
    for (line in yl..yr) {
        for (col in xl..xr) {
            // Returns false if the square around the point is not empty
            val cellString = matrixTerrain[line][col].first
            if (!(line == centerY && col == centerX) && (cellString == "*" || cellString == "P" || cellString == "f")) return false
        }
    }
    return true
}

fun isMovementPValid(currentCoord: Pair<Int,Int>, targetCoord: Pair<Int,Int>): Boolean {
    val currentY = currentCoord.first
    val currentX = currentCoord.second
    val targetY = targetCoord.first
    val targetX = targetCoord.second

    return targetY in currentY - 1..currentY + 1 && targetX in currentX - 1..currentX + 1
}

fun isCoordinateInsideTerrain(coord: Pair<Int,Int>, numColumns: Int, numLines: Int): Boolean {
    return coord.second in 0 until numColumns && coord.first in 0 until numLines
}

fun isValidGameMinesConfiguration(numLines: Int, numColumns: Int, numMines: Int): Boolean {
    val emptyPlaces = calculateEmptyPlaces(numLines, numColumns)
    return !(numMines <= 0 || emptyPlaces < numMines)
}