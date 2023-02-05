package dev.gluton.muswitch.util

fun <T> tryOrNull(block: () -> T): T? = try { block() } catch (e: Exception) { println("${e::class.simpleName}: ${e.message}"); null }
