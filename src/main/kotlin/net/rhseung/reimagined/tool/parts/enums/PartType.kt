package net.rhseung.reimagined.tool.parts.enums

import net.minecraft.item.Item
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.parts.BindingPart
import net.rhseung.reimagined.tool.parts.HandlePart
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart
import net.rhseung.reimagined.tool.parts.base.IPartItem

enum class PartType {
	PICKAXE_HEAD,
	BINDING,
	HANDLE;
	
	fun cast(part: Item): IPartItem {
		return when (this) {
			PICKAXE_HEAD -> part as PickaxeHeadPart
            BINDING -> part as BindingPart
			HANDLE -> part as HandlePart
		}
	}
	
	companion object {
		fun HEAD(gearType: GearType): PartType = when (gearType) {
			GearType.PICKAXE -> PICKAXE_HEAD
		}
	}
}