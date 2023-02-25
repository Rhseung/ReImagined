package net.rhseung.reimagined.datagen

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.*
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined.modID
import net.rhseung.reimagined.ReImagined.minecraftID
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.definition.Gear
import net.rhseung.reimagined.tool.gears.GearClient
import net.rhseung.reimagined.tool.gears.GearClient.BROKEN_ID
import net.rhseung.reimagined.tool.gears.GearClient.GRIP_ID
import net.rhseung.reimagined.tool.parts.definitions.Part
import net.rhseung.reimagined.utils.Text.pathName
import net.rhseung.reimagined.utils.Utils.getClassName
import java.util.function.BiConsumer
import java.util.function.Supplier

class ModModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
	override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
	}
	
	override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
		ModItems.PARTS_LIST.forEach { itemModelGenerator.generatePart(it) }
		ModItems.GEARS_LIST.forEach { itemModelGenerator.generateGear(it) }
	}
	
	fun BiConsumer<Identifier, Supplier<JsonElement>>.upload(builder: ItemModelBuilder): Identifier {
		this.accept(builder.id, Supplier {
			val jsonObject = JsonObject()
			
			jsonObject.addProperty("parent", builder.parent.toString())
			
			if (builder.textures.isNotEmpty()) {
				val textureJsonObject = JsonObject()
				builder.textures.forEach { (textureKey: String, textureId: Identifier) ->
					textureJsonObject.addProperty(textureKey, textureId.toString())
				}
				jsonObject.add("textures", textureJsonObject)
			}
			
			if (builder.overrides.isNotEmpty()) {
				val overrideJsonArray = JsonArray()
				builder.overrides.forEach { overrideBuilder ->
					val eachOverride = JsonObject()
					
					val eachPredicate = JsonObject()
					overrideBuilder.predicates.forEach {
						eachPredicate.addProperty(it.key.toString(), it.value)
					}
					
					eachOverride.add("predicate", eachPredicate)
					eachOverride.addProperty("model", overrideBuilder.model.toString())
					
					overrideJsonArray.add(eachOverride)
				}
				jsonObject.add("overrides", overrideJsonArray)
			}
			
			jsonObject
		})
		
		return builder.id
	}
	
	fun ItemModelGenerator.generate(builder: ItemModelBuilder) {
		val modelCollector = this.writer
		
		modelCollector.upload(builder)
		builder.overrides.forEach { override ->
			modelCollector.upload(
				ItemModelBuilder(override.model)
					.setParent(builder.parent)
					.setTexture(override.textures)
			)
		}
	}
	
	class ItemModelBuilder constructor(val id: Identifier) {
		var parent = minecraftID("item/generated")
		var textures = mutableMapOf<String, Identifier>()
		var overrides = mutableListOf<OverrideBuilder>()
		
		fun setParent(parent: Identifier): ItemModelBuilder {
			this.parent = parent
			return this
		}
		
		fun setTexture(textures: MutableMap<String, Identifier>): ItemModelBuilder {
			this.textures = textures
			return this
		}
		
		fun addTexture(
			name: String,
			texture: String,
			prefix: String = "",
			postfix: String = "",
		): ItemModelBuilder {
			this.textures[name] = modID(prefix + texture + postfix)
			return this
		}
		
		fun addTexture(
			texture: String,
			prefix: String = "",
			postfix: String = "",
		): ItemModelBuilder {
			this.textures["layer${this.textures.count()}"] = modID(prefix + texture + postfix)
			return this
		}
		
		fun <T : Any> addTextures(
			values: Collection<T>,
			prefix: String = "",
			postfix: String = "",
			action: (T) -> String,
		): ItemModelBuilder {
			values.map(action)
				.forEach { texture ->
					this.textures["layer${this.textures.count()}"] = modID(prefix + texture + postfix)
				}
			return this
		}
		
		fun addOverride(): OverrideBuilder {
			return OverrideBuilder(this)
		}
		
		class OverrideBuilder(val itemModelBuilder: ItemModelBuilder) {
			var model = modID("item/")
			var predicates = mutableMapOf<Identifier, Number>()
			var textures = mutableMapOf<String, Identifier>()
			
			fun addPredicate(
				id: Identifier,
				value: Number,
			): OverrideBuilder {
				this.predicates[id] = value
				return this
			}
			
			fun setModel(
				model: String,
				postfix: String = "",
			): OverrideBuilder {
				this.model = modID(model).withPath { it + postfix }
				return this
			}
			
			fun setModel(
				model: Identifier,
				postfix: String = "",
			): OverrideBuilder {
				this.model = model.withPath { it + postfix }
				return this
			}
			
			fun addTexture(
				texture: String,
				prefix: String = "",
				postfix: String = "",
			): OverrideBuilder {
				this.textures["layer${this.textures.count()}"] = modID(prefix + texture + postfix)
				return this
			}
			
			fun <T : Any> addTextures(
				values: Collection<T>,
				prefix: String = "",
				postfix: String = "",
				action: (T) -> String,
			): OverrideBuilder {
				values.map(action)
					.forEach { texture ->
						this.textures["layer${this.textures.count()}"] = modID(prefix + texture + postfix)
					}
				return this
			}
			
			fun end(): ItemModelBuilder {
				itemModelBuilder.overrides.add(this)
				return itemModelBuilder
			}
		}
	}
	
	private fun ItemModelGenerator.generatePart(part: Part) {
		val identifier = ModItems.getId(part)
			.withPrefixedPath("item/")
		
		val builder = ItemModelBuilder(identifier)
			.addTexture("parts/${part.className.pathName()}", prefix = "item/")
		
		this.generate(builder)
	}
	
	private fun ItemModelGenerator.generateGear(gear: Gear) {
		val identifier = ModItems.getId(gear)
			.withPrefixedPath("item/")
		val parent = minecraftID("item/handheld")
		
		val builder = ItemModelBuilder(identifier)
			.setParent(parent)
			.addOverride()
				.addPredicate(BROKEN_ID, 1)
				.setModel(identifier, postfix = "_broken")
				.addTextures(gear.notOptionalParts, prefix = "item/", postfix = "_broken") {
					"gear/${gear.className.pathName()}/${getClassName(it.element).pathName()}"
				}
				.end()
			.addOverride()
				.addPredicate(GRIP_ID, 1)
				.setModel(identifier, postfix = "_grip")
				.addTextures(gear.includeParts, prefix = "item/") {
					"gear/${gear.className.pathName()}/${getClassName(it.element).pathName()}"
				}
				.end()
			.addTextures(gear.notOptionalParts, prefix = "item/") {
				"gear/${gear.className.pathName()}/${getClassName(it.element).pathName()}"
			}
		
		this.generate(builder)
	}
}