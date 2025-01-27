plugins {
    id("mediproject.android.feature")
    id(libs.plugins.kotlin.parcelize.get().pluginId)
}

android {
    namespace = "com.android.mediproject.feature.medicine"

    buildFeatures {
        buildConfig = true
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":feature:search"))
    implementation(project(":feature:penalties"))
    implementation(project(":feature:comments"))

    implementation(libs.bundles.glides)
    kapt(libs.bundles.glides.kapt)
    implementation(libs.bundles.kotlins)
    implementation(libs.bundles.materials)
    implementation(libs.bundles.uiAndroidx)
    implementation(libs.androidx.asynclayoutinflater)

}