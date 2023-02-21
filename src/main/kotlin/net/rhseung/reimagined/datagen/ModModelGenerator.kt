package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.*
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.Gear
import net.rhseung.reimagined.tool.gears.GearClient
import net.rhseung.reimagined.tool.parts.Part
import net.rhseung.reimagined.utils.Text.pathName
import net.rhseung.reimagined.utils.Texture
import net.rhseung.reimagined.utils.Utils.getClassName

class ModModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
	override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
	}
	
	override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
		ModItems.PARTS_LIST.forEach { itemModelGenerator.generatePart(it) }
		ModItems.GEARS_LIST.forEach { itemModelGenerator.generateGear(it) }
	}
	
	private fun <T : Item> ItemModelGenerator.generate(
		item: T,
		vararg texturePath: String,
		makeBrokenTexture: Boolean = false,
		overrides: MutableList<Texture.OverrideTexture> = mutableListOf(),
	) {
		val id = Registries.ITEM.getId(item).withPrefixedPath("item/")
		val textureMap = mutableMapOf<TextureKey, Identifier>()
		texturePath.forEachIndexed { index, path ->
			textureMap[TextureKey.of("layer$index")] = Identifier(ReImagined.MOD_ID, "item/$path")
		}
		
		// 파일 이름 정하기
		if (makeBrokenTexture)
			overrides.add(Texture.OverrideTexture(GearClient.BROKEN_ID, 1))
		val wholeOverrides = overrides.map {
			it.modelNameWithPrefix(id.path.replaceFirst("item/", "") + "_")
		}
		
		// 모델 업로드
		Texture.upload(
			id,
			this.writer,
			textureMap,
			wholeOverrides
		)
		
		// 오버라이드 모델 업로드
		wholeOverrides.forEach { override -> Texture.upload(
			Identifier(ReImagined.MOD_ID, override.modelName).withPrefixedPath("item/"),
			this.writer,
			textureMap.mapValues { (_, value) ->
					value.withPath { it + "_${override.id}" }
			}
		)}
	}
	
	private fun ItemModelGenerator.generatePart(part: Part) {
		this.generate(part, "parts/${part.className.pathName()}")
	}
	
	private fun ItemModelGenerator.generateGear(gear: Gear) {
		val texturePaths = gear.includeParts.map {
			"gear/${gear.className.pathName()}/${getClassName(it.element).pathName()}"
		}.toTypedArray()
		
		this.generate(
			gear, *texturePaths,
			makeBrokenTexture = true
		)
	}
}