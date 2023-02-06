package net.rhseung.reimagined.tool.parts

import net.minecraft.item.Item
import net.rhseung.reimagined.registration.ModItemGroups
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Name.toPathName
import org.objectweb.asm.Handle

class HandlePart constructor (
	override val material: Material
) : IPartItem, Item(Item.Settings()) {
	
	override val includeStats = listOf(
		Stat.DURABILITY,
		Stat.ATTACK_SPEED,
		Stat.MINING_SPEED
	)
	
	override fun getType(): PartType {
		return HandlePart.getType()
	}
	
	override fun getStat(stat: Stat): Float {
		return material.getStat(stat)
	}
	
	companion object {
		fun registerAll(): List<HandlePart> {
			val ret = mutableListOf<HandlePart>()
			
			for (material in Material.values()) {
				if (getType() !in material.canParts) continue
				
				ret.add(ModItems.registerItem(
					"parts/${getType().name.toPathName()}_${material.name.toPathName()}",
					HandlePart(material), if (material != Material.DUMMY) ModItemGroups.PARTS else null
				))
			}
			
			return ret
		}
		
		fun getType(): PartType {
			return PartType.HANDLE
		}
	}
}
