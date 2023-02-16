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
	override val type = Companion.type
	override val includeStats = type.includeStats
	
	companion object {
		val type = PartType.BINDING
	}
}
