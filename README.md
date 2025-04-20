# KtModules
<hr>

A library to automatically collect modules in a project to make registering things more convenient.

## For Developers
<hr>

Be sure to add our maven to your `build.gradle(.kts)`:
```groovy
repositories {
    maven { url = "https://maven.teamresourceful.com/repository/maven-public/" }
    // <--- other repositories here --->
}
```
You can then add the annotation processor as dependency.

<b>build.gradle</b>
```groovy
dependencies {
    // <--- Other dependencies here --->
    ksp "me.owdding.ktmodules:KtModules:${version}"
    compileOnly "me.owdding.ktmodules:KtModules:${version}"
}
```
<b>build.gradle.kts</b>
```kts
dependencies {
    // <--- Other dependencies here --->
    compileOnly(ksp("me.owdding.ktmodules:KtModules:${version}")!!)
}
```

The last thing you have to do is configure the processor, to do this add the following to your `build.gradle(.kts)`
```groovy
ksp {
    arg("meowdding.modules.project_name", project.name)
    arg("meowdding.modules.package", TODO("Change to your generated code package"))
}
```