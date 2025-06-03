package me.owdding.ktmodules.generators

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import me.owdding.ktmodules.generators.Generators.escapeQualifiedName

internal object VariableGenerator : Generator {

    override fun isFor(annotated: KSAnnotated): Boolean {
        if (annotated !is KSPropertyDeclaration) return false
        if (annotated.parent == null && annotated.isPublic()) return true
        if (annotated.parent is KSClassDeclaration && (annotated.parent as? KSClassDeclaration)?.classKind == ClassKind.OBJECT) return true
        return false
    }

    override fun emit(annotated: KSAnnotated): String {
        return (annotated as KSPropertyDeclaration).escapeQualifiedName()
    }

}