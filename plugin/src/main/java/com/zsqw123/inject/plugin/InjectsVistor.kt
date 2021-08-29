package com.zsqw123.inject.plugin

import com.zsqw123.inject.CatInjects
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.Method

class InjectsVistor(
    classVisitor: ClassVisitor,
    private val injectImplsMap: InjectImplsMap
) : ClassVisitor(Opcodes.ASM9, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "<init>" && descriptor == "()V") {
            return InjectAdviceAdapter(api, methodVisitor, access, name, descriptor)
        }
        return methodVisitor
    }

    inner class InjectAdviceAdapter(
        api: Int,
        methodVisitor: MethodVisitor?,
        access: Int,
        name: String?,
        descriptor: String?
    ) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

        private val injectType = Type.getType(CatInjects::class.java)
        private val addImplementationMethod = Method(
            CatInjects::addImpl.name, Type.VOID_TYPE,
            arrayOf(Type.getType(String::class.java), Type.getType(String::class.java))
        )
//        private val addImplementationMethod = Method(Injects::addImpl.name, "(Ljava/lang/String;Ljava/lang/String;)V")

        override fun visitInsn(opcode: Int) {
            if (opcode == ARETURN || opcode == RETURN) {
                injectImplsMap.forEach { (injectInterface, implList) ->
                    implList.forEach { implementation ->
                        insertCode(injectInterface, implementation)

                    }
                }
            }
            super.visitInsn(opcode)
        }

        private fun insertCode(injectInterface: String, injectImpl: String) {
            //ALOAD 0
            loadThis()
            //LDC "injectInterface"
            visitLdcInsn(injectInterface)
            //LDC "injectImpl"
            visitLdcInsn(injectImpl)
            //INVOKE
            invokeVirtual(injectType, addImplementationMethod)
        }

    }

}