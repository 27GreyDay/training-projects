import kotlin.system.exitProcess

fun generateAvailableSymbols(): List<Char> {
    return ('0'..'9') + ('a'..'z')
}

fun generateSecretCode(length: Int, symbolsCount: Int): String {
    val allSymbols = generateAvailableSymbols()

    if (symbolsCount > allSymbols.size) {
        println("Ошибка: нельзя использовать больше 36 уникальных символов.")
        exitProcess(1)
    }

    val usedSymbols = allSymbols.take(symbolsCount).shuffled()
    return usedSymbols.take(length).joinToString("")
}

fun pluralForm(number: Int, form1: String, form2: String, form5: String): String {
    val n = number % 100
    val lastDigit = number % 10
    return when {
        n in 11..14 -> form5
        lastDigit == 1 -> form1
        lastDigit in 2..4 -> form2
        else -> form5
    }
}

fun codeGrade(code: CharArray, secretCode: CharArray): String {
    var bull = 0
    var cow = 0
    val length = secretCode.size

    for (i in 0 until length) {
        if (code[i] == secretCode[i]) {
            bull++
        } else if (code[i] in secretCode) {
            cow++
        }
    }

    val bullWord = pluralForm(bull, "бык", "быка", "быков")
    val cowWord = pluralForm(cow, "корова", "коровы", "коров")

    return when {
        (bull != 0 && cow != 0) -> "Оценка: $bull $bullWord и $cow $cowWord"
        (cow == 0 && bull == 0) -> "Оценка: нет"
        (cow != 0) -> "Оценка: $cow $cowWord"
        else -> "Оценка: $bull $bullWord"
    }
}

fun readInt(): Int {
    val input = readln()

    val number = input.toIntOrNull()
    if (number == null || number <= 0) {
        println("Ошибка: \"$input\" не является числом больше 0.")
        exitProcess(1)
    }

    return number
}

fun main() {
    println("Введите длину секретного кода:")
    val length = readInt()

    println("Введите количество возможных символов в коде: (максимум 36):")
    val symbolCount = readInt()

    if (symbolCount < length) {
        println("Ошибка: уникальных символов недостаточно для генерации кода.")
        return
    }

    val secretCode = generateSecretCode(length, symbolCount)

    println(secretCode)
    val maskedCode = "*".repeat(length)
    val symbols = generateAvailableSymbols().take(symbolCount)

    val rangeDescription = when {
        symbolCount <= 10 -> "0-${symbols.last()}"
        else -> "0-9, ${symbols[10]}-${symbols.last()}"
    }

    println("Секретный код подготовлен: $maskedCode ($rangeDescription)")
    println("Игра начинается!")

    var round = 1
    while (true) {
        println("Раунд $round:")
        val guess = readln()

        if (guess.length != length) {
            val symbolWord = pluralForm(length, "символ", "символа", "символов")
            println("Ошибка: введите строку длиной $length $symbolWord.")
            continue
        }

        if (!guess.all { it in symbols }) {
            println("Ошибка: допустимы только символы из диапазона $rangeDescription.")
            continue
        }

        round++

        println(codeGrade(guess.toCharArray(), secretCode.toCharArray()))

        if (guess == secretCode) {
            println("Поздравляем! Вы угадали секретный код.")
            break
        }
    }
}