package net.rhseung.reimagined.tool.parts

import net.minecraft.item.Item
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType

class BindingPart constructor (
	override val material: Material
) : IPartItem, Item(Item.Settings()) {
	
	override fun getType(): PartType {
		return PartType.BINDING
	}
}
