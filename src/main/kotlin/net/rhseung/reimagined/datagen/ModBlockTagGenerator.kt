package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.*
import net.minecraft.registry.tag.BlockTags
import net.rhseung.reimagined.registration.ModBlockTags
import java.util.concurrent.CompletableFuture

class ModBlockTagGenerator(
	output: FabricDataOutput,
	registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?,
) : FabricTagProvider<Block>(output, RegistryKeys.BLOCK, registriesFuture) {
	
	override fun configure(arg: RegistryWrapper.WrapperLookup?) {
		getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(
			Blocks.COPPER_BLOCK,
			Blocks.RAW_COPPER_BLOCK,
			Blocks.COPPER_ORE,
			Blocks.DEEPSLATE_COPPER_ORE,
			Blocks.CUT_COPPER_SLAB,
			Blocks.CUT_COPPER_STAIRS,
			Blocks.CUT_COPPER,
			Blocks.WEATHERED_COPPER,
			Blocks.WEATHERED_CUT_COPPER,
			Blocks.WEATHERED_CUT_COPPER_STAIRS,
			Blocks.WEATHERED_CUT_COPPER_SLAB,
			Blocks.OXIDIZED_COPPER,
			Blocks.OXIDIZED_CUT_COPPER,
			Blocks.OXIDIZED_CUT_COPPER_STAIRS,
			Blocks.OXIDIZED_CUT_COPPER_SLAB,
			Blocks.EXPOSED_COPPER,
			Blocks.EXPOSED_CUT_COPPER_SLAB,
			Blocks.EXPOSED_CUT_COPPER_STAIRS,
			Blocks.EXPOSED_CUT_COPPER,
			Blocks.WAXED_COPPER_BLOCK,
			Blocks.WAXED_CUT_COPPER,
			Blocks.WAXED_CUT_COPPER_SLAB,
			Blocks.WAXED_CUT_COPPER_STAIRS,
			Blocks.WAXED_WEATHERED_COPPER,
			Blocks.WAXED_WEATHERED_CUT_COPPER,
			Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
			Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,
			Blocks.WAXED_EXPOSED_COPPER,
			Blocks.WAXED_EXPOSED_CUT_COPPER,
			Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
			Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
			Blocks.WAXED_OXIDIZED_COPPER,
			Blocks.WAXED_OXIDIZED_CUT_COPPER,
			Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB,
			Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
			Blocks.LIGHTNING_ROD
		)
		
		getOrCreateTagBuilder(ModBlockTags.NEEDS_COPPER_TOOL).add(
			Blocks.IRON_BLOCK,
			Blocks.RAW_IRON_BLOCK,
			Blocks.IRON_ORE,
			Blocks.DEEPSLATE_IRON_ORE,
			Blocks.LAPIS_BLOCK,
			Blocks.LAPIS_ORE,
			Blocks.DEEPSLATE_LAPIS_ORE
		)
		
		getOrCreateTagBuilder(ModBlockTags.NEEDS_IRON_TOOL).forceAddTag(BlockTags.NEEDS_IRON_TOOL)
		
		getOrCreateTagBuilder(ModBlockTags.NEEDS_DIAMOND_TOOL).forceAddTag(BlockTags.NEEDS_DIAMOND_TOOL)

//		getOrCreateTagBuilder(ModBlockTags.NEEDS_NETHERITE_TOOL).add(ModBlocks.NETHERITE_BLOCK)
		
		getOrCreateTagBuilder(ModBlockTags.SWORD_MINEABLE).add(Blocks.COBWEB).forceAddTag(BlockTags.LEAVES)
	}
}