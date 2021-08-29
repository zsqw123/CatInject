package com.zsqw123.inject.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import java.io.File

open class BaseTransform : Transform() {
    companion object {
        private const val TRANSFORM_NAME = "CatInjectTransform"
    }

    override fun getName(): String = TRANSFORM_NAME
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

    protected open fun processJar(outputJarFile: File) = Unit
    protected open fun processDirectory(outputDirFile: File) = Unit
    protected open fun onTransformed() = Unit
}