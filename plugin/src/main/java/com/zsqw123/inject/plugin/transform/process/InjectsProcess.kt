package com.zsqw123.inject.plugin.transform.process

import com.zsqw123.inject.plugin.Const
import com.zsqw123.inject.plugin.IP
import com.zsqw123.inject.plugin.InjectImpl
import com.zsqw123.inject.plugin.printMappedInterfaceAndImpls
import com.zsqw123.inject.plugin.vistor.InjectsVistor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

object InjectsProcess : BytesProcess {
    override fun process(bytes: ByteArray): ByteArray? {
        if (IP.data.injectImpls.isEmpty()) return null
        val classReader = ClassReader(bytes)
        val internalName = classReader.className
        if (internalName != Const.injectsUtilInternalName) return null
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val injectImplMap = IP.data.injectImpls
            .groupBy(InjectImpl::interfaceName) // interface: [impls]
            .mapValues { it.value.map(InjectImpl::implName) } // interface: [implsName]
            .toMutableMap()
        printMappedInterfaceAndImpls(injectImplMap)
        val visitor = InjectsVistor(classWriter, injectImplMap)
        classReader.accept(visitor, 0)
        return classWriter.toByteArray()
    }
}