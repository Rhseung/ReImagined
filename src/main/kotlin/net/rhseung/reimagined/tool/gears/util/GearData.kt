package net.rhseung.reimagined.tool.gears.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Name.toPathName

object GearData {
	val NBT_ROOT = "Gear_Data"
	val NBT_ROOT_STATS = "Stats"
	val NBT_ROOT_CONSTRUCTION = "Construction"
	
	val DUMMY_ELEMENT = 0
	
	fun getData(
		stack: ItemStack,
		compoundKey: String
	): NbtCompound {
		val root = stack.getOrCreateSubNbt(NBT_ROOT)
		
		if (!root.contains(compoundKey))
			root.put(compoundKey, null)
		
		return root.getCompound(compoundKey)
	}
	
	fun putStatIfMissing(
		stack: ItemStack,
		stat: Stat
	) {
		val root = getData(stack, NBT_ROOT_STATS)
		val compoundKey = stat.name.toPathName()
		
		if (!root.contains(compoundKey)) {
			when {
				stat.isInt -> root.putInt(compoundKey, stat.defaultValue as Int)
				!stat.isInt -> root.putFloat(compoundKey, stat.defaultValue as Float)
			}
		}
	}
	
	fun putConstructionIfMissing(
		stack: ItemStack,
		partType: PartType
	) {
		val root = getData(stack, NBT_ROOT_CONSTRUCTION)
		val compoundKey = partType.name.toPathName()
		
		if (!root.contains(compoundKey)) {
			// todo: Material.Wood 말고 dummy material이 필요해 보이는데...
			root.putString(compoundKey, Material.Wood.name.toPathName())    // fixme: wood
		}
	}
}