package net.rhseung.reimagined.tool.parts.base

import com.ibm.icu.text.MessagePattern.Part
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.enums.PartType

interface IPartItem : ItemConvertible {
	val material: Material
	val includeStats: List<Stat>
	
	// todo: appendTooltip
	// todo: getAttackDamage 같은거
	
	fun getType(): PartType
	
	fun getStat(stat: Stat): Float
}