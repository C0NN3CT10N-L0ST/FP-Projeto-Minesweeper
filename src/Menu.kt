fun makeMenu(): String {
    return "\nWelcome to DEISI Minesweeper\n\n" +
            "1 - Start New Game\n" +
            "0 - Exit Game\n"
}

val invalidResponse = "Invalid response.\n"

fun getMenuOption(): Int {
    var menuOption: Int? = null
    do {
        println(makeMenu())
        menuOption = validateMenuOption()

        if (menuOption != null) {
            return menuOption
        } else {
            println("Invalid response.\n")
        }
    } while (menuOption == null)
    return -1
}

fun isNameValid(name: String?, minLength: Int = 3): Boolean {  // !WARNING! Só deve funcionar para 2 nomes
    if (name != null) {
        // Obtém a posição do espaço que separa os nomes
        var count = 0
        var spacePosition = -1
        while (count < name.length && spacePosition == -1) {
            if (name[count] == ' ') {
                spacePosition = count
            }
            count++
        }

        val firstNameLen = spacePosition
        // Verifica se contém espaço, se o primeiro nome tem a length mínima, e se existe segundo nome
        if (spacePosition != -1 && firstNameLen >= minLength && (name.length > spacePosition && name[spacePosition + 1].isLetter())) {

            // Verifica se os nomes começam com letra maiúscula
            if ((!name[0].isLetter() || !name[0].isUpperCase())
                || (!name[spacePosition + 1].isLetter() || !name[spacePosition + 1].isUpperCase())) return false

            // Verifica se as restantes letras são minúsculas
            var charPosition = 0
            while (charPosition < name.length) {
                if (charPosition != 0 && charPosition != spacePosition && charPosition != spacePosition + 1) {
                    val currentChar = name[charPosition]
                    if (!currentChar.isLetter() || !currentChar.isLowerCase()) return false
                }
                charPosition++
            }

            return true
        }

        return false
    }

    return false
}

fun validateMenuOption(): Int? {
    val option = readLine()?.toIntOrNull()

    // Se a opção for válida (0 ou 1), retorna esse valor, caso contrário retorna null
    if (option != null && (option == 0 || option == 1)) {
        return option
    }
    return null
}

// Se o nome introduzido for válido, retorna o nome, caso contrário apresenta 'Invalid response' e volta a pedir o nome
fun validatePlayerName(): String {
    var name: String
    do {
        println("Enter player name?")
        name = readLine() ?: ""
        if (isNameValid(name)) return name else println(invalidResponse)
    } while (!isNameValid(name))
    return ""
}

// Retorna 'true' ou 'false'
fun validateLegend(): Boolean {
    do {
        println("Show legend (y/n)?")
        val option = readLine() ?: ""
        if (option.toLowerCase() == "y") {
            return true
        } else if (option.toLowerCase() == "n") {
            return false
        } else {
            println(invalidResponse)
        }
    } while (option.toLowerCase() != "y" || option.toLowerCase() != "n")
    return false
}

// Retorna número de Linhas
fun validateLines(): Int {
    do {
        println("How many lines?")
        val lines = readLine()?.toIntOrNull()
        if (lines != null && lines in 4..9) return lines else println(invalidResponse)
    } while (lines != 1)
    return -1
}

// Retorna número de Colunas
fun validateColumns(): Int {
    do {
        println("How many columns?")
        val columns = readLine()?.toIntOrNull()
        if (columns != null && columns in 4..9) return columns else println(invalidResponse)
    } while (columns == null || columns < 3)
    return -1
}

// Retorna número de minas
fun validateMines(numLines: Int, numColumns: Int): Int {
    do {
        println("How many mines (press enter for default value)?")
        val mines = readLine()
        if (mines != null) {
            if (mines == "") {
                val numMines = calculateNumMinesForGameConfiguration(numLines, numColumns)
                if (numMines != null) return numMines
            } else if (mines.toIntOrNull() != null && isValidGameMinesConfiguration(numLines, numColumns, mines.toInt())) {
                return mines.toInt()
            } else {
                println(invalidResponse)
            }
        } else {
            println(invalidResponse)
        }
    } while (mines == null || mines != "" || mines.toIntOrNull() == null)
    return -1
}

// Converts coordinates from String to a 'Pair<Int,Int>'
fun getCoordinates(readText: String?): Pair<Int,Int>? {
    if (readText != null && readText.length == 2) {
        val line = readText[0].toString().toIntOrNull()
        val colLetter = if (readText[1].isLetter() and readText[1].isUpperCase()) readText[1] else null
        var letter = 'A'
        var letterNum = 0
        while (colLetter != null && colLetter != letter) {
            letter++
            letterNum++
        }

        if (line != null) {
            return Pair(line-1, letterNum)
        }
    }
    return null
}

fun validateExit(input: String?): Boolean {
    return input != null && input == "exit"
}

// Read coordinates and check if they're valid
fun validateCoordinates(playerCoordinates: Pair<Int, Int>, numLines: Int, numColumns: Int): Pair<Int, Int>? {
    do {
        println("Choose the Target cell (e.g 2D)")
        val coordinatesInput = readLine()

        // Checking for 'exit' input
        val exitGame = validateExit(coordinatesInput)
        if (exitGame) return null

        val targetCoordinates = getCoordinates(coordinatesInput)

        if (targetCoordinates != null && isCoordinateInsideTerrain(targetCoordinates, numColumns, numLines)
            && isMovementPValid(playerCoordinates, targetCoordinates)) {
            return targetCoordinates
        } else {
            println(invalidResponse)
        }
    } while (targetCoordinates == null || !isMovementPValid(playerCoordinates, targetCoordinates)
        || !isCoordinateInsideTerrain(targetCoordinates, numColumns, numLines))

    return Pair(-1, -1)
}
// Returns 0 if wins, 1 if loses, 2 if 'exit'
fun gameLoop(matrixTerrain: Array<Array<Pair<String,Boolean>>>, numLines: Int, numColumns: Int,
             showLegend: Boolean = true, withColor: Boolean = false): Int {

    var playerCoordinates = Pair(0,0)  // Keeps track of player position

    // End game/Program exit
    var exitProgram = false
    var endGame = false
    var winGame = false

    // Keeps track of cell info that was previously in player position
    var deletedPosition = Pair(" ", true)
    var deletedPositionCoordinates = Pair(0,0)

    do {
        val target = validateCoordinates(playerCoordinates, numLines, numColumns)
        if (target != null) {
            // Check end game
            val targetY = target.first
            val targetX = target.second

            if (matrixTerrain[targetY][targetX].first == "*" || matrixTerrain[targetY][targetX].first == "f") {
                endGame = true
                winGame = matrixTerrain[targetY][targetX].first == "f"
            } else {
                matrixTerrain[deletedPositionCoordinates.first][deletedPositionCoordinates.second] = deletedPosition
                deletedPosition = matrixTerrain[targetY][targetX]
                deletedPositionCoordinates = Pair(targetY, targetX)
                matrixTerrain[targetY][targetX] = Pair("P", true)
                playerCoordinates = Pair(targetY, targetX)

                // Makes positions close to current player cell visible
                revealMatrix(matrixTerrain, targetY, targetX)

                // Print terrain
                println(makeTerrain(matrixTerrain, showLegend, withColor))
            }
        } else {
            exitProgram = true
        }
    } while (endGame != false || exitProgram != false)

    // endGame or exitProgram?
    return if (endGame && winGame) 0 else if (endGame) 1 else 2
}