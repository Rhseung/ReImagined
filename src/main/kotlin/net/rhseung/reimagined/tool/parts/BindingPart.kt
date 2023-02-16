package net.rhseung.reimagined.tool.parts

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import net.rhseung.reimagined.registration.ModItemGroups
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.base.BasicPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.tool.parts.util.PartHelper
import net.rhseung.reimagined.utils.Text.pathName

class BindingPart constructor (
	override val material: Material
) : BasicPartItem() {
	
	override val includeStats = listOf(
		Stat.DURABILITY,
		Stat.ENCHANTABILITY
	)
	
	override fun getType(): PartType {
		return BindingPart.getType()
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
	
	companion object {
		fun registerAll(): List<BindingPart> {
			val ret = mutableListOf<BindingPart>()
			
			for (material in Material.values()) {
				if (getType() !in material.canParts) continue
				
				ret.add(ModItems.registerItem(
					"parts/${getType().name.pathName()}_${material.name.pathName()}",
					BindingPart(material), if (material != Material.DUMMY) ModItemGroups.PARTS else null
				))
			}
			
			return ret
		}
		
		fun getType(): PartType {
			return PartType.BINDING
		}
	}
}
