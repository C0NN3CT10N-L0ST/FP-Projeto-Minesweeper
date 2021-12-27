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
fun drawTerrainWithLegend(matrixTerrain: Array<Array<Pair<String, Boolean>>>, withColor: Boolean = false): String {
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

    // Adds the number of close mines to each empty place
    fillNumberOfMines(matrixTerrain)

    // Draw Legend
    terrain += "$legendColor    ${createLegend(numColumns)}    $endLegendColor\n"

    for (line in 0 until numLines) {
        // Draw line number
        terrain += "$legendColor ${line + 1} $endLegendColor"

        // Draw game artifacts by column for each line
        for (col in 0 until numColumns) {
            // If its last column, don't draw pipe
            if (col == numColumns - 1) {
                terrain += " ${matrixTerrain[line][col].first} $legendColor   $endLegendColor\n"
            } else {
                terrain += " ${matrixTerrain[line][col].first} |"
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

fun drawTerrainWithNoLegend(matrixTerrain: Array<Array<Pair<String, Boolean>>>): String {
    var terrain = ""
    val numLines = matrixTerrain.size
    val numColumns = matrixTerrain[0].size

    // Adds the number of close mines to each empty place
    fillNumberOfMines(matrixTerrain)

    for (line in 0 until numLines) {
        // Draw game artifacts by column for each line
        for (col in 0 until numColumns) {
            // If its last column, don't draw pipe
            if (col == numColumns - 1) {
                terrain += " ${matrixTerrain[line][col].first} \n"
            } else {
                terrain += " ${matrixTerrain[line][col].first} |"
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
    if (showLegend) return drawTerrainWithLegend(matrixTerrain, withColor)

    return drawTerrainWithNoLegend(matrixTerrain)
}

// Returns the coordinates of the square around the given point
fun getSquareAroundPoint(linha: Int, coluna: Int, numLines: Int, numColumns: Int): Pair<Pair<Int,Int>, Pair<Int,Int>> {
    // Guaranties that the square does not exceed the matrix limits
    val yL = if (linha - 1 < 0) linha else linha - 1
    val yR = if (linha + 1 > numLines - 1) linha else linha + 1
    val xL = if (coluna - 1 < 0) coluna else coluna - 1
    val xR = if (coluna + 1 > numColumns - 1) coluna else coluna + 1

    return Pair(Pair(yL,xL), Pair(yR,xR))
}

fun createMatrixTerrain(numLines: Int, numColumns: Int, numMines: Int, ensurePathToWin: Boolean = false):
        Array<Array<Pair<String,Boolean>>> {
    // Creates the matrix
    var matrix = Array(numLines) { Array(numColumns) { Pair(" ", ensurePathToWin) } }
    // Places the player 'P' and finish 'f'
    matrix[0][0]= Pair("P", false)
    matrix[numLines-1][numColumns-1]= Pair("f", false)
    var mines = numMines

    if (ensurePathToWin) {
        // Place mines semi-randomly, always possible to win
        while (mines > 0) {
            val randomLine = (0 until numLines).random()
            val randomColumn = (0 until numColumns).random()
            val square = getSquareAroundPoint(randomLine, randomColumn, numLines, numColumns)
            val emptyAroundPoint = isEmptyAround(matrix, randomLine, randomColumn,
                square.first.first, square.first.second, square.second.first, square.second.second)

            /*  !!!NOTA IMPORTANTE!!!
                De momento o modo semi-aleatório não tem em conta se o ponto gerado de forma aleatória já está ocupado
                por uma mina, podendo colocar outra mina nesse mesmo lugar, resultando em que a matriz final não contenha
                o número de minas pretendido. Isto deve-se ao facto de em certas situações (dependendo do tamanho da matriz)
                não seja possível colocar todas as minas respeitando o que está descrito no enunciado (deixando todos os
                espaços à volta da mina livres).
            */

            // Verify if the randomly generated point is not the player place 'P' or finish 'f' and all places around it
            // are empty
            if (matrix[randomLine][randomColumn].first != "P" && matrix[randomLine][randomColumn].first != "f"
                && emptyAroundPoint) {
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
    val yl = square.first.first
    val yr = square.second.first
    val xl = square.first.second
    val xr = square.second.second

    var mines = 0

    for (line in yl..yr){
        for (column in xl..xr){
            if(matrixTerrain[line][column].first == "*" && !(line == centerY && column == centerX)){
                mines++
            }
        }
    }
    return mines
}

// Places number of mines around each cell
fun fillNumberOfMines(matrixTerrain: Array<Array<Pair<String, Boolean>>>) {
    var numLines = matrixTerrain.size
    var numColumns = matrixTerrain[0].size
    for(line in 0 until numLines){
        for(column in 0 until numColumns){
            var mines = countNumberOfMinesCloseToCurrentCell(matrixTerrain, line, column)
            if(matrixTerrain[line][column].first == " " && mines > 0){
                matrixTerrain[line][column] = Pair("$mines", false)
            }
        }
    }
}

fun revealMatrix(matrixTerrain: Array<Array<Pair<String, Boolean>>>, coordY: Int, coordX: Int, endGame: Boolean = false) {

}

fun isEmptyAround(matrixTerrain: Array<Array<Pair<String, Boolean>>>, centerY: Int, centerX: Int, yl: Int, xl: Int, yr: Int, xr: Int): Boolean {
    for (line in yl..yr) {
        for (col in xl..xr) {
            if (!(line == centerY && col == centerX)) {
                // Returns false if a mine is in the square around the point
                if (matrixTerrain[line][col].first == "*") return false
            }
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