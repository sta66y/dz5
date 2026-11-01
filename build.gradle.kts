import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("application")
    kotlin("jvm") version "1.9.25"
    id("com.google.devtools.ksp") version "1.9.25-1.0.20"
}

group = property("groupId")!!
version = property("koraVersion")!!

val koraBom: Configuration by configurations.creating
configurations {
    ksp.get().extendsFrom(koraBom)
    implementation.get().extendsFrom(koraBom)
}

repositories { mavenCentral() }

dependencies {
    koraBom(platform("ru.tinkoff.kora:kora-parent:${property("koraVersion")}"))
    ksp("ru.tinkoff.kora:kora-app-symbol-processor:1.2.23")
    implementation("ru.tinkoff.kora:common")
    implementation("ru.tinkoff.kora:application-graph")
}

kotlin {
    jvmToolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
    sourceSets.main { kotlin.srcDir("build/generated/ksp/main/kotlin") }
}

application {
    mainClass.set("ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
}
