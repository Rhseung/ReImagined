package net.rhseung.reimagined.tool.gears.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.registry.Registries
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.gears.util.GearHelper.getConstructions

@Environment(EnvType.CLIENT)
object GearHelperClient {
	
	// todo: 텍스쳐 틴트
	fun tint() {
		Registries.ITEM.entrySet.forEach { (key, value) ->
			if (value is IGearItem) {
				ColorProviderRegistry.ITEM.register(ItemColorProvider { stack, layerIndex ->
					val parts = value.getConstructions(stack)
					
					parts[value.includeParts[layerIndex]]!!.material.color.toHex()
				}, value)
			}
		}
	}
	
	// todo: 텍스쳐 오버라이딩
	
	
}