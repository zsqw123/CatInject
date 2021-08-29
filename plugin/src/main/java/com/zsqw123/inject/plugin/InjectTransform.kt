package com.zsqw123.inject.plugin

import com.zsqw123.inject.CatInjects
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type
import java.io.File
import java.util.jar.JarFile

class InjectTransform : BaseTransform() {
    private val injectInterfaceInternalNames = HashSet<String>()
    private val injectImpls = ArrayList<InjectImpl>()
    private lateinit var injectsJarOutputFile: File

    override fun processDirectory(outputDirFile: File) {
        val needClassesSequence = outputDirFile.walkTopDown().filter { it.name.isNeededClassName() }.map { it.readBytes() }
        scanInjectAnnotation(needClassesSequence)
        scanInjectImpls(needClassesSequence)
    }

    override fun processJar(outputJarFile: File) {
        if (!::injectsJarOutputFile.isInitialized) {
            if (JarFile(outputJarFile).hasEntry(Type.getInternalName(CatInjects::class.java) + ".class")) {
                pluginLog("find jar include CatInjects:${outputJarFile.name}")
                injectsJarOutputFile = outputJarFile
                return
            }
        }
        JarFile(outputJarFile).use { jarFile ->
            val bytesSequence = jarFile.entries().asSequence()
                .filter { it.name.isNeededClassName() }
                .map { jarFile.getInputStream(it).readBytes() }
            scanInjectAnnotation(bytesSequence)
        }
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

    private fun modifyInjects(injectImplMap: InjectImplsMap) {
        injectsJarOutputFile.writeToZip(
            filter = { je -> je.name == "${Type.getInternalName(CatInjects::class.java)}.class" },
            bytesTransform = { bytes ->
                val classReader = ClassReader(bytes)
                val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                val visitor = InjectsVistor(classWriter, injectImplMap)
                classReader.accept(visitor, 0)
                classWriter.toByteArray()
            }
        )
    }

    override fun onTransformed() {
        val injectImplMap = injectImpls
            .groupBy(InjectImpl::interfaceName) // interface: [impls]
            .mapValues { it.value.map(InjectImpl::implName) } // interface: [implsName]
        printMappedInterfaceAndImpls(injectImplMap)
        modifyInjects(injectImplMap)
    }
}