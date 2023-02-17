package net.rhseung.reimagined.tool.gears

import net.rhseung.reimagined.tool.gears.base.BasicMiningGearItem
import net.rhseung.reimagined.tool.gears.enums.GearType

class ShovelGear
: BasicMiningGearItem() {
	override val type = GearType.SHOVEL
	override val includeStats = type.includeStats
	override val includeParts= type.includeParts
	override val effectiveBlocks = type.effectiveBlocks
	
	// todo: 우클릭 액션
}