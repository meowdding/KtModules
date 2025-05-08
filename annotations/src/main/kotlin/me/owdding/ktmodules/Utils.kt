package me.owdding.ktmodules

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal inline fun <reified T : Annotation> Resolver.getAnnotatedSymbols() = this.getSymbolsWithAnnotation(T::class.qualifiedName!!).toList()
internal fun Resolver.getAnnotatedSymbols(name: KSClassDeclaration) = this.getSymbolsWithAnnotation(name.qualifiedName!!.asString()).toList()
internal inline fun <reified T : Annotation> Resolver.getClassDeclaration() = this.getClassDeclarationByName(T::class.qualifiedName!!)