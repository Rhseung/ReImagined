package net.rhseung.reimagined.tool.parts.enums

import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.parts.BindingPart
import net.rhseung.reimagined.tool.parts.HandlePart
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart
import net.rhseung.reimagined.tool.parts.base.BasicPartItem

enum class PartType constructor(
	val partClass: Class<out BasicPartItem>
) {
	PICKAXE_HEAD(PickaxeHeadPart::class.java),
	BINDING(BindingPart::class.java),
	HANDLE(HandlePart::class.java);
	
	companion object {
		fun HEAD(gearType: GearType): PartType = gearType.includeParts[1]
//			when (gearType) {
//			GearType.PICKAXE -> PICKAXE_HEAD
//			else -> error("Unsupported gear type: $gearType")
//		}
	}
}