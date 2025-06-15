package me.owdding.ktmodules

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.owdding.kotlinpoet.*

internal data class ObjectCreator(
    val context: ModuleContext,
    val annotation: KSClassDeclaration,
    val lines: List<String>,
) {
    @OptIn(KspExperimental::class)
    fun create(): TypeSpec {

        val name = (
                if (annotation.isAnnotationPresent(AutoCollect::class)) {
                    annotation.getAnnotationsByType(AutoCollect::class).first().nameOverride
                } else {
                    ""
                }
                ).takeUnless { it.isEmpty() }
            ?: if (annotation.qualifiedName!!.asString() == Module::class.qualifiedName!!) {
                "Modules"
            } else annotation.simpleName.asString()

        return TypeSpec.objectBuilder("${context.projectName}$name").apply {
            this.addModifiers(KModifier.INTERNAL)
            this.addProperty(
                PropertySpec.builder("collected").initializer(
                    CodeBlock.builder()
                        .apply {
                            add("listOf(\n")

                            add(lines.joinToString(",\n", postfix = "\n", transform = {"    $it"}))

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
                    .addStatement("collected.forEach(applicator)")
                    .build()
            )
        }.build()

    }


}