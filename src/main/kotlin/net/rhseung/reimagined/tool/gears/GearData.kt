package net.rhseung.reimagined.tool.gears

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.Part
import net.rhseung.reimagined.utils.Math.roundTo
import net.rhseung.reimagined.utils.Text.pathName
import net.rhseung.reimagined.utils.Utils.getClassName
import kotlin.reflect.KClass

object GearData {
	val NBT_ROOT = "Gear_Data"
	val NBT_ROOT_STATS = "Stats"
	val NBT_ROOT_PARTS = "Parts"
	
	fun getData(
		stack: ItemStack,
		compoundKey: String,
	): NbtCompound {
		val root = stack.getOrCreateSubNbt(NBT_ROOT)
		
		if (!root.contains(compoundKey)) {
			root.put(compoundKey, NbtCompound())
		}
		
		return root.getCompound(compoundKey)
	}
	
	fun putStatIfMissing(
		stack: ItemStack,
		stat: Stat,
		defaultValue: Float? = null,
	) {
		val root = getData(stack, NBT_ROOT_STATS)
		val compoundKey = stat.name.pathName()
		
		if (!root.contains(compoundKey)) {
			root.putFloat(compoundKey, defaultValue ?: stat.defaultValue)
		}
	}
	
	fun putStatIfMissing(
		rootInput: NbtCompound,
		stat: Stat,
		defaultValue: Float? = null,
	) {
		val root = rootInput.getCompound(NBT_ROOT_STATS)
		val compoundKey = stat.name.pathName()
		
		if (!root.contains(compoundKey)) {
			root.putFloat(compoundKey, defaultValue ?: stat.defaultValue)
		}
	}
	
	fun writeStats(
		stack: ItemStack,
		vararg stats: Pair<Stat, Float>,
	) {
		val root = getData(stack, NBT_ROOT_STATS)
		
		for ((stat, value) in stats) {
			val compoundKey = stat.name.pathName()
			root.putFloat(compoundKey, value)
		}
	}
	
	fun putPartIfMissing(
		stack: ItemStack,
		part: KClass<out Part>,
	) {
		val root = getData(stack, NBT_ROOT_PARTS)
		val compoundKey = getClassName(part).pathName()
		
		if (!root.contains(compoundKey)) {
			root.putString(compoundKey, Material.DUMMY.name.pathName())
		}
	}
	
	fun putPartIfMissing(
		rootInput: NbtCompound,
		part: KClass<out Part>,
	) {
		val root = rootInput.getCompound(NBT_ROOT_PARTS)
		val compoundKey = getClassName(part).pathName()
		
		if (!root.contains(compoundKey)) {
			root.putString(compoundKey, Material.DUMMY.name.pathName())
		}
	}
	
	fun writeParts(
		stack: ItemStack,
		vararg parts: Part,
	) {
		val root = getData(stack, NBT_ROOT_PARTS)
		
		for (part in parts) {
			val compoundKey = part.className.pathName()
			root.putString(compoundKey, part.material.name.pathName())
		}
	}
	
	fun reCalculate(
		output: ItemStack,
		includeStats: Set<Stat>,
		vararg parts: Part,
	) {
		for (stat in includeStats) {
			val values = mutableListOf<Float>()
			
			for (part in parts) {
				if (part.includeStats.contains(stat)) {
					values += part.getStat(stat)
				}
			}
			
			writeStats(output, stat to when (stat.calculateType) {
				Stat.CalculateType.SUM      -> values.reduce { acc, cur -> acc + cur }.roundTo(2)
				Stat.CalculateType.MULTIPLY -> values.reduce { acc, cur -> acc * cur }.roundTo(2)
				Stat.CalculateType.AVERAGE  -> if (values.isEmpty()) stat.defaultValue
				                               else (values.reduce { acc, cur -> acc + cur } / values.count()).roundTo(2)
			})
		}
	}
}