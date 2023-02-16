package net.rhseung.reimagined.tool.gears.enums

import net.minecraft.block.Block
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.PickaxeGear
import net.rhseung.reimagined.tool.gears.base.BasicGearItem
import net.rhseung.reimagined.tool.gears.base.BasicMiningGearItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Utils.append

enum class GearType constructor(
	val baseAttackSpeed: Double,
	val gearClass: Class<out BasicGearItem>,
	val includeStats: Set<Stat>,
	val includeParts: List<PartType>,
	val effectiveBlocks: TagKey<Block>? = null,
) {
	GEAR(
		0.0, BasicGearItem::class.java,
		includeStats = setOf(
			Stat.ATTACK_DAMAGE,
			Stat.ATTACK_SPEED,
			Stat.DURABILITY
		),
		includeParts = listOf(
			PartType.HANDLE,
			PartType.BINDING
		),
		effectiveBlocks = null
	),
	MINING_GEAR(
		0.0, BasicMiningGearItem::class.java,
		includeStats = setOf(
			Stat.ATTACK_DAMAGE,
			Stat.ATTACK_SPEED,
			Stat.MINING_SPEED,
			Stat.MINING_TIER,
			Stat.DURABILITY,
			Stat.ENCHANTABILITY
		),
		includeParts = listOf(
			PartType.HANDLE,
			PartType.BINDING
		),
		effectiveBlocks = null
	),
	
	PICKAXE(
		1.2, PickaxeGear::class.java,
		includeStats = MINING_GEAR.includeStats,
		includeParts = MINING_GEAR.includeParts.append(1, PartType.PICKAXE_HEAD),
		effectiveBlocks = BlockTags.PICKAXE_MINEABLE
	);
	
	companion object {
		fun getValues(): List<GearType> = GearType.values().filter { it != GEAR && it != MINING_GEAR }
	}
}