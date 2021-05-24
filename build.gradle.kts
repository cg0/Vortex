plugins {
    java
    kotlin("jvm") version "1.4.31"
}

group = "uk.cg0"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
    implementation("mysql", "mysql-connector-java", "5.1.13")
}
