import kotlin.system.exitProcess
fun makeMenu(opcao: String):String {
        while(opcao == "1") {
            return "1"
        }
        while (opcao == "0" ) {
            return "0"
        }
    return ""
}
fun main() {
    println("Welcome to DEISI Minesweeper")
    println(
        "1 - Start New Game\n" +
                "0 - Exit Game"
    )
    var opcao = readLine()?.toString() ?: "Invalid response"
    makeMenu(opcao)
    if(makeMenu(opcao)=="0"){
        exitProcess(0)
    }

    if(makeMenu(opcao)=="1"){
        println("Enter player name?")
        var name = readLine()}

 if(makeMenu(opcao) != "1" || makeMenu(opcao) != "0") {
        do {
            println("Invalid response")
            println(
                "1 - Start New Game\n" +
                        "0 - Exit Game"
            )
            var opcao = readLine()?.toString() ?: "Invalid response"
            if(opcao=="1"){
                println("Enter player name?")
                var name = readLine()}
            if(opcao=="0"){
            exitProcess(0) }
        break
        } while (makeMenu(opcao) == "1" || makeMenu(opcao) == "0")
    }}




