package com.zsqw123.inject.plugin.transform.process

import com.zsqw123.inject.plugin.isValidClassName
import com.zsqw123.inject.plugin.pluginLog
import com.zsqw123.inject.plugin.writeToZip
import java.io.File
import java.util.jar.JarFile

interface BytesProcess {
    fun read(bytes: ByteArray) {}
    fun process(bytes: ByteArray): ByteArray? {
        read(bytes)
        return null
    }
}

fun BytesProcess.convertToFileProcess(): FileProcess {
    return object : FileProcess {
        override fun processDirectory(outputDirFile: File) {
            outputDirFile.walkTopDown().filter { it.name.isValidClassName }.forEach { singleFile ->
                val readBytes = singleFile.readBytes()
                val processBytes = process(readBytes) ?: return@forEach
                singleFile.writeBytes(processBytes)
            }
        }

        override fun processJar(outputJarFile: File) {
            val outputJar = JarFile(outputJarFile)
            outputJar.entries().asSequence()
                .filter { it.name.isValidClassName }
                .forEach { jarEntry ->
                    val ins = outputJar.getInputStream(jarEntry)
                    val readBytes = ins.readBytes()
                    ins.close()
                    val processBytes = process(readBytes) ?: return@forEach
                    outputJar.close()
                    outputJarFile.writeToZip({ zipEntry -> zipEntry.name == jarEntry.name }
                    ) { processBytes }
                    return
                }
            outputJar.close()
        }
    }
}