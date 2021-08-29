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
        //存放输出文件的目录
        val outputProvider = transformInvocation.outputProvider

        if (!isIncremental) {
            //如果未开启增量编译，则清除缓存
            outputProvider.deleteAll()
        }
        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                //获取输出目录
                val outputJarFile = outputProvider.getContentLocation(
                    jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR
                )
                if (isIncremental && jarInput.status == Status.REMOVED) {
                    //如果开启了增量编译并且当该jar被移除，则从输出目录中移除掉
                    outputJarFile.deleteRecursively()
                } else {
                    //如果没有开启增量编译，或者jar是其它状态，则复制到输出目录
                    jarInput.file.copyTo(outputJarFile, true)
                }
                if (!isIncremental || jarInput.status != Status.REMOVED) {
                    //处理jar
                    processJar(jarInput.file, outputJarFile)
                }
            }
            input.directoryInputs.forEach { dirInput ->
                //复制到输出目录
                val outputDir = outputProvider.getContentLocation(
                    dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
                )
                dirInput.file.copyRecursively(outputDir, true)
                //处理本地class
                processDirectory(dirInput.file, outputDir)
            }
        }
        onTransformed()
    }

    protected open fun processJar(inputJarFile: File, outputJarFile: File) = Unit
    protected open fun processDirectory(inputDirFile: File, outputDirFile: File) = Unit
    protected open fun onTransformed() = Unit
}