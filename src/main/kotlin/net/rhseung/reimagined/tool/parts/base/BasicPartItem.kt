package net.rhseung.reimagined.tool.parts.base

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import net.rhseung.reimagined.registration.ModItemGroups
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.tool.parts.util.PartHelper
import net.rhseung.reimagined.utils.Text.pathName

open class BasicPartItem(open val material: Material) : Item(Settings()) {
	open lateinit var type: PartType
	open val includeStats by lazy { type.includeStats }
	
	fun getStat(stat: Stat): Float {
		return material.getStat(stat)
	}
	
	override fun isEnchantable(stack: ItemStack?): Boolean {
		return false
	}
	
	override fun appendTooltip(
		stack: ItemStack,
		world: World?,
		tooltip: MutableList<Text>,
		context: TooltipContext
	) {
		PartHelper.appendTooltip(stack, world, tooltip, context, includeStats, material)
	}
}