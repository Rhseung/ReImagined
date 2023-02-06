package net.rhseung.reimagined.registration

import net.minecraft.block.Block
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined

object ModBlockTags {
	val NEEDS_COPPER_TOOL: TagKey<Block> =
		TagKey.of(RegistryKeys.BLOCK, Identifier.of(ReImagined.MOD_ID, "needs_copper_tool"))
	val NEEDS_STEEL_TOOL: TagKey<Block> =
		TagKey.of(RegistryKeys.BLOCK, Identifier.of(ReImagined.MOD_ID, "needs_steel_tool"))
	val NEEDS_NETHERITE_TOOL: TagKey<Block> =
		TagKey.of(RegistryKeys.BLOCK, Identifier.of(ReImagined.MOD_ID, "needs_netherite_tool"))
	
	fun getTag(stat: Int) = when (stat) {
		1 -> BlockTags.NEEDS_STONE_TOOL
		2 -> NEEDS_COPPER_TOOL
		3 -> BlockTags.NEEDS_IRON_TOOL
		4 -> BlockTags.NEEDS_DIAMOND_TOOL   // todofar: NEEDS_STEEL_TOOL로 바꿔야됨
		5 -> NEEDS_NETHERITE_TOOL
		else -> error("1보다 작거나 5보다 큰 mining level은 존재하지 않습니다.")
		// todofar: MAX_TIER까지 확장
	}
}