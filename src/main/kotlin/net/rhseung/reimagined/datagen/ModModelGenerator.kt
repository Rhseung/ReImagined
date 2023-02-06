package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.*
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.utils.Name.toPathName
import java.util.*

class ModModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
	override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
	}
	
	override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
		ModItems.PARTS.values.forEach { it -> it.forEach { itemModelGenerator.generatePart(it) } }
		ModItems.GEARS.forEach { itemModelGenerator.generateGear(it) }
	}
	
	private fun <T : Item> ItemModelGenerator.generate(item: T, vararg texturePath: String) {
		val id = Registries.ITEM.getId(item).withPrefixedPath("item/")
		val model = Model(Optional.of(Identifier("minecraft", "item/generated")), Optional.empty())
		val textureMap = TextureMap()
		
		texturePath.forEachIndexed { index, path -> textureMap.register(
			TextureKey.of("layer$index"),
			Identifier(ReImagined.MOD_ID, "item/${path}")
		) }
		
		model.upload(id, textureMap, this.writer)
	}
	
	private fun ItemModelGenerator.generatePart(part: IPartItem) {
		if (part !is Item) return
		
		this.generate(part, "parts/${part.getType().name.toPathName()}")
	}
	
	private fun ItemModelGenerator.generateGear(gear: IGearItem) {
		if (gear !is Item) return
		
		val texturePaths = mutableListOf<String>()
		gear.includeParts.forEach { partType ->
			// todo: crude texture
			texturePaths.add("gear/${gear.getType().name.toPathName()}/${partType.name.split("_").last().toPathName()}")
		}
		
		this.generate(gear, *texturePaths.toTypedArray())
		
		// todo: override
	}
}