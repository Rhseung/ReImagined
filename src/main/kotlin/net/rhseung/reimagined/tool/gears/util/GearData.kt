package net.rhseung.reimagined.tool.gears.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Name.toPathName

object GearData {
	val NBT_ROOT = "Gear_Data"
	val NBT_ROOT_STATS = "Stats"
	val NBT_ROOT_PARTS = "Parts"
	
	val DUMMY_ELEMENT = 0
	
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
		val compoundKey = stat.name.toPathName()
		
		if (!root.contains(compoundKey)) {
			root.putFloat(compoundKey, stat.defaultValue)
		}
	}
	
	fun putStatIfMissing(
		root: NbtCompound,
		stat: Stat
	) {
		val root = root.getCompound(NBT_ROOT_STATS)
		val compoundKey = stat.name.toPathName()
		
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
			val compoundKey = stat.name.toPathName()
			root.putFloat(compoundKey, value)
		}
	}
	
	fun putPartIfMissing(
		stack: ItemStack,
		partType: PartType
	) {
		val root = getData(stack, NBT_ROOT_PARTS)
		val compoundKey = partType.name.toPathName()
		
		if (!root.contains(compoundKey)) {
			root.putString(compoundKey, Material.DUMMY.name.toPathName())
		}
	}
	
	fun putPartIfMissing(
		root: NbtCompound,
		partType: PartType
	) {
		val root = root.getCompound(NBT_ROOT_PARTS)
		val compoundKey = partType.name.toPathName()
		
		if (!root.contains(compoundKey)) {
			root.putString(compoundKey, Material.DUMMY.name.toPathName())
		}
	}
	
	fun writeParts(
		stack: ItemStack,
		vararg parts: IPartItem
	) {
		val root = getData(stack, NBT_ROOT_PARTS)
		
		for (part in parts) {
			val compoundKey = part.getType().name.toPathName()
			root.putString(compoundKey, part.material.name.toPathName())
		}
	}
}