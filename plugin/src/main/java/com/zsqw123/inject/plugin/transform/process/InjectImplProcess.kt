package com.zsqw123.inject.plugin.transform.process

import com.zsqw123.inject.plugin.IP
import com.zsqw123.inject.plugin.InjectImpl
import com.zsqw123.inject.plugin.pluginLog
import com.zsqw123.inject.plugin.toInternalName
import org.objectweb.asm.ClassReader

object InjectImplProcess : BytesProcess {
    override fun read(bytes: ByteArray) {
        val classReader = ClassReader(bytes)
        val className = classReader.className
        val classInterfaces = classReader.interfaces
        val findedInterface = IP.data.injectInterfaceInternalNames.find(classInterfaces::contains)
        if (findedInterface != null) {
            pluginLog("findedInterface = [${findedInterface.toInternalName()}], implClassName = [${className.toInternalName()}]")
            IP.data.injectImpls.add(InjectImpl(className, findedInterface))
        }
    }
}