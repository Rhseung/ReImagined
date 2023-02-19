package net.rhseung.reimagined.tool.parts.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.utils.Texture

@Environment(EnvType.CLIENT)
object PartHelperClient {
	
	fun textureProcess() {
		ModItems.PARTS_LIST.forEach {
			Texture.tintTexture({ _, _ -> it.material.color }, it)
		}
	}
}