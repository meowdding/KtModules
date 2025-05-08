package me.owdding.ktmodules

@AutoCollect("Tests")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class TestAnnotation()
