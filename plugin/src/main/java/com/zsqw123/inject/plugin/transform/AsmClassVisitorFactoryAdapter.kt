@file:Suppress("UnstableApiUsage")

package com.zsqw123.inject.plugin.transform

import com.android.build.api.instrumentation.*
import org.gradle.api.provider.Property
import org.objectweb.asm.ClassVisitor

//class AsmClassVisitorFactoryAdapter(
//    override val instrumentationContext: InstrumentationContext,
//    override val parameters: Property<InstrumentationParameters.None>,
//    private val iTransform: ITransform
//) : AsmClassVisitorFactory<InstrumentationParameters.None> {
//    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
//        return iTransform.processClass(TransformClassData.createFromClassData(classContext.currentClassData), nextClassVisitor)
//    }
//
//    override fun isInstrumentable(classData: ClassData): Boolean {
//        return iTransform.needTransform(TransformClassData.createFromClassData(classData))
//    }
//}