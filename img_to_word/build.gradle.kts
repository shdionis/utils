import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.uiTooling)
    implementation("org.apache.poi:poi:5.4.0")
    implementation("org.apache.poi:poi-scratchpad:5.4.0")
    implementation("org.apache.poi:poi-ooxml:5.4.0")
}

compose.desktop {
    application {
        mainClass = "ru.sharov.imgtodoc.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "img_to_word"
            packageVersion = "1.0.0"
        }
    }
}
