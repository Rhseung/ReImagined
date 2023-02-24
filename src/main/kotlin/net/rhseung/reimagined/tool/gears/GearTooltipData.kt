package net.rhseung.reimagined.tool.gears

import net.minecraft.client.item.TooltipData
import net.minecraft.item.ItemStack
import net.rhseung.reimagined.tool.gears.definition.Gear
import net.rhseung.reimagined.tool.gears.GearStack

class GearTooltipData constructor(
	gear: Gear,
	stack: ItemStack
): TooltipData {
	val gearStack = GearStack(stack, gear)
}