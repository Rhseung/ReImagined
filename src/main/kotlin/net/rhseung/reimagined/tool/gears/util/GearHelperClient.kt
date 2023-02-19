package net.rhseung.reimagined.tool.gears.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.utils.Color
import net.rhseung.reimagined.utils.Texture.overrideTexture
import net.rhseung.reimagined.utils.Texture.tintTexture

@Environment(EnvType.CLIENT)
object GearHelperClient {
	const val BROKEN_ID = "broken"
	
	fun textureProcess() {
		ModItems.GEARS_LIST.forEach { gear ->
			tintTexture({ stack, layerIndex ->
				gear.getParts(stack)[gear.type.includeParts[layerIndex]]!!.material.color
			}, gear)
			
			overrideTexture(BROKEN_ID, { stack, _, _, _ ->
				if (gear.isBroken(stack)) 1 else 0
			}, gear)
		}
	}
}