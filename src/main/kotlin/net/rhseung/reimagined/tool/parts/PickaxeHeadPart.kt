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

class PickaxeHeadPart constructor (
	override val material: Material
) : BasicPartItem() {
	
	override val includeStats = listOf(
		Stat.DURABILITY,
		Stat.MINING_SPEED,
		Stat.MINING_TIER,
		Stat.ATTACK_DAMAGE
	)
	
	override fun getType(): PartType {
		return PickaxeHeadPart.getType()
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
		fun registerAll(): List<PickaxeHeadPart> {
			val ret = mutableListOf<PickaxeHeadPart>()
			
			for (material in Material.values()) {
				if (getType() !in material.canParts) continue
				
				ret.add(ModItems.registerItem(
					"parts/${getType().name.pathName()}_${material.name.pathName()}",
					PickaxeHeadPart(material), if (material != Material.DUMMY) ModItemGroups.PARTS else null
				))
			}
			
			return ret
		}
		
		fun getType(): PartType {
			return PartType.PICKAXE_HEAD
		}
	}
}
