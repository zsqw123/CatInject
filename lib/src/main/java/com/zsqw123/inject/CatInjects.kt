package com.zsqw123.inject

class CatInjects private constructor() {
    private val iteratorMap = hashMapOf<String, Iterator<Any>>()

    fun <T> getInstance(clazz: Class<T>): T = getAllInstances(clazz).firstOrNull() ?: throw CatInjectException("No impls found")

    @Suppress("UNCHECKED_CAST")
    fun <T> getAllInstances(clazz: Class<T>): Sequence<T> {
        val name = clazz.name
        iteratorMap.putIfAbsent(name, InstanceIterator(clazz))
        return Sequence {
            (iteratorMap[name] as InstanceIterator<*>).apply {
                nowIdx = 0
            }
        } as Sequence<T>
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
    var nowIdx = 0
    private val internalName = InjectsUtil.getInternalName(clazz)
    private val count = InjectsUtil.anyImplsCount(internalName)
    private val innerArr = arrayOfNulls<Any>(count)
    override fun hasNext(): Boolean = nowIdx < count
    override fun next(): Any {
        val cache = innerArr[nowIdx]
        return if (cache == null) {
            val o = InjectsUtil.findAnyWithIndex(internalName, nowIdx)
                ?: throw IllegalStateException("不会吧 这个真的会抛出吗")
            innerArr[nowIdx++] = o
            o
        } else {
            nowIdx++
            cache
        }
    }
}