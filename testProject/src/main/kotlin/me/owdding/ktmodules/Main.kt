package me.owdding.ktmodules

import me.owdding.ktmodules.generated.TestProjectModules
import me.owdding.ktmodules.generated.TestProjectTests

@Module
object Main : TestInterface {

    var worked = false

    @JvmStatic
    fun main(args: Array<String>) {
        TestProjectModules.collected.forEach { it.silly() }
        TestProjectModules.init { println(it) }
        if (!worked) {
            throw IllegalStateException("Modules didn't load correctly.")
        } else {
            println(":D")
        }
        TestProjectTests.init { println(it) }
    }

    override fun silly() {

    }

}