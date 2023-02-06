package net.rhseung.reimagined.tool.gears.util

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
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.utils.Color
import net.rhseung.reimagined.utils.Texture.overrideTexture
import net.rhseung.reimagined.utils.Texture.tintTexture

@Environment(EnvType.CLIENT)
object GearHelperClient {
	
	fun textureProcess() {
		ModItems.GEARS.forEach { value ->
			tintTexture({ stack, layerIndex ->
				value.getConstructions(stack)[value.includeParts[layerIndex]]!!.material.color
			}, value)
			
			if (value is Item) {
				overrideTexture("broken", { stack, world, entity, seed ->
					if (value.broken(stack)) 1 else 0
				}, value)
			}
		}
	}
}