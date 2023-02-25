package net.rhseung.reimagined.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.rhseung.reimagined.datagen.ModModelGenerator
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Supplier

object Texture {
	@Environment(EnvType.CLIENT)
	fun tintTexture(
		colorProvider: (stack: ItemStack, layerIndex: Int) -> Color,
		vararg item: ItemConvertible
	) {
		ColorProviderRegistry.ITEM.register(ItemColorProvider { stack, layerIndex ->
			colorProvider(stack, layerIndex).toHex()
		}, *item)
	}
	
	@Environment(EnvType.CLIENT)
	fun overrideTexture(
		id: Identifier,
		predicateProvider: (stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int) -> Number,
		item: Item
	) {
		ModelPredicateProviderRegistry.register(item, id) { stack, world, entity, seed ->
			predicateProvider(stack, world, entity, seed).toFloat()
		}
	}
	
	class OverrideBuilder constructor(
		vararg val predicates: Pair<Identifier, Number>,
		val modelName: String = predicates.joinToString("") { "_" + it.first },
		val prefix: String = ""
	) {
		fun addPredicate(id: Identifier, value: Number): OverrideBuilder {
			return OverrideBuilder(*predicates, id to value, modelName = modelName, prefix = prefix)
		}
		
		fun setModelName(modelName: String): OverrideBuilder {
			return OverrideBuilder(*predicates, modelName = modelName)
		}
		
		fun setModelNamePrefix(prefix: String): OverrideBuilder {
			return OverrideBuilder(*predicates, modelName = modelName, prefix = prefix)
		}
	}
	
	fun upload(
		modelCollector: BiConsumer<Identifier, Supplier<JsonElement>>,
		builder: ModModelGenerator.ItemModelBuilder
	): Identifier {
		modelCollector.accept(builder.id, Supplier {
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
						eachPredicate.addProperty(it.key.toString(), 1)
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
}