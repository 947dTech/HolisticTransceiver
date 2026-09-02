import java.net.URI

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ksndtech.holistictransceiver"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ksndtech.holistictransceiver"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    packaging {
        resources {
            // 一時的な回避として重複を許容する場合(根本解決ではない点に注意)
//             pickFirsts += "META-INF/..."
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.concurrent.features)
    implementation(libs.androidx.concurrent.features.ktx)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view) // for PreviewView
    implementation(libs.androidx.camera.compose) // for compose UI
    implementation(libs.androidx.camera.extensions) // For Extensions

    // material-icons is deprecated
    implementation("androidx.compose.material:material-icons-core")

    implementation(libs.kotlinx.serialization.json)

//    implementation(libs.mediapipe.tasks.vision)

    implementation(libs.mediapipe.tasks.vision) {
        // MediaPipe内部のテレメトリ用ライブラリが引き込むprotobuf-javaliteを除外し、
        // tasks-vision本体が要求するフル版protobuf-javaのみを解決させる
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
    }
    implementation("com.google.protobuf:protobuf-java:3.25.5")

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

tasks.register("downloadHolisticModel") {
    val modelUrl = "https://storage.googleapis.com/mediapipe-models/holistic_landmarker/holistic_landmarker/float16/1/holistic_landmarker.task"
    val outputFile = file("src/main/assets/holistic_landmarker.task")

    doLast {
        if (!outputFile.exists()) {
            outputFile.parentFile.mkdirs()
            outputFile.outputStream().use { out ->
                URI(modelUrl).toURL().openStream().use { input -> input.copyTo(out) }
            }
        }
    }
}

tasks.named("preBuild") {
    dependsOn("downloadHolisticModel")
}