package net.rhseung.reimagined.tool.gears

import kotlin.reflect.KClass

annotation class BelongTo(
	vararg val value: KClass<out BasicGear>
)
