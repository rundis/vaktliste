plugins {
    kotlin("jvm") version "2.1.0"
}

group = "no.kodemaker.vaktliste"
version = "1.0-SNAPSHOT"

val timefoldSolverVersion = "1.18.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation(platform("ai.timefold.solver:timefold-solver-bom:${timefoldSolverVersion}"))
    implementation("ai.timefold.solver:timefold-solver-core")
    implementation("ch.qos.logback:logback-core:1.5.16")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")


    testImplementation(kotlin("test"))
    testImplementation("ai.timefold.solver:timefold-solver-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}