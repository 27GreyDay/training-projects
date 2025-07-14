package logic

import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import java.io.IOException
import kotlin.experimental.xor
/**
 * Объект для извлечения и расшифровки скрытого сообщения из изображения.
 * Использует стеганографию (LSB) и XOR-дешифрацию по паролю.
 */
object Decoder {
    /**
     * Декодирует и расшифровывает сообщение, скрытое в изображении.
     *
     * @param imageFile Изображение (.png) содержащее сообщение.
     * @param password Пароль, с помощью которого зашифровано сообщение (XOR).
     * @return [Result] со строкой сообщения или ошибкой, если расшифровка невозможна.
     */
    fun decodeMessageFromImage(imageFile: File, password: String): Result<String> {
        val image: BufferedImage = try {
            ImageIO.read(imageFile) ?: return Result.failure(IOException("Файл нельзя прочитать."))
        } catch (e: IOException) {
            return Result.failure(e)
        }

        val encryptedBytes = extractMessage(image)
        val decrypted = xorDecrypt(encryptedBytes, password.encodeToByteArray())

        return runCatching {
            decrypted.toString(Charsets.UTF_8)
        }
    }

    private fun xorDecrypt(encrypted: ByteArray, password: ByteArray): ByteArray {
        return encrypted.mapIndexed { i, byte ->
            byte xor password[i % password.size] // Кольцевое применение байтов пароля
        }.toByteArray()
    }

    private fun extractMessage(image: BufferedImage): ByteArray {
        val bits = mutableListOf<Int>()

        // Извлекаем младший бит синего канала каждого пикселя
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val blue = Color(image.getRGB(x, y)).blue
                bits.add(blue and 1) // LSB
            }
        }
        val bytes = mutableListOf<Byte>()
        for (i in bits.indices step 8) {
            if (i + 8 > bits.size) break

            // Собираем байт из 8 битов
            var byte = 0
            for (j in 0 until 8) {
                byte = (byte shl 1) or bits[i + j]
            }
            bytes.add(byte.toByte())

            // Проверка на конец сообщения: последовательность 0, 0, 3
            if (bytes.size >= 3 &&
                bytes[bytes.size - 3] == 0.toByte() &&
                bytes[bytes.size - 2] == 0.toByte() &&
                bytes[bytes.size - 1] == 3.toByte()
            ) {
                repeat(3) { bytes.removeLast() } // Удаляем маркер окончания
                break
            }
        }
        return bytes.toByteArray()
    }
}
