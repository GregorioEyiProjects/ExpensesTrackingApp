plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Add the Google services Gradle plugin
}

android {
    namespace = "com.example.expensestraker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.expensestraker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.11.0") // material design
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))  // Import the Firebase BoM
    implementation("com.google.firebase:firebase-analytics") //
    implementation("com.google.firebase:firebase-firestore:24.10.3") //firebase for the database
    implementation("com.google.firebase:firebase-auth") // firebase auth
    implementation ("com.github.bumptech.glide:glide:4.16.0") // to load images
    //implementation("androidx.navigation:navigation-compose:2.7.7") // creo que es para jetpackCompose
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}