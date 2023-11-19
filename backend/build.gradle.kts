val ktor_version: String by project

plugins {
    application
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.6"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
}