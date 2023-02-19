package net.rhseung.reimagined.tool

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class Property(
	val tier: Int,
	val weight: Int,
	val hardness: Int,
)
