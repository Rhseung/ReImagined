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
import net.minecraft.data.client.TextureKey
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
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
		id: String,
		predicateProvider: (stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int) -> Number,
		item: Item
	) {
		ModelPredicateProviderRegistry.register(item, Identifier(id)) { stack, world, entity, seed ->
			predicateProvider(stack, world, entity, seed).toFloat()
		}
	}
	
	class OverrideTexture(val id: String, val value: Number, val modelName: String = id) {
		fun modelNameWithPrefix(prefix: String): OverrideTexture {
			return OverrideTexture(id, value, modelName = prefix + id)
		}
	}
	
	fun upload(
		id: Identifier,
		modelCollector: BiConsumer<Identifier, Supplier<JsonElement>>,
		textures: Map<TextureKey, Identifier>,
		overrides: List<OverrideTexture> = emptyList()
	): Identifier {
		val parent = Optional.of(Identifier("minecraft", "item/handheld"))
		
		modelCollector.accept(id, Supplier {
			val jsonObject = JsonObject()
			
			parent.ifPresent { parentId: Identifier ->
				jsonObject.addProperty("parent", parentId.toString())
			}
			
			if (textures.isNotEmpty()) {
				val textureJsonObject = JsonObject()
				textures.forEach { (textureKey: TextureKey, textureId: Identifier) ->
					textureJsonObject.addProperty(textureKey.name, textureId.toString())
				}
				jsonObject.add("textures", textureJsonObject)
			}
			
			if (overrides.isNotEmpty()) {
				val overrideJsonArray = JsonArray()
				overrides.forEach {
					val eachOverride = JsonObject()
					val eachPredicate = JsonObject()
					eachPredicate.addProperty(it.id, it.value)
					eachOverride.add("predicate", eachPredicate)
					eachOverride.addProperty("model", "${ReImagined.MOD_ID}:item/${it.modelName}")
					
					overrideJsonArray.add(eachOverride)
				}
				jsonObject.add("overrides", overrideJsonArray)
			}
			
			jsonObject
		})
		return id
	}
}