package net.rhseung.reimagined.tool.parts.base

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.enums.PartType

open class BasicPartItem : Item(Item.Settings()) {
	open val material = Material.DUMMY
	open val includeStats = listOf<Stat>()
	
	open fun getType(): PartType? {
		return null
	}
	
	fun getStat(stat: Stat): Float {
		return material.getStat(stat)
	}
	
	override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
		super.appendTooltip(stack, world, tooltip, context)
	}
}