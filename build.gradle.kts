buildscript {
    repositories {
        google() // Ensure this is added
        mavenCentral()
    }
    dependencies {
        // Other dependencies
        classpath("com.google.gms:google-services:4.4.2")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply true
}