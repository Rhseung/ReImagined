package net.rhseung.reimagined.tool.gears.base

import net.minecraft.item.ItemStack
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.parts.enums.PartType

interface IGearItem {
	val includeStats: List<Stat>
	val includeParts: List<PartType>
	
	fun getType(): GearType
	
	fun getCurrentDurability(stack: ItemStack): Int
	fun getMaxDurability(stack: ItemStack): Int
	fun getRatioDurability(stack: ItemStack): Float
	fun broken(stack: ItemStack): Boolean
	
	// todo: gear로서 필수적인 GearHelper 함수들 선언해놓기
	
	fun getTier(stack: ItemStack): Int
}