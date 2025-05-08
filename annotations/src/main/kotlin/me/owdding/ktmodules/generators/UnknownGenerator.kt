package me.owdding.ktmodules.generators

import com.google.devtools.ksp.symbol.KSAnnotated

internal object UnknownGenerator : Generator {

    override fun isFor(annotated: KSAnnotated) = true
    override fun emit(annotated: KSAnnotated) = "// Type at ${annotated.location} is not supported atm."

}