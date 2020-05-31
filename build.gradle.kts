import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
    java
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
}

group = "io.github.single-coroutine-scheduler"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.3.7")
    implementation(group = "io.projectreactor", name = "reactor-core", version = "3.3.5.RELEASE")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_HIGHER
}

tasks {
    fun DokkaTask.configureDokka(newOutputFormat: String) {
        outputFormat = newOutputFormat

        outputDirectory = "$projectDir/docs"

        configuration {
            targets = listOf("JVM")
            platform = "JVM"

            externalDocumentationLink {
                url = URL("https://projectreactor.io/docs/core/3.3.5.RELEASE/api/")
                packageListUrl = URL("https://projectreactor.io/docs/core/3.3.5.RELEASE/api/package-list")
            }
            externalDocumentationLink {
                url = URL("https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/")
                packageListUrl = URL("https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/package-list")

            }

            sourceLink {
                path = "src/main/kotlin"
                url = "https://github.com/MattiKrause/SingleCoroutineScheduler"
                lineSuffix = "#L"
            }

            jdkVersion = 8
        }
    }

    //val dokka by getting(DokkaTask::class) {
    //}

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    named<Test>("test") {
        useJUnitPlatform()
    }

    register<DokkaTask>("dokkaGFM") {
        configureDokka("gfm")
    }

    register<DokkaTask>("dokkaHTML") {
        configureDokka("html")
    }
}
