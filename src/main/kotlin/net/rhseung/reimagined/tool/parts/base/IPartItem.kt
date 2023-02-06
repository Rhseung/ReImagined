package net.rhseung.reimagined.tool.parts.base

import com.ibm.icu.text.MessagePattern.Part
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.enums.PartType

interface IPartItem : ItemConvertible {
	val material: Material
	val includeStats: List<Stat>
	
	fun getType(): PartType
	
	fun getStat(stat: Stat): Float
	
	fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext)
}