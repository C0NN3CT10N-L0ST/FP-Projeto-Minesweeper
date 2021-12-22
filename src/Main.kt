fun main() {
    var menuOption: Int? = null
    do {
        println(makeMenu())
        menuOption = validateMenuOption()

        if (menuOption == null) println("Invalid response.\n")
    } while (menuOption == null)

    if (menuOption == 1) {
        // Game Settings/Options
        val playerName = validatePlayerName()
        val useLegend = validateLegend()
        val numLines = validateLines()
        val numColumns = validateColumns()
        val numMines = validateMines(numLines, numColumns)

        val terrain = createMatrixTerrain(numLines, numColumns, numMines)
        for (i in terrain) {
            println(i.contentDeepToString())
        }
    }
}