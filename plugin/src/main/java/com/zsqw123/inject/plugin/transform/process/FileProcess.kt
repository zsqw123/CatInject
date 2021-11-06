package com.zsqw123.inject.plugin.transform.process

import java.io.File

interface FileProcess {
    fun processJar(outputJarFile: File) {}
    fun processDirectory(outputDirFile: File) {}
    fun onTransformed() {}
}