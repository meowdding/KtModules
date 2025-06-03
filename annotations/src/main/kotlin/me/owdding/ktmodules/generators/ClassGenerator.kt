package me.owdding.ktmodules.generators

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.owdding.ktmodules.generators.Generators.escapeQualifiedName

internal object ClassGenerator : Generator {

    override fun isFor(annotated: KSAnnotated): Boolean {
        if (annotated !is KSClassDeclaration) return false
        if (annotated.classKind != ClassKind.CLASS) return false

        return annotated.getConstructors().any { it.parameters.filterNot { it.hasDefault }.isEmpty() }
    }

    override fun emit(annotated: KSAnnotated) = "${(annotated as KSClassDeclaration).escapeQualifiedName()}()"

}