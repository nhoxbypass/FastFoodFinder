package com.iceteaviet.fastfoodfinder.utils.shell

/**
 * Wrapper class of {@link Runtime} to exec shell command
 *
 * Wrap to support mock testing. May need to convert to non-static to remove PowerMock
 */
object AppShell {
    fun exec(command: String): Process {
        return Runtime.getRuntime().exec(command)
    }
}