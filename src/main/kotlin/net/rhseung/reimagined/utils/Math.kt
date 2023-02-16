package net.rhseung.reimagined.utils

import kotlin.math.pow
import kotlin.math.roundToInt

object Math {
	fun sum(vararg numbers: Int): Int {
		return numbers.reduce { acc, cur -> acc + cur }
	}
	
	fun average(vararg numbers: Int): Float {
		return sum(*numbers).toFloat() / numbers.count()
	}
	
	fun sum(vararg numbers: Float): Float {
		return numbers.reduce { acc, cur -> acc + cur }
	}
	
	fun average(vararg numbers: Float): Float {
		return sum(*numbers) / numbers.count()
	}
	
	fun Int.pow(power: Int): Int {
		var ret = 1
		var exp = power
		while (exp > 0) {
			ret *= this
			exp--
		}
		return ret
	}
	
	fun Float.roundTo(n: Int = 0): Float {
		val p = 10F.pow(n)
		return (this * p).roundToInt() / p
	}
}