package net.rhseung.reimagined.tool.gears.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.base.BasicPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Math.roundTo
import net.rhseung.reimagined.utils.Text.pathName

object GearData {
	val NBT_ROOT = "Gear_Data"
	val NBT_ROOT_STATS = "Stats"
	val NBT_ROOT_PARTS = "Parts"
	
	fun getData(
		stack: ItemStack,
		compoundKey: String
	): NbtCompound {
		val root = stack.getOrCreateSubNbt(NBT_ROOT)
		
		if (!root.contains(compoundKey)) {
			root.put(compoundKey, NbtCompound())
		}
		
		return root.getCompound(compoundKey)
	}
	
	fun putStatIfMissing(
		stack: ItemStack,
		stat: Stat
	) {
		val root = getData(stack, NBT_ROOT_STATS)
		val compoundKey = stat.name.pathName()
		
		if (!root.contains(compoundKey)) {
			root.putFloat(compoundKey, stat.defaultValue)
		}
	}
	
	fun putStatIfMissing(
		rootInput: NbtCompound,
		stat: Stat
	) {
		val root = rootInput.getCompound(NBT_ROOT_STATS)
		val compoundKey = stat.name.pathName()
		
		if (!root.contains(compoundKey)) {
			root.putFloat(compoundKey, stat.defaultValue)
		}
	}
	
	fun writeStats(
		stack: ItemStack,
		vararg stats: Pair<Stat, Float>
	) {
		val root = getData(stack, NBT_ROOT_STATS)
		
		for ((stat, value) in stats) {
			val compoundKey = stat.name.pathName()
			root.putFloat(compoundKey, value)
		}
	}
	
	fun putPartIfMissing(
		stack: ItemStack,
		partType: PartType
	) {
		val root = getData(stack, NBT_ROOT_PARTS)
		val compoundKey = partType.name.pathName()
		
		if (!root.contains(compoundKey)) {
			root.putString(compoundKey, Material.DUMMY.name.pathName())
		}
	}
	
	fun putPartIfMissing(
		rootInput: NbtCompound,
		partType: PartType
	) {
		val root = rootInput.getCompound(NBT_ROOT_PARTS)
		val compoundKey = partType.name.pathName()
		
		if (!root.contains(compoundKey)) {
			root.putString(compoundKey, Material.DUMMY.name.pathName())
		}
	}
	
	fun writeParts(
		stack: ItemStack,
		vararg parts: BasicPartItem
	) {
		val root = getData(stack, NBT_ROOT_PARTS)
		
		for (part in parts) {
			val compoundKey = part.getType()!!.name.pathName()
			root.putString(compoundKey, part.material.name.pathName())
		}
	}
	
	fun recalculate(
		output: ItemStack,
		includeStats: Set<Stat>,
		vararg parts: BasicPartItem
	) {
		for (stat in includeStats) {
			var count = 0
			var value = 0.0F
			
			for (part in parts) {
				if (part.includeStats.contains(stat)) {
					count++
					value += part.getStat(stat)
				}
			}
			
			writeStats(output, stat to (value / count).roundTo(2))
		}
	}
}