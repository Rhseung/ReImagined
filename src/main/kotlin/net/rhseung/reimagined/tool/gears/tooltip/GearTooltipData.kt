package net.rhseung.reimagined.tool.gears.tooltip

import net.minecraft.client.item.TooltipData
import net.minecraft.item.ItemStack
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.gears.base.IMiningGearItem

class GearTooltipData constructor(
	val gear: IGearItem,
	val stack: ItemStack
): TooltipData {}