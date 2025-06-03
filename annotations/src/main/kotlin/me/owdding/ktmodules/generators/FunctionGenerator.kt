package me.owdding.ktmodules.generators

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.escapeSegmentsIfNecessary
import me.owdding.ktmodules.generators.Generators.escapeQualifier

internal object FunctionGenerator : Generator {

    override fun isFor(annotated: KSAnnotated): Boolean {
        if (annotated !is KSFunctionDeclaration) return false
        if (annotated.parent == null && annotated.isPublic()) return true
        if (annotated.parent is KSClassDeclaration && (annotated.parent as? KSClassDeclaration)?.classKind == ClassKind.OBJECT) return true
        return false
    }

    override fun emit(annotated: KSAnnotated): String {
        (annotated as KSFunctionDeclaration).let {
            return "${annotated.escapeQualifier()}::${annotated.qualifiedName!!.getShortName().escapeSegmentsIfNecessary()}"
        }
    }

}