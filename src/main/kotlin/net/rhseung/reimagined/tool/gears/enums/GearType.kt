package net.rhseung.reimagined.tool.gears.enums

import net.minecraft.block.Block
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.AxeGear
import net.rhseung.reimagined.tool.gears.HoeGear
import net.rhseung.reimagined.tool.gears.PickaxeGear
import net.rhseung.reimagined.tool.gears.ShovelGear
import net.rhseung.reimagined.tool.gears.base.BasicGearItem
import net.rhseung.reimagined.tool.gears.base.BasicMiningGearItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Utils.append
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

enum class GearType constructor(
	private val gearClass: KClass<out BasicGearItem>,
	val baseAttackDamage: Double,
	val baseAttackSpeed: Double,
	val includeStats: Set<Stat>,
	val includeParts: List<PartType>,
	val effectiveBlocks: TagKey<Block>? = null
) {
	GEAR(BasicGearItem::class,
		0.0, 0.0,
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
	MINING_GEAR(BasicMiningGearItem::class,
		0.0, 0.0,
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
	
	PICKAXE(PickaxeGear::class,
		1.0, 1.2,
		includeStats = MINING_GEAR.includeStats,
		includeParts = MINING_GEAR.includeParts.append(1, PartType.PICKAXE_HEAD),
		effectiveBlocks = BlockTags.PICKAXE_MINEABLE
	),
	AXE(AxeGear::class,
		4.0, 1.0,
		includeStats = MINING_GEAR.includeStats,
		includeParts = MINING_GEAR.includeParts.append(1, PartType.AXE_HEAD),
		effectiveBlocks = BlockTags.AXE_MINEABLE
	),
	SHOVEL(ShovelGear::class,
		1.5, 1.0,
		includeStats = MINING_GEAR.includeStats,
		includeParts = MINING_GEAR.includeParts.append(1, PartType.SHOVEL_HEAD),
		effectiveBlocks = BlockTags.SHOVEL_MINEABLE
	),
	HOE(HoeGear::class,
		1.0, 1.4,
		includeStats = MINING_GEAR.includeStats,
		includeParts = MINING_GEAR.includeParts.append(1, PartType.HOE_HEAD),
		effectiveBlocks = BlockTags.HOE_MINEABLE
	);
	
//	val instance = gearClass.primaryConstructor!!.call()
	fun instance() = when (this) {
		PICKAXE -> PickaxeGear()
		AXE -> AxeGear()
		SHOVEL -> ShovelGear()
		HOE -> HoeGear()
		else -> error("GEAR랑 MINING_GEAR는 instance를 받지 않습니다.")
	}
	
	companion object {
		fun getValues(): List<GearType> = GearType.values().filter { it != GEAR && it != MINING_GEAR }
	}
}