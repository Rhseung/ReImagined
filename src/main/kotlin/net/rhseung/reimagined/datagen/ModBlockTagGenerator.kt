package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.*
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.rhseung.reimagined.registration.ModBlockTags
import java.util.concurrent.CompletableFuture

class ModBlockTagGenerator(
	output: FabricDataOutput,
	registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?
) : FabricTagProvider<Block>(output, RegistryKeys.BLOCK, registriesFuture) {
	
	override fun configure(arg: RegistryWrapper.WrapperLookup?) {
		// bug: why addTag is not running?
		getOrCreateTagBuilder(ModBlockTags.NEEDS_COPPER_TOOL).add(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE)
//		getOrCreateTagBuilder(ModBlockTags.NEEDS_NETHERITE_TOOL).add(Blocks.NETHERITE_BLOCK)
		
		// note: block 추가하면 PICKAXE_MINABLE에 넣고, 각각의 MiningLevelTag에도 넣기
	}
}