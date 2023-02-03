package net.rhseung.reimagined.utils

import net.minecraft.text.MutableText
import net.minecraft.text.Text

object Tooltip {
	fun singleTextf(
		text: String,
		color: Color
	): MutableText {
		val t = Text.literal(text)
		t.style = t.style.withColor(color.toHex())
		return t
	}
	
	fun textf(
		text: String,
		vararg colors: Color
	): MutableText {
		var newText = text
		newText = newText.replace("\\{(.*?)}".toRegex(), "\n#$1\n")
		
		var arr = newText.split("\n")
		arr = arr.filter { e -> e != "" }
		
		var ret = Text.literal("")
		var idx = 0
		for (e in arr) {
			ret = if (e.startsWith('#')) {
				ret.append(singleTextf(e.substring(1), if (colors.count() > idx) colors[idx++] else Color.WHITE))
			} else {
				ret.append(e)
			}
		}
		
		return ret
	}
	
	fun point(
		number: Float,
		n: Int = 2
	) = String.format("%.${n}f", number)
	
	fun button(
		text: String,
		isPressed: Boolean
	): MutableText {
		return textf(
			"{[}{${text}}{]} {+}",
			if (isPressed) Color.DARK_GRAY else Color.GRAY,
			if (isPressed) Color.WHITE else Color.DARK_GRAY,
			if (isPressed) Color.DARK_GRAY else Color.GRAY,
			if (isPressed) Color.WHITE else Color.DARK_GRAY
		)
	}
}