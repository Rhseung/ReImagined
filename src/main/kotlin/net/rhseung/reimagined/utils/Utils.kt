package net.rhseung.reimagined.utils

object Utils {
	fun <T> List<T>.append(index: Int, element: T): List<T> {
		val ret = this.toMutableList()
		try {
			ret.add(index, element)
		} catch (e: IndexOutOfBoundsException) {
			error("index out of bounds (index: $index, list: ${0 until ret.size})")
		}
		return ret.toList()
	}
}