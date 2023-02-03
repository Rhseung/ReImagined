package net.rhseung.reimagined.utils

import java.lang.Integer.min
import kotlin.math.max

class Color constructor(
	val R: Int, val G: Int, val B: Int
) {
	companion object {
		val WHITE = Color(255, 255, 255)
		val GRAY = Color(170, 170, 170)
		val DARK_GRAY = Color(85, 85, 85)
		
		val RED = Color(255, 85, 85)
		val DARK_RED = Color(170, 0, 0)
		val GREEN = Color(85, 255, 85)
		val DARK_GREEN = Color(0, 170, 0)
		val BLUE = Color(85, 85, 255)
		val DARK_BLUE = Color(0, 0, 170)
		
		val YELLOW = Color(255, 255, 85)
		val DARK_YELLOW = Color(170, 170, 0)
		val AQUA = Color(85, 255, 255)
		val DARK_AQUA = Color(0, 170, 170)
		val PINK = Color(255, 85, 255)
		val DARK_PINK = Color(170, 0, 170)
		
		val GOLD = Color(255, 170, 0)
		val SMOOTH_RED = Color(255, 125, 125)
		val SMOOTH_VIOLET = Color(193, 143, 255)
		val SMOOTH_BLUE = Color(143, 201, 255)
	}
	
	fun toHex(): Int {
		return Integer.parseInt(
			Integer.toHexString(1 shl 24 or (R shl 16) or (G shl 8) or B).substring(1), 16
		)
	}
	
	fun darker(delta: Int): Color {
		check(delta >= 0) { "use `Color#brighter`" }
		
		return Color(
			max(R - delta, 0),
			max(G - delta, 0),
			max(B - delta, 0)
		)
	}
	
	fun brighter(delta: Int): Color {
		check(delta >= 0) { "use `Color#darker`" }
		
		return Color(
			min(R + delta, 255),
			min(G + delta, 255),
			min(B + delta, 255)
		)
	}
	
	fun gradient(endColor: Color, ratio: Float): Color {
		return Color(
			(R * ratio + endColor.R * (1 - ratio)).toInt(),
			(G * ratio + endColor.G * (1 - ratio)).toInt(),
			(B * ratio + endColor.B * (1 - ratio)).toInt()
		)
	}
}