package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.*
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined.ID
import net.rhseung.reimagined.ReImagined.MinecraftID
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.definition.Gear
import net.rhseung.reimagined.tool.gears.GearClient
import net.rhseung.reimagined.tool.gears.GearClient.MODEL_ID
import net.rhseung.reimagined.tool.gears.GearClient.withPostfixedPath
import net.rhseung.reimagined.tool.parts.definitions.BasicPart
import net.rhseung.reimagined.tool.parts.definitions.Part
import net.rhseung.reimagined.utils.Bunch
import net.rhseung.reimagined.utils.Text.pathName
import net.rhseung.reimagined.utils.Texture
import net.rhseung.reimagined.utils.Utils.associateIndexed
import net.rhseung.reimagined.utils.Utils.getClassName
import kotlin.reflect.KClass

class ModModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
	override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
	}
	
	override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
		ModItems.PARTS_LIST.forEach { itemModelGenerator.generatePart(it) }
		ModItems.GEARS_LIST.forEach { itemModelGenerator.generateGear(it) }
	}
	
	class ItemModelBuilder constructor(val id: Identifier) {
		var parent = MinecraftID("item/generated")
		var textures = mutableMapOf<String, Identifier>()
		var overrides = mutableListOf<OverrideBuilder>()
		
		fun setParent(parent: Identifier): ItemModelBuilder {
			this.parent = parent
			return this
		}
		
		fun addTexture(name: String, texture: String, prefix: String = "", postfix: String = ""): ItemModelBuilder {
			this.textures[name] = ID(prefix + texture + postfix)
			return this
		}
		
		fun addTexture(texture: String, prefix: String = "", postfix: String = ""): ItemModelBuilder {
			this.textures["layer${this.textures.count()}"] = ID(prefix + texture + postfix)
			return this
		}
		
		fun <T: Any> addTextures(values: Collection<T>, prefix: String = "", postfix: String = "", action: (T) -> String): ItemModelBuilder {
			values.map(action).forEach { texture ->
				this.textures["layer${this.textures.count()}"] = ID(prefix + texture + postfix)
			}
			return this
		}
		
		fun addOverride(): OverrideBuilder {
			return OverrideBuilder(this)
		}
		
		class OverrideBuilder(val itemModelBuilder: ItemModelBuilder) {
			var model = ID("item/")
			var predicates = mutableMapOf<Identifier, GearClient.Model>()
			
			fun addPredicate(id: Identifier, value: GearClient.Model): OverrideBuilder {
				this.predicates[id] = value
				return this
			}
			
			fun setModel(model: String, postfix: String = ""): OverrideBuilder {
				this.model = ID(model).withPath { it + postfix }
				return this
			}
			
			fun setModel(model: Identifier, postfix: String = ""): OverrideBuilder {
				this.model = model.withPath { it + postfix }
				return this
			}
			
			fun end(): ItemModelBuilder {
				itemModelBuilder.overrides.add(this)
				return itemModelBuilder
			}
		}
	}
	
	private fun ItemModelGenerator.generate(builder: ItemModelBuilder) {
		Texture.upload(this.writer, builder)
	}
	
	private fun ItemModelGenerator.generatePart(part: Part) {
		val identifier = ModItems.getId(part).withPrefixedPath("item/")
		
		val builder = ItemModelBuilder(identifier)
			.addTexture("parts/${part.className.pathName()}", prefix = "item/")
		
		this.generate(builder)
	}
	
	private fun ItemModelGenerator.generateGear(gear: Gear) {
		val identifier = ModItems.getId(gear).withPrefixedPath("item/")
		val parent = MinecraftID("item/handheld")
		
		val builder = ItemModelBuilder(identifier)
			.setParent(parent)
			.addOverride()
				.addPredicate(MODEL_ID, GearClient.Model.BROKEN)
				.setModel(identifier, postfix = GearClient.Model.BROKEN.postfix)
				.end()
			.addOverride()
				.addPredicate(MODEL_ID, GearClient.Model.GRIP)
				.setModel(identifier, postfix = GearClient.Model.GRIP.postfix)
				.end()
			.addTextures(gear.notOptionalParts, prefix = "item/") {
				"gear/${gear.className.pathName()}/${getClassName(it.element).pathName()}"
			}
		
		this.generate(builder)
		
		builder.overrides.forEachIndexed { index, overrideBuilder ->
			val postfix = overrideBuilder.predicates.values.joinToString("") { it.postfix }
			
			when (index) {
				0 -> this.generate(ItemModelBuilder(overrideBuilder.model)
					   .addTextures(gear.notOptionalParts, prefix = "item/", postfix = postfix) {
					        "gear/${gear.className.pathName()}/${getClassName(it.element).pathName()}"
			    })
				1 -> this.generate(ItemModelBuilder(overrideBuilder.model)
					   .addTextures(gear.includeParts, prefix = "item/") {
						"gear/${gear.className.pathName()}/${getClassName(it.element).pathName()}"
		        })
			}
		}
	}
}