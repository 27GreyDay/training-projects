import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Обратите внимание, что если вы разрабатываете библиотеку, вам следует использовать compose.desktop.common.
    // compose.desktop.currentOs должен использоваться в launcher-sourceSet
    // (в отдельном модуле для демо-проекта и в testMain).
    // При использовании compose.desktop.common вы также потеряете функциональность @Preview.
    implementation(compose.desktop.currentOs)
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "PixelHide"
            packageVersion = "1.0.0"
        }
    }
}
