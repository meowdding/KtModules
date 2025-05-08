package me.owdding.ktmodules

import me.owdding.ktmodules.generated.TestProjectModules
import me.owdding.ktmodules.generated.TestProjectTests

@Module
object Main {

    var worked = false

    @JvmStatic
    fun main(args: Array<String>) {
        TestProjectModules.init { println(it) }
        if (!worked) {
            throw IllegalStateException("Modules didn't load correctly.")
        } else {
            println(":D")
        }
        TestProjectTests.init { println(it) }
    }

}