package net.rhseung.reimagined.tool.gears

import net.rhseung.reimagined.tool.gears.definition.BasicGear
import kotlin.reflect.KClass

annotation class BelongsTo(
	vararg val value: KClass<out BasicGear>
)
