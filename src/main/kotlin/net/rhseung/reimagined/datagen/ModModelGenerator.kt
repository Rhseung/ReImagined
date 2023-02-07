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
import net.rhseung.reimagined.utils.Name.pathName
import net.rhseung.reimagined.utils.Texture

class ModModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
	override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
	}
	
	override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
		ModItems.PARTS.values.forEach { it -> it.forEach { itemModelGenerator.generatePart(it) } }
		ModItems.GEARS.forEach { itemModelGenerator.generateGear(it) }
	}
	
	private fun <T : Item> ItemModelGenerator.generate(
		item: T,
		vararg texturePath: String,
		makeBrokenTexture: Boolean = false,
		overrides: List<Texture.OverrideTexture> = listOf(),
	) {
		val id = Registries.ITEM.getId(item).withPrefixedPath("item/")
		val textureMap = mutableMapOf<TextureKey, Identifier>()
		
		texturePath.forEachIndexed { index, path ->
			textureMap[TextureKey.of("layer$index")] = Identifier(ReImagined.MOD_ID, "item/$path")
		}
		
		// 파일 이름 정하기
		val wholeOverrides = (if (!makeBrokenTexture) overrides else overrides + Texture.OverrideTexture(
			"broken",
			1
		)).map { it.modelNameWithPrefix("${id.path.replaceFirst("item/", "")}_") }
		
		// 원래 json 업로드
		Texture.upload(
			id,
			this.writer,
			textureMap,
			wholeOverrides
		)
		
		// 오버라이드 업로드
		wholeOverrides.forEach { override -> Texture.upload(
			Identifier(ReImagined.MOD_ID, override.modelName).withPrefixedPath("item/"),
			this.writer,
			textureMap.mapValues { (key, value) ->
					value.withPath { it + "_${override.id}" }
			}
		)}
	}
	
	private fun ItemModelGenerator.generatePart(part: IPartItem) {
		if (part !is Item) return
		
		this.generate(part, "parts/${part.getType().name.pathName()}")
	}
	
	private fun ItemModelGenerator.generateGear(gear: IGearItem) {
		if (gear !is Item) return
		
		val texturePaths = mutableListOf<String>()
		gear.includeParts.forEach { partType ->
			texturePaths.add(
				"gear/${gear.getType().name.pathName()}/" +
						partType.name.split("_").last().pathName()
			)
		}
		
		this.generate(
			gear, *texturePaths.toTypedArray(),
			makeBrokenTexture = true
		)
	}
}