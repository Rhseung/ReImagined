package net.rhseung.reimagined.tool.gears.enums

import net.minecraft.block.Block
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.rhseung.reimagined.registration.ModBlockTags
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.*
import net.rhseung.reimagined.tool.gears.base.BasicGearItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Utils.append

enum class GearType constructor(
	val baseAttackDamage: Double,
	val baseAttackSpeed: Double,
	vararg moreParts: Pair<PartType, Int>,
	val effectiveBlocks: TagKey<Block>? = null,
	var includeStats: Set<Stat> = setOf(),
	var superType: GearBasicType = GearBasicType.GEAR
) {
	@Using(PickaxeGear::class)
	@Super(GearBasicType.MINING_TOOL)
	PICKAXE(
		1.0, 1.2,
		PartType.PICKAXE_HEAD to 1,
		effectiveBlocks = BlockTags.PICKAXE_MINEABLE
	),
	
	@Using(AxeGear::class)
	@Super(GearBasicType.MINING_TOOL)
	AXE(
		5.0, 1.0,
		PartType.AXE_HEAD to 1,
		effectiveBlocks = BlockTags.AXE_MINEABLE
	),
	
	@Using(ShovelGear::class)
	@Super(GearBasicType.MINING_TOOL)
	SHOVEL(
		1.5, 1.0,
		PartType.SHOVEL_HEAD to 1,
		effectiveBlocks = BlockTags.SHOVEL_MINEABLE
	),
	
	@Using(HoeGear::class)
	@Super(GearBasicType.MINING_TOOL)
	HOE(
		1.0, 1.4,
		PartType.HOE_HEAD to 1,
		effectiveBlocks = BlockTags.HOE_MINEABLE
	),
	
	@Using(SwordGear::class)
	@Super(GearBasicType.MELEE_WEAPON)
	SWORD(
		3.0, 1.6,
		PartType.SWORD_HEAD to 1,
		effectiveBlocks = ModBlockTags.SWORD_MINEABLE
	);
	
	lateinit var includeParts: List<PartType>
	lateinit var instance: BasicGearItem
	
	init {
		this.declaringJavaClass.getField(name).annotations.forEach { annotation ->
			when (annotation) {
				is Super -> {
					superType = annotation.type
					includeStats = superType.includeStats
					includeParts = superType.includeParts
				}
				is Using ->
					instance = annotation.value.java.getDeclaredConstructor().newInstance()
			}
		}
		
		for ((partType, index) in moreParts) {
			includeParts = includeParts.append(index, partType)
		}
		
		instance.type = this
	}
	
	companion object {
		fun getValues(): List<GearType> = GearType.values().toList()
	}
}