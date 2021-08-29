package com.zsqw123.inject

class CatInjects private constructor() {
    private val instanceMap = mutableMapOf<String, Sequence<Any>>()

    fun <T> getInstance(clazz: Class<T>): T = getAllInstances(clazz).firstOrNull() ?: throw CatInjectException("No impls found")

    @Suppress("UNCHECKED_CAST")
    fun <T> getAllInstances(clazz: Class<T>): Sequence<T> {
        if (instanceMap.containsKey(clazz.name)) return instanceMap[clazz.name] as Sequence<T>
        val sequence = InstanceIterator(clazz).asSequence()
        instanceMap[clazz.name] = sequence
        return sequence as Sequence<T>
    }

    companion object {

        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED, ::CatInjects)

        inline fun <reified T> getInstance() = instance.getInstance(T::class.java)
        inline fun <reified T> getAllInstances() = instance.getAllInstances(T::class.java)
    }

}

class CatInjectException(msg: String) : Exception(msg)

class InstanceIterator<T>(clazz: Class<T>) : Iterator<Any> {
    private var nowIdx = 0
    private val internalName = InjectsUtil.getInternalName(clazz)
    private val count = InjectsUtil.anyImplsCount(internalName)
    override fun hasNext(): Boolean = nowIdx < count
    override fun next(): Any = InjectsUtil.findAnyWithIndex(internalName, nowIdx++)
        ?: throw IllegalStateException("不会吧 这个真的会抛出吗")
}