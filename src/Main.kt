fun main() {
    do {
        // Get Menu Option
        val menuOption = getMenuOption()
        var success = -1

        if (menuOption == 0) return

        if (menuOption == 1) {
            // Game Settings/Options
            val playerName = validatePlayerName()
            val useLegend = validateLegend()
            val numLines = validateLines()
            val numColumns = validateColumns()
            val numMines = validateMines(numLines, numColumns)

            // Creates matrix terrain with the given settings
            val matrixTerrain = createMatrixTerrain(numLines, numColumns, numMines)

            // Adds the number of close mines to each empty place
            fillNumberOfMines(matrixTerrain)

            // Make positions around player ('P') starting position visible
            revealMatrix(matrixTerrain, 0, 0)

            // Prints initial board
            val terrain = makeTerrain(matrixTerrain, useLegend)
            println(terrain)

            success = gameLoop(matrixTerrain, numLines, numColumns, useLegend)
            if (success == 0) {
                // Shows the board with all cells visible
                println(makeTerrain(matrixTerrain, useLegend, showEverything = true))
                println("You win the game!")
            } else if (success == 1) {
                // Shows the board with all cells visible
                println(makeTerrain(matrixTerrain, useLegend, showEverything = true))
                println("You lost the game!")
            }
        }
    } while (success != 2)
}