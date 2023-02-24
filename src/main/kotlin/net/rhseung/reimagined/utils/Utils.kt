package net.rhseung.reimagined.utils

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object Utils {
	fun <T> List<T>.append(index: Int, element: T): List<T> {
		val ret = this.toMutableList()
		try {
			ret.add(index, element)
		} catch (e: IndexOutOfBoundsException) {
			error("index out of bounds (index: $index, list: ${0 until ret.size})")
		}
		
		val a = arrayOf(1 to 1,2 to 2,3 to 3)
		a.associate { it.first to it.second }
		
		return ret.toList()
	}
	
	fun <T, K, V> Array<out T>.associateIndexed(action: (index: Int, element: T) -> Pair<K, V>): Map<K, V> {
		val map = mutableMapOf<K, V>()
		this.forEachIndexed { idx, t -> map[action(idx, t).first] = action(idx, t).second }
		return map.toMap()
	}
	
	fun <T> List<T>.append(element: T): List<T> {
		val ret = this.toMutableList()
		ret.add(element)
		return ret.toList()
	}
	
	fun <T> List<T>.up(element: T): List<T> {
		val ret = this.toMutableList()
		ret.add(element)
		return ret.toList()
	}
	
	fun <T: Any> createInstance(kclass: KClass<T>, vararg parameters: Any?): T {
		return kclass.primaryConstructor?.call(*parameters) ?: error("$kclass primary constructor is null")
	}
	
	fun <T: Any> createInstance(type: Type<out T>, vararg parameters: Any?): T {
		return createInstance(type.kclass, *parameters)
	}
	
	fun getClassName(kclass: KClass<*>): String {
		return kclass.java.simpleName
	}
}