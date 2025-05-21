import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinApiPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
}

apply<KotlinApiPlugin>()

repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.20-1.0.25")

    implementation("me.owdding.kotlinpoet:kotlinpoet-jvm:2.2.1-SNAPSHOT")
    implementation("me.owdding.kotlinpoet:ksp:2.2.0-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
    target {
        compilations.create("java8") {
            defaultSourceSet.kotlin.srcDir(project.sourceSets.main.get().kotlin.srcDirs)
            defaultSourceSet.resources.srcDir(project.sourceSets.main.get().resources.srcDirs)
            source(project.sourceSets.main.get())
            defaultSourceSet {
                dependencies {
                    for (file in project.sourceSets.main.get().compileClasspath.files) {
                        implementation(files(file))
                    }
                }
            }
        }
    }
}

tasks.getByName<KotlinCompile>("compileJava8Kotlin") {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}

tasks.getByName<JavaCompile>("compileJava8Java") {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

val java8Jar = tasks.create("java8jar", Jar::class) {
    mustRunAfter(tasks.getByName("java8Classes"))
    from(sourceSets.getByName("java8").output)
    archiveClassifier.set("java-8")
}

publishing {
    publications {
        fun createDefault(name: String, task: Jar, suffix: String = "") = create<MavenPublication>(name) {
            artifactId = "KtModules"
            artifact(task)
            version = project.version.toString() + suffix

            pom {
                this.name.set("KtModules")
                url.set("https://github.com/meowdding/ktmodules")

                scm {
                    connection.set("git:https://github.com/meowdding/ktmodules.git")
                    developerConnection.set("git:https://github.com/meowdding/ktmodules.git")
                    url.set("https://github.com/meowdding/ktmodules")
                }
            }
        }
        createDefault("maven", tasks.jar.get())
        createDefault("java8", java8Jar, "+java-8")
    }
    repositories {
        maven {
            setUrl("https://maven.teamresourceful.com/repository/thatgravyboat/")
            credentials {
                username = System.getenv("MAVEN_USER") ?: providers.gradleProperty("maven_username").orNull
                password = System.getenv("MAVEN_PASS") ?: providers.gradleProperty("maven_password").orNull
            }
        }
    }
}
