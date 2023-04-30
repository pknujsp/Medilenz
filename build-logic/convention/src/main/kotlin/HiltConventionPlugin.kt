import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.kapt")
                apply("com.google.dagger.hilt.android")
            }
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                "implementation"(libs.findBundle("hilts").get())
                "kapt"(libs.findLibrary("androidx.hilt.work.compilerKapt").get())
                "kapt"(libs.findLibrary("androidx.hilt.compilerKapt").get())
                "androidTestImplementation"(libs.findLibrary("androidx.hilt.android.testingAndroidTestImplementation").get())
            }
        }
    }

}