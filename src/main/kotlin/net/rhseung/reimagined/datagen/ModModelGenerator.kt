package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.*
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.base.BasicGearItem
import net.rhseung.reimagined.tool.gears.util.GearHelperClient
import net.rhseung.reimagined.tool.parts.base.BasicPartItem
import net.rhseung.reimagined.utils.Text.pathName
import net.rhseung.reimagined.utils.Texture

class ModModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
	override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
	}
	
	override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
		ModItems.PARTS.values.forEach { partItems -> partItems.forEach { itemModelGenerator.generatePart(it) } }
		ModItems.GEARS_LIST.forEach { itemModelGenerator.generateGear(it) }
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
			GearHelperClient.BROKEN_ID,
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
			textureMap.mapValues { (_, value) ->
					value.withPath { it + "_${override.id}" }
			}
		)}
	}
	
	private fun ItemModelGenerator.generatePart(part: BasicPartItem) {
		this.generate(part, "parts/${part.type!!.name.pathName()}")
	}
	
	private fun ItemModelGenerator.generateGear(gear: BasicGearItem) {
		val texturePaths = mutableListOf<String>()
		
		gear.type.includeParts.forEach { partType ->
			texturePaths.add(
				"gear/${gear.type.name.pathName()}/" +
						partType.name.split("_").last().pathName()
			)
		}
		
		this.generate(
			gear, *texturePaths.toTypedArray(),
			makeBrokenTexture = true
		)
	}
}