package me.owdding.ktmodules

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import me.owdding.ktmodules.generators.Generators

internal class Processor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val context: ModuleContext,
) : SymbolProcessor {

    private var ran = false

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (ran) return emptyList()
        ran = true

        val annotated = buildList {
            addAll(resolver.getAnnotatedSymbols<AutoCollect>().map { it as KSClassDeclaration })
            add(resolver.getClassDeclaration<Module>())
        }.filterNotNull().associateWith { resolver.getAnnotatedSymbols(it) }
        val resolved = annotated.mapValues { (_, v) -> v.mapNotNull { Generators.generate(it) } }

        logger.warn("--- Module Processor ---")
        annotated.forEach { (key, value) ->
            logger.warn("Found ${value.size} for @${key.simpleName.asString()}")
        }
        logger.warn("Generating ${context.projectName}Modules.kt")

        val file = FileSpec.builder(context.generatedPackage, "${context.projectName}Modules")
            .indent("    ")
            .apply {
                resolved.map { (k, v) -> ObjectCreator(context, k, v) }.forEach {
                    addType(it.create())
                }
            }

        file.build().writeTo(codeGenerator, Dependencies(true))

        return emptyList()
    }
}

internal class ModuleProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment,
    ): SymbolProcessor {
        return Processor(
            environment.codeGenerator,
            environment.logger,
            ModuleContext.create(environment.options, environment.logger)
        )
    }
}

internal data class ModuleContext(
    val projectName: String,
    val generatedPackage: String,
) {
    companion object {
        fun create(
            options: Map<String, String>,
            logger: KSPLogger,
        ): ModuleContext {
            return ModuleContext(
                this.require("project_name", options, logger).let {
                    it.replaceFirstChar { first -> first.uppercase() }
                },
                this.require("package", options, logger),
            )
        }

        private fun require(option: String, map: Map<String, String>, logger: KSPLogger): String {
            return requireNotNull(map["meowdding.modules.$option"] ?: map["meowdding.$option"], logger)
        }

        private fun <T> requireNotNull(value: T?, logger: KSPLogger): T {
            if (value == null) {
                """
Please make sure to include the following in your build.gradle(.kts) file!

ksp {
    arg("meowdding.modules.project_name", project.name)
    arg("meowdding.modules.package", TODO("Change to your generated code package"))
}
                """.split("\n").forEach { logger.error(it) }
                throw IllegalStateException("Module processor wasn't configured correctly!")
            }
            return value
        }
    }
}