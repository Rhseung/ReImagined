package net.rhseung.reimagined.registration

import net.minecraft.block.Block
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
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
}