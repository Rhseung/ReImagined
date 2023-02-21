package net.rhseung.reimagined.tool.gears

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.utils.Texture.overrideTexture
import net.rhseung.reimagined.utils.Texture.tintTexture

@Environment(EnvType.CLIENT)
object GearClient {
	const val BROKEN_ID = "broken"
	
	fun textureProcess() {
		ModItems.GEARS_LIST.forEach { gear ->
			tintTexture({ stack, layerIndex ->
				gear.getParts(stack)[layerIndex].element.material.color
			}, gear)

			overrideTexture(BROKEN_ID, { stack, _, _, _ ->
				if (gear.isBroken(stack)) 1 else 0
			}, gear)
		}
	}
}