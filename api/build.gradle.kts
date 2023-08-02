plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

//plugins {
//    id("kmovie.kotlin-library-conventions")
//    kotlin("jvm") version "1.8.20"
//    kotlin("jvm")
//    id ("org.jetbrains.kotlin.jvm") version "1.8.20"
//    id ("org.jetbrains.kotlin.multiplatform")
//}

dependencies {
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation ("com.squareup.retrofit2:helpers:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
}