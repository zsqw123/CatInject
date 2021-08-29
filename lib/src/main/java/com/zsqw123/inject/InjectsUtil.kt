package com.zsqw123.inject

object InjectsUtil {
    fun findAnyWithIndex(classInternalName: String, idx: Int): Any? {
        return null
    }

    fun anyImplsCount(classInternalName: String): Int {
        return 0
    }

    internal fun <T> getInternalName(clazz: Class<T>) = clazz.name.replace('.', '/')
}