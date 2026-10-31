import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("application")
    id("jacoco")
    kotlin("jvm") version ("2.3.21")
    id("com.google.devtools.ksp") version ("2.3.5")
}

group = property("groupId")!!
version = property("koraVersion")!!

val koraBom: Configuration by configurations.creating
configurations {
    ksp.get().extendsFrom(koraBom); compileOnly.get().extendsFrom(koraBom)
    api.get().extendsFrom(koraBom); implementation.get().extendsFrom(koraBom)
}

repositories {
    mavenCentral()
    maven { url = uri("https://central.sonatype.com/repository/maven-snapshots") }
}

dependencies {
    koraBom(platform("io.koraframework:kora-bom:${property("koraVersion")}"))

    ksp("io.koraframework:symbol-processors")
    ksp("org.slf4j:slf4j-simple:2.0.16")

    implementation("io.koraframework:http-server-undertow")
    implementation("io.koraframework:config-hocon")
    implementation("io.koraframework:logging-logback")
    implementation("io.koraframework:json-common")

    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.koraframework:test-junit5")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
}

kotlin {
    jvmToolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
    sourceSets.main { kotlin.srcDir("build/generated/ksp/main/kotlin") }
    sourceSets.test { kotlin.srcDir("build/generated/ksp/test/kotlin") }
}

application {
    applicationName = "application"
    mainClass.set("io.koraframework.guide.gettingstarted.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

tasks.distTar {
    archiveFileName.set("application.tar")
}

tasks.withType<JavaExec> {
    environment(
        "" to "",
    )
}

val jacocoExcludeSet = setOf("**/generated/**", "**/Application*", "**/\$*")
tasks.test {
    dependsOn("distTar")

    jvmArgs(
        "-XX:+TieredCompilation",
        "-XX:TieredStopAtLevel=1",
    )

    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }

    exclude("**/\$*")

    reports {
        html.required = false
        junitXml.required = false
    }

    jacoco {
        jacocoExcludeSet.forEach { exclude(it) }
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
    classDirectories.setFrom(sourceSets.main.get().output.asFileTree.matching {
        jacocoExcludeSet.forEach { exclude(it) }
    })
}
