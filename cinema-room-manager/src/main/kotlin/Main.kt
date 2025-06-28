var ticketsPurchased = 0
var currentIncome = 0
var totalIncome = 0

fun main() = printMenu(createCinemaHall())

fun printMenu(hall: MutableList<MutableList<String>>) {
    println("\n1. Схема зала\n2. Купить билет\n3. Статистика\n0. Выход")
    when (readln().toInt()) {
        1 -> showSeats(hall)
        2 -> buyTicket(hall)
        3 -> showStatistics(hall)
        0 -> return
    }
    printMenu(hall)
}

fun createCinemaHall(): MutableList<MutableList<String>> {
    return try {
        println("Введите количество рядов:")
        val rows = readln().toInt()
        println("Введите количество мест в каждом ряду:")
        val seats = readln().toInt()
        totalIncome = if (rows * seats > 60) (rows / 2 * seats * 700) + ((rows - rows / 2) * seats * 560) else rows * seats * 700
        MutableList(rows) { MutableList(seats) { "□" } }
    } catch (e: Exception) {
        println("\nНеправильный ввод!\n")
        createCinemaHall()
    }
}

fun showSeats(hall: MutableList<MutableList<String>>) {
    print("\nСхема зала:\n ")
    for (i in 1..hall[0].size) print(if (i == hall[0].size) " $i\n" else " $i")
    for (i in 1..hall.size) println("$i ${hall[i - 1].joinToString(" ")}")
}

fun buyTicket(hall: MutableList<MutableList<String>>) {
    try {
        println("\nВведите номер ряда:")
        val row = readln().toInt().dec()
        println("Введите номер места в этом ряду:")
        val seat = readln().toInt().dec()
        hall[row][seat] = if (hall[row][seat] == "■") throw Exception("\nЭтот билет уже куплен!") else "■"
        val ticketPrice = if (hall.size * hall[0].size > 60 && row < hall.size / 2 || hall.size * hall[0].size < 60) 700 else 560
        currentIncome += ticketPrice
        ticketsPurchased++
        println("\nСтоимость билета: ₽$ticketPrice")
    } catch (e: Exception) {
        println(if (e.message!!.contains("билет")) e.message else "\nНеправильный ввод!")
        buyTicket(hall)
    }
}

fun showStatistics(hall: MutableList<MutableList<String>>) {
    val percentage = ticketsPurchased / (hall.size * hall[0].size).toDouble() * 100.00
    println("\nКоличество приобретенных билетов: $ticketsPurchased\n" +
            "Процент: ${"%.2f".format(percentage)}%\n" +
            "Текущий доход: ₽$currentIncome\n" +
            "Общий доход: ₽$totalIncome")
}