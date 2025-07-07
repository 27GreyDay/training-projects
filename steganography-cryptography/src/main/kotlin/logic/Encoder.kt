package logic

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.experimental.xor
import javax.imageio.ImageIO
import java.io.File
import java.io.IOException

object Encoder {

    fun encodeMessageToImage(
        inputFile: File,
        outputFile: File,
        message: String,
        password: String
    ): Result<Unit> {
        val inputImage: BufferedImage = try {
            ImageIO.read(inputFile) ?: return Result.failure(IOException("Файл нельзя прочитать."))
        } catch (e: IOException) {
            return Result.failure(e)
        }

        val encrypted = xorEncrypt(
            message.encodeToByteArray(),
            password.encodeToByteArray()
        )
        val byteArray = encrypted + byteArrayOf(0, 0, 3)

        if (inputImage.width * inputImage.height < byteArray.size * 8) {
            return Result.failure(IllegalArgumentException("Изображение слишком маленькое для этого сообщения."))
        }

        val outputImage = insertBits(inputImage, byteArray)

        return try {
            ImageIO.write(outputImage, "png", outputFile)
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    private fun xorEncrypt(message: ByteArray, password: ByteArray): ByteArray {
        return message.mapIndexed { i, byte ->
            byte xor password[i % password.size]
        }.toByteArray()
    }

    private fun insertBits(image: BufferedImage, byteArray: ByteArray): BufferedImage {
        var bit = 0
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val color = Color(image.getRGB(x, y))
                var b = color.blue
                if (bit < byteArray.size * 8) {
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
        val bitPos = 7 - (bitIndex % 8)
        return (byteArray[bitIndex / 8].toInt() shr bitPos) and 1
    }
}