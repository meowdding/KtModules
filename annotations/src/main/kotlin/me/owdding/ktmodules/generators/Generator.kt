package me.owdding.ktmodules.generators

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.escapeSegmentsIfNecessary

internal object Generators {
    
    val registered = listOf(
        ObjectGenerator,
        ClassGenerator,
        FunctionGenerator,
        VariableGenerator,
        UnknownGenerator,
    )
    fun generate(ksAnnotated: KSAnnotated) = registered.find { it.isFor(ksAnnotated) }?.emit(ksAnnotated)

    fun KSDeclaration.escapeQualifier() = this.qualifiedName!!.getQualifier().escapeSegmentsIfNecessary()
    fun KSDeclaration.escapeQualifiedName() = this.qualifiedName!!.asString().escapeSegmentsIfNecessary()

}

internal interface Generator {

    fun isFor(annotated: KSAnnotated): Boolean
    fun emit(annotated: KSAnnotated): String

}