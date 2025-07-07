package ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.FileDialog
import java.awt.Frame
import logic.Encoder
import logic.Decoder
import java.io.File

@Composable
fun App() {
    var inputFilePath by remember { mutableStateOf("") }
    var inputFileName by remember { mutableStateOf("") }
    var outputFileName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Приложение шифрует текст и незаметно встраивает его в изображение, также позволяет извлечь и расшифровать обратно.", style = MaterialTheme.typography.h6)

        Button(onClick = {
            val file = pickFile()
            if (file != null) {
                inputFilePath = file.absolutePath
                inputFileName = file.name
            }
        }) {
            Text(
                if (inputFilePath.isNotBlank())
                    "Выбрано изображение: $inputFileName"
                else
                    "Выбрать изображение для шифрования/дешифрования (.png)"
            )
        }

        OutlinedTextField(
            value = outputFileName,
            onValueChange = { outputFileName = it },
            label = { Text("Укажите название для нового изображения (например, secret.png)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Сообщение которое хотите скрыть") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                if (inputFilePath.isBlank()) {
                    outputText = "Пожалуйста, выберите изображение."
                    return@Button
                } else if (message.isBlank() || password.isBlank()) {
                    outputText = "Пожалуйста, заполните все поля для скрытия сообщения."
                    return@Button
                }
                val inputFile = File(inputFilePath)
                val correctedName = if (outputFileName.endsWith(".png")) outputFileName else "$outputFileName.png"
                val outputFile = getOutputFileInDownloads(correctedName)
                val result = Encoder.encodeMessageToImage(inputFile, outputFile, message, password)
                inputFilePath = ""
                outputFileName = ""
                message = ""
                password = ""
                outputText = ""
                passwordVisible = false
                outputText = result.fold(
                    onSuccess = { "Сообщение успешно скрыто." },
                    onFailure = { "Ошибка: ${it.message}" }
                )
            }) {
                Text("Скрыть сообщение")
            }

            Button(onClick = {
                if (inputFilePath.isBlank() || password.isBlank()) {
                    outputText = "Пожалуйста, укажите файл и пароль для извлечения."
                    return@Button
                }
                val inputFile = File(inputFilePath)
                val result = Decoder.decodeMessageFromImage(inputFile, password)
                outputText = result.fold(
                    onSuccess = { "Сообщение:\n$it" },
                    onFailure = { "Ошибка: ${it.message}" }
                )
            }) {
                Text("Показать сообщение")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(outputText)
    }
}

fun pickFile(): File? {
    val frame = Frame()
    val dialog = FileDialog(frame, "Выберите изображение (.png)", FileDialog.LOAD)
    dialog.isVisible = true
    val dir = dialog.directory
    val file = dialog.file
    frame.dispose()
    return if (dir != null && file != null) File(dir, file) else null
}

fun getOutputFileInDownloads(fileName: String): File {
    val downloadsDir = File(System.getProperty("user.home"), "Downloads")
    return File(downloadsDir, fileName)
}

