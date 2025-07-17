import groovy.util.Node
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

    implementation("me.owdding.kotlinpoet:kotlinpoet-jvm:1.0.1")
    implementation("me.owdding.kotlinpoet:ksp:1.0.1")
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

                withXml {
                    val root = asNode()

                    root.children()
                        .filterIsInstance<Node>()
                        .filter { "dependencies" == it.name() || "dependencyManagement" == it.name() }
                        .forEach { root.remove(it) }

                    val dependencies = root.appendNode("dependencies")

                    fun addDependency(group: String, artifact: String, version: String) {
                        val dependency = dependencies.appendNode("dependency")
                        dependency.appendNode("groupId", group)
                        dependency.appendNode("artifactId", artifact)
                        dependency.appendNode("version", version)
                    }

                    addDependency("me.owdding.kotlinpoet", "kotlinpoet-jvm", "1.0.1")
                    addDependency("me.owdding.kotlinpoet", "ksp", "1.0.1")
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
