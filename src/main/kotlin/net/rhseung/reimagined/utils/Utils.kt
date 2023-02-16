package net.rhseung.reimagined.utils

object Utils {
	fun <T> List<T>.append(index: Int, element: T): List<T> {
		val ret = this.toMutableList()
		ret.add(index, element)
		return ret.toList()
	}
}