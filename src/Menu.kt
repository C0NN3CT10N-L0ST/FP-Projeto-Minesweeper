fun isNameValid(name: String?, minLength: Int = 3): Boolean {
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
            if ((!name[0].isLetter() || !name[0].isUpperCase()) || (!name[spacePosition + 1].isLetter() || !name[spacePosition + 1].isUpperCase())) return false

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