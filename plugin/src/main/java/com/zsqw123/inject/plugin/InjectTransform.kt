package com.zsqw123.inject.plugin

import com.zsqw123.inject.CatInjects
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type
import java.io.File
import java.util.jar.JarFile

class InjectTransform(project: Project) : BaseTransform() {
    private val injectInterfaceInternalNames = HashSet<String>()
    private val injectImpls = ArrayList<InjectImpl>()
    private lateinit var injectsJarOutputFile: File

    override fun processDirectory(inputDirFile: File, outputDirFile: File) {
        scanClasses(outputDirFile)
    }

    override fun processJar(inputJarFile: File, outputJarFile: File) {
        if (!::injectsJarOutputFile.isInitialized) {
            if (JarFile(outputJarFile).hasEntry(Type.getInternalName(CatInjects::class.java) + ".class")) {
                pluginLog("find jar include Injects:${outputJarFile.name}")
                injectsJarOutputFile = outputJarFile
                return
            }
        }
        val unzipPath = outputJarFile.unzipTo()
        scanClasses(unzipPath)
        unzipPath.deleteRecursively()
    }

    private fun scanInjectAnnotation(bytesSequence: Sequence<ByteArray>) {
        bytesSequence.forEach {
            val classReader = ClassReader(it)
            val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
            val injectClassVister = InjectClassVistor(injectInterfaceInternalNames, classWriter)
            classReader.accept(injectClassVister, 0)
        }
    }

    private fun scanInjectImpls(bytesSequence: Sequence<ByteArray>) {
        bytesSequence.forEach {
            val classReader = ClassReader(it)
            val className = classReader.className.toClassName()
            val classInterfaces = classReader.interfaces
            val findedInterface = injectInterfaceInternalNames.find(classInterfaces::contains)
            if (findedInterface != null) {
                val findedInterfaceClassName = findedInterface.toClassName()
                pluginLog("findedInterface = [$findedInterfaceClassName], implClassName = [$className]")
                injectImpls.add(InjectImpl(className, findedInterfaceClassName))
            }
        }
    }

    private fun scanClasses(classesDir: File) {
        val needClassesSequence = classesDir.walkTopDown().filter { it.name.isNeededClassName() }.map { it.readBytes() }
        scanInjectAnnotation(needClassesSequence)
        scanInjectImpls(needClassesSequence)
    }

    private fun modifyInjects(injectImplMap: InjectImplsMap) {
        val unzipFile = injectsJarOutputFile.unzipTo()
        val injectsClassFile = unzipFile.walkTopDown().first {
            it.name == "${CatInjects::class.java.simpleName}.class"
        }

        val classReader = ClassReader(injectsClassFile.readBytes())
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val visitor = InjectsVistor(classWriter, injectImplMap)
        classReader.accept(visitor, 0)

        injectsClassFile.writeBytes(classWriter.toByteArray())
        unzipFile.zipTo()
        unzipFile.deleteRecursively()
    }

    override fun onTransformed() {
        val injectImplMap = injectImpls
            .groupBy(InjectImpl::interfaceName) // interface: [impls]
            .mapValues { it.value.map(InjectImpl::implName) } // interface: [implsName]
        printMappedInterfaceAndImpls(injectImplMap)
        modifyInjects(injectImplMap)
    }
}