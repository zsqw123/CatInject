package com.zsqw123.inject.plugin.transform

import com.android.build.api.instrumentation.*
import com.zsqw123.inject.plugin.Const
import com.zsqw123.inject.plugin.InjectImplsMap
import com.zsqw123.inject.plugin.vistor.InjectsVistor
import org.objectweb.asm.ClassVisitor

val injectImplsMap: InjectImplsMap = mutableMapOf()

private object InjectsModifierRealTransform : ITransform {
    override fun needTransform(classData: TransformClassData): Boolean {
        return classData.className == Const.injectsQualifiedName
    }

    override fun processClass(classData: TransformClassData, originClassVisitor: ClassVisitor): ClassVisitor {
        return InjectsVistor(originClassVisitor, injectImplsMap)
    }
}

@Suppress("UnstableApiUsage")
abstract class InjectsModifierTransform : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return InjectsModifierRealTransform.processClass(TransformClassData.createFromClassData(classContext.currentClassData), nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return InjectsModifierRealTransform.needTransform(TransformClassData.createFromClassData(classData))
    }
}
