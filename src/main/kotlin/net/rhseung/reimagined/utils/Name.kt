package net.rhseung.reimagined.utils

object Name {
	fun String.displayName(): String {
		return this.lowercase().split("_")
			.joinToString(" ") { it.replaceFirstChar { it.titlecase() } }
	}
	
	fun String.pathName(): String {
		return this.replace(" ", "_").lowercase()
	}
}