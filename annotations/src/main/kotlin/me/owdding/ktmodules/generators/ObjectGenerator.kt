package me.owdding.ktmodules.generators

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.owdding.ktmodules.generators.Generators.escapeQualifiedName

internal object ObjectGenerator : Generator {

    override fun isFor(annotated: KSAnnotated) = annotated is KSClassDeclaration && annotated.classKind == ClassKind.OBJECT
    override fun emit(annotated: KSAnnotated) = (annotated as KSClassDeclaration).escapeQualifiedName()

}