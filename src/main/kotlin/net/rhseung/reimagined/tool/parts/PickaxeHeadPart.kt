package net.rhseung.reimagined.tool.parts

import net.minecraft.item.Item
import net.rhseung.reimagined.registration.ModItemGroups
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.PickaxeGear
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Name.toPathName

class PickaxeHeadPart constructor (
	override val material: Material
) : IPartItem, Item(Item.Settings()) {
	
	override val includeStats = listOf(
		Stat.DURABILITY,
		Stat.MINING_TIER,
		Stat.ATTACK_DAMAGE
	)
	
	override fun getType(): PartType {
		return PickaxeHeadPart.getType()
	}
	
	override fun getStat(stat: Stat): Float {
		return material.getStat(stat)
	}
	
	companion object {
		fun registerAll(): List<PickaxeHeadPart> {
			val ret = mutableListOf<PickaxeHeadPart>()
			
			for (material in Material.values()) {
				if (getType() !in material.canParts) continue
				
				ret.add(ModItems.registerItem(
					"parts/${getType().name.toPathName()}_${material.name.toPathName()}",
					PickaxeHeadPart(material), ModItemGroups.PARTS
				))
			}
			
			return ret
		}
		
		fun getType(): PartType {
			return PartType.PICKAXE_HEAD
		}
	}
}
