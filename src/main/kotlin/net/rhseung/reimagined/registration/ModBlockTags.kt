package net.rhseung.reimagined.registration

import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined

object ModBlockTags {
	val NEEDS_STONE_TOOL: TagKey<Block> =
		TagKey.of(RegistryKeys.BLOCK, Identifier.of(ReImagined.MOD_ID, "needs_stone_tool"))
	val NEEDS_COPPER_TOOL: TagKey<Block> =
		TagKey.of(RegistryKeys.BLOCK, Identifier.of(ReImagined.MOD_ID, "needs_copper_tool"))
	val NEEDS_IRON_TOOL: TagKey<Block> =
		TagKey.of(RegistryKeys.BLOCK, Identifier.of(ReImagined.MOD_ID, "needs_iron_tool"))
	val NEEDS_DIAMOND_TOOL: TagKey<Block> = // todofar: diamond -> steel
		TagKey.of(RegistryKeys.BLOCK, Identifier.of(ReImagined.MOD_ID, "needs_diamond_tool"))
	val NEEDS_NETHERITE_TOOL: TagKey<Block> =
		TagKey.of(RegistryKeys.BLOCK, Identifier.of(ReImagined.MOD_ID, "needs_netherite_tool"))
	
	val SWORD_MINEABLE: TagKey<Block> =
		TagKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "mineable/sword"))
	
	fun getTag(tier: Int) = when (tier) {
		1 -> NEEDS_STONE_TOOL
		2 -> NEEDS_COPPER_TOOL
		3 -> NEEDS_IRON_TOOL
		4 -> NEEDS_DIAMOND_TOOL   // todofar: NEEDS_STEEL_TOOL로 바꿔야됨
		5 -> NEEDS_NETHERITE_TOOL
		else -> error("1보다 작거나 5보다 큰 mining level에 해당하는 mineable tag는 존재하지 않습니다.")
		// todofar: MAX_TIER까지 확장
	}
}