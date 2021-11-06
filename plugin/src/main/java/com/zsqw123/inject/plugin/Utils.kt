package com.zsqw123.inject.plugin

import java.io.File
import java.util.*
import java.util.function.Predicate
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

internal typealias InjectImplsMap = MutableMap<String, List<String>>

internal fun pluginLog(msg: String) = println("CatInjectTransform---->$msg")

internal fun String.toClassName() = replace('/', '.')
internal fun String.toInternalName() = replace('.', '/')

internal fun String.isNeededClassName(): Boolean {
    return endsWith(".class") &&
        !startsWith("BuildConfig") &&
        !startsWith("R$") &&
        this != "R.class"
}

internal fun printMappedInterfaceAndImpls(map: InjectImplsMap) {
    map.forEach { (k, v) -> pluginLog("$k : $v") }
}

internal fun File.writeToZip(filter: (ZipEntry) -> Boolean, bytesTransform: (ByteArray) -> ByteArray) {
    val tmpFile = File(parent, "catClass${System.currentTimeMillis()}.jar")
    tmpFile.outputStream().use { tos ->
        JarOutputStream(tos).use { jarOut ->
            JarFile(this).use { jarFile ->
                jarFile.entries().asSequence().forEach { jarEntry ->
                    val ins = jarFile.getInputStream(jarEntry)
                    jarOut.putNextEntry(JarEntry(jarEntry.name))
                    if (filter(jarEntry)) {
                        jarOut.write(bytesTransform(ins.readBytes()))
                    } else {
                        jarOut.write(ins.readBytes())
                    }
                    jarOut.closeEntry()
                }
            }
        }
    }
    tmpFile.copyTo(this, true)
}

internal fun JarFile.hasEntry(str: String): Boolean = use {
    getEntry(str) != null
}