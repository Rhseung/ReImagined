package net.rhseung.reimagined.tool.gears

import net.rhseung.reimagined.tool.gears.base.BasicMiningGearItem
import net.rhseung.reimagined.tool.gears.enums.GearType

class AxeGear
: BasicMiningGearItem() {
	override val type = GearType.AXE
	override val includeStats = type.includeStats
	override val includeParts= type.includeParts
	override val effectiveBlocks = type.effectiveBlocks
	
	// todo: 우클릭 액션
}