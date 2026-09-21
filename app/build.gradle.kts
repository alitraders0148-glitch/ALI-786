plugins {
    id("com.android.application")
}

android {
    namespace = "com.newalitraders.oos16theme"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.newalitraders.oos16theme"
        minSdk = 26
        targetSdk = 36
        versionCode = 5
        versionName = "1.2.0-iconpack-fix"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
