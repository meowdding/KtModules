package me.owdding.ktmodules

import me.owdding.ktmodules.generated.TestProjectModules

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
    }

}