package net.rhseung.reimagined.tool.gears.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.Item
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.utils.Texture.overrideTexture
import net.rhseung.reimagined.utils.Texture.tintTexture

@Environment(EnvType.CLIENT)
object GearHelperClient {
	
	fun textureProcess() {
		ModItems.GEARS.forEach { gear ->
			tintTexture({ stack, layerIndex ->
				gear.getParts(stack)[gear.includeParts[layerIndex]]!!.material.color
			}, gear)
			
			if (gear is Item) {
				overrideTexture("broken", { stack, world, entity, seed ->
					if (gear.isBroken(stack)) 1 else 0
				}, gear)
			}
		}
	}
}