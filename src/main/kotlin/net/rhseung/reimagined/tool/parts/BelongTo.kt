package net.rhseung.reimagined.tool.parts

import net.rhseung.reimagined.tool.parts.BasicPart
import kotlin.reflect.KClass

annotation class BelongTo(
	val value: KClass<out BasicPart>
)
