@file:Suppress("DEPRECATION")

package com.zsqw123.inject.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.zsqw123.inject.plugin.pluginLog
import com.zsqw123.inject.plugin.transform.process.FileProcess

abstract class BaseTransform : Transform(), FileProcess {
    companion object {
        private const val TRANSFORM_NAME = "CatInjectTransform"
    }

    override fun getName(): String = TRANSFORM_NAME + this::class.java.simpleName
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_CLASS
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_PROJECT
    override fun isIncremental(): Boolean = true
    override fun transform(transformInvocation: TransformInvocation) {
        pluginLog("transform start")
        val outputProvider = transformInvocation.outputProvider

        if (!isIncremental) {
            outputProvider.deleteAll()
        }
        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                val outputJarFile = outputProvider.getContentLocation(
                    jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR
                )
                if (isIncremental && jarInput.status == Status.REMOVED) {
                    outputJarFile.deleteRecursively()
                } else {
                    jarInput.file.copyTo(outputJarFile, true)
                }
                if (!isIncremental || jarInput.status != Status.REMOVED) {
                    processJar(outputJarFile)
                }
            }
            input.directoryInputs.forEach { dirInput ->
                val outputDir = outputProvider.getContentLocation(
                    dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
                )
                outputDir.deleteRecursively()
                dirInput.file.copyRecursively(outputDir, true)
                processDirectory(outputDir)
            }
        }
        onTransformed()
    }
}