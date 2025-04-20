package me.owdding.ktmodules

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.writeTo

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Module

internal class Processor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val context: ModuleContext,
) : SymbolProcessor {

    private var ran = false

    private fun validateSymbol(symbol: KSAnnotated): KSClassDeclaration? {
        if (!symbol.validate()) {
            logger.warn("Symbol is not valid: $symbol")
            return null
        }

        if (symbol !is KSClassDeclaration || symbol.classKind != ClassKind.OBJECT) {
            logger.error("@Module is only valid on objects", symbol)
            return null
        }
        return symbol
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (ran) return emptyList()
        ran = true

        val annotated = resolver.getSymbolsWithAnnotation(Module::class.qualifiedName!!).toList()
        val validModules = annotated.mapNotNull { validateSymbol(it) }

        logger.warn("--- Module Processor ---")
        logger.warn("Found ${validModules.size} modules")
        logger.warn("Generating ${context.projectName}Modules.kt")

        val file = FileSpec.builder(context.generatedPackage, "${context.projectName}Modules")
            .indent("    ")
            .addType(
                TypeSpec.objectBuilder("${context.projectName}Modules").apply {
                    this.addModifiers(KModifier.INTERNAL)
                    this.addProperty(
                        PropertySpec.builder(
                            "modules",
                            List::class.asClassName().parameterizedBy(Any::class.asTypeName()),
                            KModifier.PRIVATE
                        ).initializer(
                            CodeBlock.builder()
                                .apply {
                                    add("listOf(\n")

                                    validModules.forEach { module ->
                                        add("    ${module.qualifiedName!!.asString()},\n")
                                    }

                                    add(")")
                                }.build()
                        ).build()
                    )
                    this.addFunction(
                        FunSpec.builder("init")
                            .addParameter(
                                "applicator",
                                LambdaTypeName.get(
                                    parameters = arrayOf<TypeName>(Any::class.asTypeName()),
                                    returnType = Unit::class.asTypeName()
                                )
                            )
                            .addStatement("modules.forEach(applicator)")
                            .build()
                    )
                }.build(),
            )

        file.build().writeTo(
            codeGenerator,
            Dependencies(true, *validModules.mapNotNull(KSClassDeclaration::containingFile).toTypedArray()),
        )

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
                this.require("meowdding.modules.project_name", options, logger).let {
                    it.replaceFirstChar { first -> first.uppercase() }
                },
                this.require("meowdding.modules.package", options, logger),
            )
        }

        private fun require(option: String, map: Map<String, String>, logger: KSPLogger): String {
            return requireNotNull(map[option], logger)
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