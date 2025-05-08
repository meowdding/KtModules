plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
}

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
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "KtModules"
            from(components["java"])

            pom {
                name.set("KtModules")
                url.set("https://github.com/meowdding/ktmodules")

                scm {
                    connection.set("git:https://github.com/meowdding/ktmodules.git")
                    developerConnection.set("git:https://github.com/meowdding/ktmodules.git")
                    url.set("https://github.com/meowdding/ktmodules")
                }
            }
        }
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
