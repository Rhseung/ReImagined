package net.rhseung.reimagined.tool.parts.definitions

import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.Type
import kotlin.reflect.KClass

annotation class PartDefinition constructor(
	val belongsTo: KClass<out BasicPart>,
	val materialType: Type = Type(),
	val moreStats: Array<Stat> = []
)
