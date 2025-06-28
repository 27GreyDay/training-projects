package parking

class CarPark(numSpaces: Int) {
    private data class Car(val reg: String, val color: String)

    private val parkingSpaces = MutableList<Car?>(numSpaces) { null }

    init { println("Создана парковка на $numSpaces места.") }

    fun parkCar(reg: String, color: String) {
        val car = Car(reg, color.lowercase())
        val index = parkingSpaces.indexOf(null)
        if (index != -1) {
            parkingSpaces[index] = car
            println("$color автомобиль припаркован на месте ${index + 1}.")
        } else {
            println("Извините, парковка переполнена.")
        }
    }

    fun leave(index: Int) {
        if (parkingSpaces[index - 1] != null) {
            println("Место $index свободно.")
            parkingSpaces[index - 1] = null
        } else {
            println("На месте $index нет автомобиля.")
        }
    }

    fun status() {
        val occupiedSpaces = parkingSpaces.filterNotNull()
        if (occupiedSpaces.isEmpty()) {
            println("Парковка пуста.")
        } else {
            occupiedSpaces.forEach { car ->
                println("${parkingSpaces.indexOf(car) + 1} ${car.reg} ${car.color}")
            }
        }
    }

    private fun filterIndexCar(filterCondition: (Car) -> Boolean): List<Int> {
        val results = mutableListOf<Int>()
        parkingSpaces.filterNotNull().forEach { car ->
            if (filterCondition(car)) {
                results.add(parkingSpaces.indexOf(car) + 1)
            }
        }
        return results
    }

    fun regByColor(color: String) {
        val regs = filterIndexCar { it.color == color.lowercase() }
        if (regs.isEmpty()) {
            println("Автомобилей с $color цветом не найдено.")
        } else {
            println(regs.joinToString(", ") { parkingSpaces[it - 1]?.reg ?: "" })
        }
    }

    fun spotByColor(color: String) {
        val spots = filterIndexCar { it.color == color.lowercase() }
        println(if(spots.isEmpty()) "Автомобилей с $color цветом не найдено." else spots.joinToString(", "))
    }

    fun spotByReg(reg: String) {
        val spot = filterIndexCar { it.reg == reg }
        println(if (spot.isEmpty()) "Автомобилей с регистрационным номером $reg не обнаружено." else spot[0])
    }
}


fun main() {
    var carPark: CarPark? = null
    while (true) {
        val input = readln().split(" ")
        when (input[0]) {
            "создать" -> carPark = CarPark(input[1].toInt())
            "припарковать" -> carPark?.parkCar(input[1], input[2])
            "забрать" -> carPark?.leave(input[1].toInt())
            "статус" -> carPark?.status()
            "рег_по_цвету" -> carPark?.regByColor(input[1])
            "места_по_цвету" -> carPark?.spotByColor(input[1])
            "места_по_рег" -> carPark?.spotByReg(input[1])
            "выход" -> break
            else -> println("Недопустимая команда.")
        } ?: println("К сожалению, парковка не была создана.")
    }
}