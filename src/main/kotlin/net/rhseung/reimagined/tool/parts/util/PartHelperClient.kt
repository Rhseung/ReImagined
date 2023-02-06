package net.rhseung.reimagined.tool.parts.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.registry.Registries
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.gears.util.GearHelper
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.utils.Texture

@Environment(EnvType.CLIENT)
object PartHelperClient {
	
	fun textureProcess() {
		ModItems.PARTS.values.forEach { valueList ->
			valueList.forEach { value ->
				Texture.tintTexture({ stack, layerIndex ->
					value.material.color
				}, value)
			}
		}
	}
}