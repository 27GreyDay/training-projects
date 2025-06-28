enum class Beverage(
    val visibleName: String,
    val water: Int,
    val milk: Int,
    val beans: Int,
    val price: Int,
) {
    ESPRESSO("эспрессо", 250, 0, 16, 140),
    LATTE("латте", 350, 75, 20, 245),
    CAPPUCCINO("капучино", 200, 100, 12, 210),
}

class CoffeeMachine(
    var water: Int,
    var milk: Int,
    var beans: Int,
    var cups: Int,
    var money: Int,
) {

    fun getStatus() =
        "\nКофемашина имеет:\n" +
                "$water мл воды\n" +
                "$milk мл молока\n" +
                "$beans г кофейных зёрен\n" +
                "$cups одноразовых стаканчиков\n" +
                "$money рублей\n"

    fun buy(beverage: Beverage): String {
        return when {
            water < beverage.water -> "Простите, не хватает воды!\n"
            milk < beverage.milk -> "Простите, не хватило молока!\n"
            beans < beverage.beans -> "Простите, не хватает кофейных зерен!\n"
            cups < 1 -> "Простите, не хватает одноразовых стаканчиков!\n"
            else -> {
                water -= beverage.water
                milk -= beverage.milk
                beans -= beverage.beans
                cups--
                money += beverage.price
                "У меня достаточно ресурсов, чтобы приготовить вам кофе!\n"
            }
        }
    }
}

class CoffeeMachineInterface {
    enum class State { MAIN, BUY, FILL_WATER, FILL_MILK, FILL_BEANS, FILL_CUPS, END }

    private val beverageString = Beverage.entries.joinToString { "${it.ordinal + 1} - ${it.visibleName}" }

    private var state = State.MAIN
    private val coffeeMachine = CoffeeMachine(400, 540, 120, 9, 19250)

    fun getPrompt(): String {
        return when (state) {
            State.MAIN -> "\nВведите действие (купить, пополнить, забрать, остаток, выйти):"
            State.BUY -> "\nЧто вы хотите купить? $beverageString, назад - к главному меню:"
            State.FILL_WATER -> "\nНапишите, сколько мл воды вы хотите добавить:"
            State.FILL_MILK -> "Напишите, сколько мл молока вы хотите добавить:"
            State.FILL_BEANS -> "Напишите, сколько граммов кофейных зерен вы хотите добавить:"
            State.FILL_CUPS -> "Напишите, сколько одноразовых стаканчиков для кофе вы хотите добавить:"
            State.END -> ""
        }
    }

    fun process(input: String): String {
        when (state) {
            State.MAIN -> when (input) {
                "купить" -> {
                    state = State.BUY
                }
                "пополнить" -> {
                    state = State.FILL_WATER
                }
                "забрать" -> {
                    val output = "Держите ₽${coffeeMachine.money}\n"
                    coffeeMachine.money = 0
                    return output
                }
                "остаток" -> {
                    return coffeeMachine.getStatus()
                }
                "выйти" -> {
                    state = State.END
                }
                else -> return "Неверный ввод"
            }
            State.BUY -> {
                state = State.MAIN
                if (input != "назад")
                    return coffeeMachine.buy(Beverage.entries[input.toInt() - 1])
            }
            State.FILL_WATER -> {
                coffeeMachine.water += input.toInt()
                state = State.FILL_MILK
            }
            State.FILL_MILK -> {
                coffeeMachine.milk += input.toInt()
                state = State.FILL_BEANS
            }
            State.FILL_BEANS -> {
                coffeeMachine.beans += input.toInt()
                state = State.FILL_CUPS
            }
            State.FILL_CUPS -> {
                coffeeMachine.cups += input.toInt()
                state = State.MAIN
            }
            State.END -> check(false)
        }
        return ""
    }

    fun isRunning() = state != State.END
}

fun main() {
    val coffeeMachineInterface = CoffeeMachineInterface()
    while (coffeeMachineInterface.isRunning()) {
        println(coffeeMachineInterface.getPrompt())
        print(coffeeMachineInterface.process(readln()))
    }
}
