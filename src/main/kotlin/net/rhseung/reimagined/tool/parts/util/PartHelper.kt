package net.rhseung.reimagined.tool.parts.util

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Material.Companion.getColor
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.util.GearHelper

object PartHelper {
	fun appendTooltip(
		stack: ItemStack,
		world: World?,
		tooltip: MutableList<Text>,
		context: TooltipContext,
		includeStats: Set<Stat>,
		material: Material
	) {
		for (stat in includeStats) {
			tooltip.add(stat.getDisplayTextUsingValue(stack, material.getStat(stat)))
		}
	}
}