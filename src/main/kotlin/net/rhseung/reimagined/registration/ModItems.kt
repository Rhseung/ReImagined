package net.rhseung.reimagined.registration

import net.rhseung.reimagined.tool.gears.Gear
import net.rhseung.reimagined.tool.parts.Part

object ModItems {
	val GEARS_LIST: List<Gear> = Gear.instanceMap.values.toList()
	val PARTS_LIST: List<Part> = Part.instanceMap.values.toList().flatten()
}