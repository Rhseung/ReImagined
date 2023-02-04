package net.rhseung.reimagined.tool.parts

import net.minecraft.item.Item
import net.rhseung.reimagined.registration.ModItemGroups
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.gears.PickaxeGear
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Name.toPathName

class PickaxeHeadPart constructor (
	override val material: Material
) : IPartItem, Item(Item.Settings()) {
	
	override fun getType(): PartType {
		return PickaxeHeadPart.getType()
	}
	
	companion object {
		fun registerAll() {
			for (material in Material.values()) {
				if (getType() !in material.canParts) continue
				
				ModItems.registerItem(
					"parts/${getType().name.toPathName()}_${material.name.toPathName()}",
					PickaxeHeadPart(material), ModItemGroups.PARTS
				)
			}
		}
		
		fun getType(): PartType {
			return PartType.PICKAXE_HEAD
		}
	}
}
