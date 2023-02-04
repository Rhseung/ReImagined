package net.rhseung.reimagined.tool.parts.util

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.registry.Registries
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.gears.util.GearHelper
import net.rhseung.reimagined.tool.parts.base.IPartItem

object PartHelperClient {
	fun tint() {
		Registries.ITEM.entrySet.forEach { (key, value) ->
			if (value is IPartItem) {
				ColorProviderRegistry.ITEM.register(ItemColorProvider { stack, tintIndex ->
					(value as IPartItem).material.color.toHex()
				}, value)
			}
		}
	}
}