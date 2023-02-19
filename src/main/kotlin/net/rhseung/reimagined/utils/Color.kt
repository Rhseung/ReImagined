package net.rhseung.reimagined.utils

import java.lang.Integer.min
import kotlin.math.max
import kotlin.math.roundToInt

class Color {
	val R: Int
	val G: Int
	val B: Int
	val H: Int
	val S: Float
	val V: Float
	
	constructor(R: Int, G: Int, B: Int) {
		this.R = R
		this.G = G
		this.B = B
		
		val r = R / 255.0F
		val g = G / 255.0F
		val b = B / 255.0F
		
		val max = maxOf(r, g, b)
		val min = minOf(r, g, b)
		
		this.H = ((when (max) {
			min -> 0.0F
			r -> (g - b) / (max - min)
			g -> 2 + (b - r) / (max - min)
			b -> 4 + (r - g) / (max - min)
			else -> 0.0F
		} * 60).roundToInt() + 360) % 360
		
		this.S = when (max) {
			0.0F -> 0.0F
			else -> (max - min) / max
		}
		
		this.V = max
	}
	
	constructor(H: Int, S: Float, V: Float) {
		this.H = H
		this.S = S
		this.V = V
		
		val max = (V * 255).roundToInt()
		val min = (max * (1 - S)).roundToInt()
		
		when (H) {
			in 300 until 360 -> {
				this.R = max
				this.G = min
				this.B = (-((H - 360) / 60.0) * (max - min) + this.G).roundToInt()
			}
			in 0 until 60 -> {
				this.R = max
				this.B = min
				this.G = ((H / 60.0) * (max - min) + this.B).roundToInt()
			}
			in 60 until 120 -> {
				this.G = max
				this.B = min
				this.R = (-(H / 60.0 - 2) * (max - min) + this.B).roundToInt()
			}
			in 120 until 180 -> {
				this.G = max
				this.R = min
				this.B = ((H / 60.0 - 2) * (max - min) + this.R).roundToInt()
			}
			in 180 until 240 -> {
				this.B = max
				this.R = min
				this.G = (-(H / 60.0 - 4) * (max - min) + this.R).roundToInt()
			}
			in 240 until 300 -> {
				this.B = max
				this.G = min
				this.R = ((H / 60.0 - 4) * (max - min) + this.G).roundToInt()
			}
			else -> error("impossible")
		}
	}
	
	companion object {
		val WOOD = Color(150, 116, 65)
		val STONE = Color(149, 145, 141)
		val COPPER = Color(202, 118, 91)
		val IRON = Color(215, 215, 215)
		val DIAMOND = Color(43, 199, 172)
		val NETHERITE = Color(134, 123, 134)
		
		val STRING = Color(255, 255, 255)
		val VINE = Color(52, 87, 25)
		
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
		
		fun Pair<Color, Color>.gradient(ratio: Float): Color {
			return Color(
				(this.first.H * ratio + this.second.H * (1 - ratio)).roundToInt(),
				this.first.S * ratio + this.second.S * (1 - ratio),
				this.first.V * ratio + this.second.V * (1 - ratio)
			)
		}
	}
	
	fun toHex(): Int {
		return Integer.parseInt(
			Integer.toHexString(1 shl 24 or (R shl 16) or (G shl 8) or B).substring(1), 16
		)
	}
	
	fun darker(delta: Int): Color {
		check(delta >= 0) { "use Color.brighter(${-delta}) instead" }
		
		return Color(
			max(R - delta, 0),
			max(G - delta, 0),
			max(B - delta, 0)
		)
	}
	
	fun brighter(delta: Int): Color {
		check(delta >= 0) { "use Color.darker(${-delta}) instead" }
		
		return Color(
			min(R + delta, 255),
			min(G + delta, 255),
			min(B + delta, 255)
		)
	}
}