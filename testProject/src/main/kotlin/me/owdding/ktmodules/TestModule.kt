package me.owdding.ktmodules

@Module
object TestModule : TestInterface {
    override fun silly() {

    }

    init {
        Main.worked = true
    }

}