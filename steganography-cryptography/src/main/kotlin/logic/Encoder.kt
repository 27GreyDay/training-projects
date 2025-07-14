package logic

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.experimental.xor
import javax.imageio.ImageIO
import java.io.File
import java.io.IOException

/**
 * Объект для шифрования и встраивания сообщения в изображение с использованием LSB-стеганографии и XOR-шифрования.
 */
object Encoder {

    /**
     * Кодирует сообщение в изображение с помощью стеганографии и сохраняет результат.
     *
     * @param inputFile Оригинальное изображение, куда будет внедрено сообщение.
     * @param outputFile Файл, в который будет сохранено закодированное изображение.
     * @param message Сообщение, которое нужно зашифровать и встроить.
     * @param password Пароль для шифрования сообщения (используется XOR).
     * @return [Result.success] если всё прошло успешно, иначе [Result.failure] с ошибкой.
     */
    fun encodeMessageToImage(
        inputFile: File,
        outputFile: File,
        message: String,
        password: String
    ): Result<Unit> {
        var inputImage: BufferedImage = try {
            ImageIO.read(inputFile) ?: return Result.failure(IOException("Файл нельзя прочитать."))
        } catch (e: IOException) {
            return Result.failure(e)
        }

        // Удаляем альфа-канал
        if (inputImage.colorModel.hasAlpha()) inputImage = removeAlpha(inputImage)

        // Шифруем сообщение с помощью XOR
        val encrypted = xorEncrypt(
            message.encodeToByteArray(),
            password.encodeToByteArray()
        )

        // Добавляем специальный маркер конца
        val byteArray = encrypted + byteArrayOf(0, 0, 3)

        if (inputImage.width * inputImage.height < byteArray.size * 8) {
            return Result.failure(IllegalArgumentException("Изображение слишком маленькое для этого сообщения."))
        }

        // Встраиваем закодированное сообщение в изображение
        val outputImage = insertBits(inputImage, byteArray)

        return try {
            ImageIO.write(outputImage, "png", outputFile)
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    private fun removeAlpha(image: BufferedImage): BufferedImage {
        val rgbImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        val g = rgbImage.createGraphics()
        g.drawImage(image, 0, 0, null)
        g.dispose()
        return rgbImage
    }

    private fun xorEncrypt(message: ByteArray, password: ByteArray): ByteArray {
        return message.mapIndexed { i, byte ->
            byte xor password[i % password.size] // Зацикленное применение байтов пароля
        }.toByteArray()
    }

    private fun insertBits(image: BufferedImage, byteArray: ByteArray): BufferedImage {
        var bit = 0
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val color = Color(image.getRGB(x, y))
                var b = color.blue

                if (bit < byteArray.size * 8) {
                    // Заменяем младший бит синего канала на следующий бит сообщения
                    b = (b and 0b11111110) or getBit(byteArray, bit)
                    bit++
                }

                val newColor = Color(color.red, color.green, b)
                image.setRGB(x, y, newColor.rgb)
            }
        }
        return image
    }

    private fun getBit(byteArray: ByteArray, bitIndex: Int): Int {
        val bitPos = 7 - (bitIndex % 8) // Старший бит — первый
        return (byteArray[bitIndex / 8].toInt() shr bitPos) and 1
    }
}
