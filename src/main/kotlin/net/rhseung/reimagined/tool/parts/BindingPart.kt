package net.rhseung.reimagined.tool.parts

import net.minecraft.item.Item
import net.rhseung.reimagined.registration.ModItemGroups
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Name.toPathName

class BindingPart constructor (
	override val material: Material
) : IPartItem, Item(Item.Settings()) {
	
	override val includeStats = listOf(
		Stat.DURABILITY,
		Stat.ENCHANTABILITY
	)
	
	override fun getType(): PartType {
		return BindingPart.getType()
	}
	
	override fun getStat(stat: Stat): Float {
		return material.getStat(stat)
	}
	
	companion object {
		fun registerAll(): List<BindingPart> {
			val ret = mutableListOf<BindingPart>()
			
			for (material in Material.values()) {
				if (getType() !in material.canParts) continue
				
				ret.add(ModItems.registerItem(
					"parts/${getType().name.toPathName()}_${material.name.toPathName()}",
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
