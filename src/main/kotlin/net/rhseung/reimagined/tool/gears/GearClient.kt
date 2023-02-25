package net.rhseung.reimagined.tool.gears

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined.modID
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.utils.Texture.overrideTexture
import net.rhseung.reimagined.utils.Texture.tintTexture

object GearClient {
	val BROKEN_ID = modID("broken")
	val GRIP_ID = modID("grip")
	
	enum class Model(val postfix: String = "") {
		DEFAULT(),
		BROKEN("_broken"),
		GRIP("_grip")
	}
	
	fun Identifier.withPostfixedPath(postfix: String): Identifier {
		return this.withPath { it + postfix }
	}
	
	@Environment(EnvType.CLIENT)
	fun textureProcess() {
		ModItems.GEARS_LIST.forEach { gear ->
			tintTexture({ stack, layerIndex ->
				gear.getParts(stack)[layerIndex].element?.material?.color ?: Material.DUMMY.color
			}, gear)

			overrideTexture(BROKEN_ID, { stack, _, _, _ ->
				val n = if (gear.isBroken(stack)) 1
				else 0
				
				n }, gear)
			
			overrideTexture(GRIP_ID, { stack, _, _, _ ->
				val n = if (gear.getPart(stack, gear.optionalParts[0].element) != null) 1
				else 0
				
				n }, gear)
		}
	}
}