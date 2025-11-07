// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.detekt) apply false
}

// Force consistent Kotlin version across all modules
allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:1.9.25")
            force("org.jetbrains.kotlin:kotlin-reflect:1.9.25")
        }
    }
    
    // Apply Kotlin compiler optimizations globally
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xjsr305=strict"
            )
        }
    }
}

// Apply Detekt across all modules with shared configuration
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "17"
        config.setFrom(files("$rootDir/detekt.yml"))
        buildUponDefaultConfig = true
        parallel = true
        ignoreFailures = true
        // Exclude generated and build outputs
        setSource(files(projectDir))
        include("**/*.kt", "**/*.kts")
        exclude("**/build/**", "**/generated/**")
        baseline.set(file("$rootDir/detekt-baseline.xml"))
    }

    tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
        jvmTarget = "17"
        config.setFrom(files("$rootDir/detekt.yml"))
        buildUponDefaultConfig = true
        baseline.set(file("$rootDir/detekt-baseline.xml"))
        setSource(files(projectDir))
        include("**/*.kt", "**/*.kts")
        exclude("**/build/**", "**/generated/**")
    }
}