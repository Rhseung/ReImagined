package net.rhseung.reimagined.tool

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class Type(
	val type: Material.MaterialType
)
