package net.rhseung.reimagined.tool.gears.tooltip

import net.minecraft.client.item.TooltipData
import net.minecraft.item.ItemStack
import net.rhseung.reimagined.tool.gears.base.BasicGearItem

class GearTooltipData constructor(
	val gear: BasicGearItem,
	val stack: ItemStack
): TooltipData {}