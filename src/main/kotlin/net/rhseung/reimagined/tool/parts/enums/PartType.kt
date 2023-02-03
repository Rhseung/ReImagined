package net.rhseung.reimagined.tool.parts.enums

import net.minecraft.item.Item
import net.rhseung.reimagined.tool.parts.BindingPart
import net.rhseung.reimagined.tool.parts.HandlePart
import net.rhseung.reimagined.tool.parts.HeadPart
import net.rhseung.reimagined.tool.parts.base.IPartItem

enum class PartType {
	HEAD,
	BINDING,
	HANDLE;
	
	fun cast(part: Item): IPartItem {
		return when (this) {
			HEAD -> part as HeadPart
            BINDING -> part as BindingPart
			HANDLE -> part as HandlePart
		}
	}
	
	companion object {
	}
}