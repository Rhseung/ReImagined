package net.rhseung.reimagined.tool.parts.enums

import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.parts.BindingPart
import net.rhseung.reimagined.tool.parts.HandlePart
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart
import net.rhseung.reimagined.tool.parts.base.BasicPartItem

enum class PartType constructor(
	val includeStats: Set<Stat>
) {
	PICKAXE_HEAD(
		includeStats = setOf(
			Stat.DURABILITY,
			Stat.MINING_SPEED,
			Stat.MINING_TIER,
			Stat.ATTACK_DAMAGE
		)
	),
	BINDING(
		includeStats = setOf(
			Stat.DURABILITY,
			Stat.ENCHANTABILITY
		)
	),
	HANDLE(
		includeStats = setOf(
			Stat.DURABILITY,
			Stat.ATTACK_SPEED,
			Stat.MINING_SPEED
		)
	);
	
	fun instance(material: Material) = when (this) {
		PICKAXE_HEAD -> PickaxeHeadPart(material)
		BINDING -> BindingPart(material)
		HANDLE -> HandlePart(material)
	}
	
	companion object {
		fun HEAD(gearType: GearType): PartType = gearType.includeParts[1]
	}
}