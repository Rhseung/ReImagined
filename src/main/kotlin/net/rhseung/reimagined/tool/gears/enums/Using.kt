package net.rhseung.reimagined.tool.gears.enums

import net.rhseung.reimagined.tool.gears.base.BasicGearItem
import kotlin.reflect.KClass

annotation class Using(val value: KClass<out BasicGearItem>)
