package net.rhseung.reimagined.utils

import net.minecraft.enchantment.Enchantment
import net.minecraft.text.Text

object Text {
	fun String.displayName(): String {
		return this.lowercase().split("_")
			.joinToString(" ") { it.replaceFirstChar { it.titlecase() } }
	}
	
	fun String.pathName(): String {
		return this.replace(" ", "_").lowercase()
	}
	
	fun Double.removeZero(): String {
		return if (this.toInt().toDouble() == this) this.toInt().toString()
		else this.toString()
	}
	
	fun Float.removeZero(): String {
		return if (this.toInt().toFloat() == this) this.toInt().toString()
		else this.toString()
	}
	
	fun getEnchantmentFullName(
		enchantment: Enchantment,
		level: Int
	): String {
		val mutableText = Text.translatable(enchantment.translationKey)
		
		if (level != 1 || enchantment.maxLevel != 1)
			mutableText.append(" ").append(Text.translatable("enchantment.level.$level"))
		
		return mutableText.string
	}
	
	fun Float.round(
		n: Int = 2
	) = String.format("%.${n}f", this)
	
	fun Double.round(
		n: Int = 2
	) = String.format("%.${n}f", this)
	
	fun String.toText(): Text = Text.literal(this)
}