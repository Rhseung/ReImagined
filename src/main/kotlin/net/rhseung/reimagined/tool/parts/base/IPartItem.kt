package net.rhseung.reimagined.tool.parts.base

import com.ibm.icu.text.MessagePattern.Part
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.parts.enums.PartType

interface IPartItem {
	val material: Material
	
	fun getType(): PartType
}