package net.rhseung.reimagined.tool.gears

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined.ID
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.parts.definitions.BasicPart
import net.rhseung.reimagined.utils.Texture.overrideTexture
import net.rhseung.reimagined.utils.Texture.tintTexture
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer.ValueParametersHandler.DEFAULT

object GearClient {
	val MODEL_ID = ID("gear_model")
	val BROKEN_ID = ID("broken")
	
	enum class Model(val number: Int, val postfix: String = "") {
		DEFAULT(0),
		BROKEN(1, "_broken"),
		GRIP(2, "_grip")
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

			overrideTexture(MODEL_ID, { stack, _, _, _ ->
				val n = if (gear.isBroken(stack)) Model.BROKEN.number
				else if (gear.getPart(stack, gear.optionalParts[0].element) != null) Model.GRIP.number
				else Model.DEFAULT.number
				
				n }, gear)
		}
	}
}