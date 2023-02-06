package net.rhseung.reimagined.utils

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
}