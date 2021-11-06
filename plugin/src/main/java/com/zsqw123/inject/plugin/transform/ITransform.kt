package com.zsqw123.inject.plugin.transform

import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor

interface ITransform {
    fun processClass(classData: TransformClassData, originClassVisitor: ClassVisitor): ClassVisitor = originClassVisitor
    fun needTransform(classData: TransformClassData) = false
}

/**
 * 此处是对 Gradle 自带的一个封装, 因为官方 API 不够稳定
 *
 * @see com.android.build.api.instrumentation.ClassData
 */
class TransformClassData(

    /**
     * Fully qualified name of the class.
     *
     * e.g. com.zsqw123.transform.TransformClassData
     */
    val className: String,
    val classAnnotations: List<String>,

    /**
     * 它实现了的接口, 也包括它祖宗们实现的
     */
    val interfaces: List<String>,

    /**
     * 它的祖宗们
     */
    val superClasses: List<String>,
) {
    companion object {
        @Suppress("UnstableApiUsage")
        fun createFromClassData(classData: ClassData): TransformClassData {
            return TransformClassData(
                classData.className, classData.classAnnotations,
                classData.interfaces, classData.superClasses
            )
        }
    }
}