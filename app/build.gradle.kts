import java.util.Properties

val props = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { load(it) }
    }
}
val url = props.getProperty("SUPABASE_URL") ?: error("Falta SUPABASE_URL en local.properties")
val key = props.getProperty("SUPABASE_KEY") ?: error("Falta SUPABASE_KEY en local.properties")

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "ec.edu.uteq.alumnossupabase"
    compileSdk = 37

    defaultConfig {
        applicationId = "ec.edu.uteq.alumnossupabase"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "SUPABASE_URL", "\"$url\"")
        buildConfigField("String", "SUPABASE_KEY", "\"$key\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(platform("io.github.jan-tennert.supabase:bom:3.6.0"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:3.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}