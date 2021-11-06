package com.zsqw123.inject.plugin.transform.process

import com.zsqw123.inject.plugin.IP
import com.zsqw123.inject.plugin.vistor.InjectClassVistor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

object InjectProcess : BytesProcess {
    override fun read(bytes: ByteArray) {
        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val injectClassVister = InjectClassVistor(IP.data.injectInterfaceInternalNames, classWriter)
        classReader.accept(injectClassVister, 0)
    }
}