package net.rhseung.reimagined.utils

import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined.modID

enum class Icon constructor(
	vararg variants: String
) {
	PROTECTION,
	TOUGHNESS,
	KNOCKBACK_RESISTANCE,
	ATTACK_DAMAGE,
	ATTACK_SPEED,
	MINING_LEVEL,
	MINING_SPEED,
	DURABILITY,
	ENCHANTMENT,
	HUNGER("_1"),
	SATURATION("_1", "_2", "_3");
	
	private var variants: List<String> = listOf("") + variants.asList()
	
	fun path(index: Int = 0): Identifier {
		return modID("textures/icon/${name.lowercase() + variants[index]}.png")
	}
}