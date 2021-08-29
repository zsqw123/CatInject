package com.zsqw123.inject.plugin.asm

import com.zsqw123.inject.plugin.InjectImplsMap
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

object ASMCodeGen {
    private const val STRING_INTERNALNAME = "java/lang/String"

    fun genImplsMethods(methodVisitor: MethodVisitor, interfaceImplsMap: InjectImplsMap) = methodVisitor.apply {
        interfaceImplsMap.forEach { (interfaceName, implsName) ->
            // if(interfaceInternalName.equals(var1))
            genJudgeClassMethod(interfaceName) {
                val defaultLabel = Label()
                // switch(var2){
                genSwitchMethod(defaultLabel, implsName) { label, impl ->
                    // case label:
                    genLableWithReturn(label) {
                        // return new Impl();
                        genNewClassMethod(impl)
                    }
                }
                // default: return null; }
                genLableWithReturn(defaultLabel) {
                    visitInsn(ACONST_NULL)
                }
            }
        }
    }

    fun genMethodEndWithNull(methodVisitor: MethodVisitor) = methodVisitor.apply {
        visitInsn(ACONST_NULL)
        visitInsn(ARETURN)
        visitMaxs(2, 3)
    }

    fun genImplCount(methodVisitor: MethodVisitor, interfaceImplsMap: InjectImplsMap) = methodVisitor.apply {
        interfaceImplsMap.forEach { (interfaceName, implsName) ->
            genJudgeClassMethod(interfaceName) {
                returnInt(implsName.size)
            }
        }
    }

    fun genMethodEndWithZero(methodVisitor: MethodVisitor) = methodVisitor.apply {
        visitInsn(ICONST_0)
        visitInsn(IRETURN)
        visitMaxs(2, 2)
    }

    // if(interfaceInternalName.equals(var1)){ genInsideMethod(); }
    private fun MethodVisitor.genJudgeClassMethod(interfaceInternalName: String, genInsideMethod: MethodVisitor.() -> Unit) {
        visitVarInsn(ALOAD, 1)
        visitLdcInsn(interfaceInternalName)
        visitMethodInsn(INVOKEVIRTUAL, STRING_INTERNALNAME, "equals", "(Ljava/lang/Object;)Z", false)
        val label = Label()
        visitJumpInsn(IFEQ, label)
        genInsideMethod()
        visitLabel(label)
    }

    // switch(var2){ labels -> }
    private fun MethodVisitor.genSwitchMethod(
        defaultLabel: Label, implsInternalNames: List<String>,
        labelCallNext: (labels: Label, implName: String) -> Unit
    ) {
        val keys = IntArray(implsInternalNames.size) { it }
        val labels = Array(implsInternalNames.size) { Label() }
        visitVarInsn(ILOAD, 2)
        visitLookupSwitchInsn(defaultLabel, keys, labels)
        for (i in labels.indices) {
            labelCallNext(labels[i], implsInternalNames[i])
        }
    }

    // case label: return genInsideMethod();
    private fun MethodVisitor.genLableWithReturn(label: Label, genInsideMethod: MethodVisitor.() -> Unit) {
        visitLabel(label)
        genInsideMethod()
        visitInsn(ARETURN)
    }

    // new Impl();
    private fun MethodVisitor.genNewClassMethod(internalName: String) {
        visitTypeInsn(NEW, internalName)
        visitInsn(DUP)
        visitMethodInsn(INVOKESPECIAL, internalName, "<init>", "()V", false)
    }

    private fun MethodVisitor.returnInt(num: Int) {
        visitIntInsn(SIPUSH, num)
        visitInsn(IRETURN)
    }
}