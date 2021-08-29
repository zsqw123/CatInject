package com.zsqw123.inject

import java.lang.Exception
import java.lang.ref.WeakReference

class CatInjects private constructor() {
    private val instanceMap = mutableMapOf<String, WeakReference<Any>>()
    private val impls = mutableMapOf<String, ArrayList<String>>()

    private fun <T> ensureImpls(clazz: Class<T>): Sequence<String> {
        if (!clazz.isInterface)
            throw CatInjectException("CatInject require an interface: $clazz.")
        val currentImpls = impls[clazz.name]
        if (currentImpls.isNullOrEmpty()) throw CatInjectException("CatInject implementation was not found: $clazz.")
        return currentImpls.asSequence()
    }

    fun <T> getInstance(clazz: Class<T>): T = getAllInstances(clazz).first()
    fun <T> getAllInstances(clazz: Class<T>): Sequence<T> = ensureImpls(clazz).map(::getOrPutInstance).map(clazz::cast)

    private fun getOrPutInstance(className: String): Any {
        var instance = instanceMap[className]?.get()
        if (instance != null) {
            return instance
        }
        instance = Class.forName(className).getDeclaredConstructor().newInstance()
        instanceMap[className] = WeakReference(instance)
        return instance
    }

    fun addImpl(interfaceName: String, implName: String) {
        val list = impls[interfaceName] ?: ArrayList()
        list.add(implName)
        impls[interfaceName] = list
    }

    companion object {

        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED, ::CatInjects)

        inline fun <reified T> getInstance() = instance.getInstance(T::class.java)
        inline fun <reified T> getAllInstances() = instance.getAllInstances(T::class.java)
    }

}

class CatInjectException(msg: String) : Exception(msg)