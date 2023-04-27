import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import com.android.mediproject.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("mediproject.android.library")
                apply("mediproject.android.hilt")
            }

            extensions.configure<LibraryExtension> {
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                "implementation"(project(":core:common"))
                "implementation"(project(":core:data"))
                "implementation"(project(":core:ui"))

                "implementation"(libs.findBundle("navigations").get())
                "implementation"(libs.findBundle("lifecycles").get())
                "implementation"(libs.findLibrary("kotlinx.coroutines.android").get())
            }
        }
    }
}