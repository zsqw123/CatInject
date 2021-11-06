package com.zsqw123.inject.plugin.transform

import com.android.build.api.instrumentation.*
import com.zsqw123.inject.plugin.*
import org.objectweb.asm.ClassVisitor

private object InjectRealTransform : ITransform {
    override fun needTransform(classData: TransformClassData): Boolean {
        return classData.classAnnotations.contains(Const.injectQualifiedName)
    }

    override fun processClass(classData: TransformClassData, originClassVisitor: ClassVisitor): ClassVisitor {
        if (classData.interfaces.isEmpty()) return originClassVisitor
        pluginLog("find impl: ${classData.className}, interfaces: ${classData.interfaces}")
        injectImplsMap[classData.className] = classData.interfaces
        return super.processClass(classData, originClassVisitor)
    }
}

@Suppress("UnstableApiUsage")
abstract class InjectTransform : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return InjectRealTransform.processClass(TransformClassData.createFromClassData(classContext.currentClassData), nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return InjectRealTransform.needTransform(TransformClassData.createFromClassData(classData))
    }
}