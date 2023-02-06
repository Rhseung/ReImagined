package net.rhseung.reimagined.tool.gears.base

import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.enums.PartType

interface IMiningGearItem : IGearItem {
	override val includeStats: List<Stat>
		get() = listOf<Stat>(
			Stat.ATTACK_DAMAGE,
			Stat.ATTACK_SPEED,
			Stat.MINING_SPEED,
			Stat.MINING_TIER,
			Stat.DURABILITY,
			Stat.ENCHANTABILITY
		)
	
	override val includeParts: List<PartType>
		get() = listOf<PartType>(
			PartType.HANDLE,
			// PartType.HEAD,
			PartType.BINDING
		)
}