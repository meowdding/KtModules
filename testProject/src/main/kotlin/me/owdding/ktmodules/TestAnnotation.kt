package me.owdding.ktmodules

@AutoCollect("Tests", prefixProjectName = false)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class TestAnnotation()
