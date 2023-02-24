package net.rhseung.reimagined.tool.parts

import net.rhseung.reimagined.tool.parts.definitions.BasicPart
import kotlin.reflect.KClass

annotation class BelongsTo(
	val value: KClass<out BasicPart>
)
