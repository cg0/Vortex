plugins {
    java
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "uk.cg0"
version = "PRERELEASE"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("junit", "junit", "4.12")
    implementation("mysql", "mysql-connector-java", "5.1.13")
    implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.7.3")
    implementation("org.jetbrains.kotlin", "kotlin-reflect", "1.5.10")

}


val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Implementation-Title"] = "Vortex"
        attributes["Implementation-Version"] = version
    }
}