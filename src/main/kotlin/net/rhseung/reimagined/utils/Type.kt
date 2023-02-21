package net.rhseung.reimagined.utils

import net.rhseung.reimagined.tool.parts.BelongTo
import kotlin.reflect.KClass

class Type<T: Any> constructor(
	val kclass: KClass<T>,
	vararg val annotations: Annotation
) {
	val className = kclass.java.simpleName
//	val belong = annotations.find { it is BelongTo }?.
}