package com.zsqw123.inject.plugin.vistor

import com.zsqw123.inject.plugin.Const
import com.zsqw123.inject.plugin.pluginLog
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

// 扫描所有被 Inject 注解的类
class InjectClassVistor(private val interfaces: HashSet<String>, classVisitor: ClassVisitor) : ClassVisitor(Opcodes.ASM5, classVisitor) {
    private lateinit var internalName: String
    override fun visit(
        version: Int, access: Int, name: String,
        signature: String?, superName: String?, interfaces: Array<String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        internalName = name
    }

    override fun visitAnnotation(desc: String?, visible: Boolean): AnnotationVisitor {
        if (desc == Const.injectAnnotationDescriptor) {
            pluginLog("in class: $internalName --- find: $desc")
            interfaces.add(internalName)
        }
        return super.visitAnnotation(desc, visible)
    }
}