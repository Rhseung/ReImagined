package net.rhseung.reimagined.tool.parts

import net.rhseung.reimagined.tool.Material

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Type(
	val allow: Array<Material.MaterialType> = [],
	val disallow: Array<Material.MaterialType> = []
)
