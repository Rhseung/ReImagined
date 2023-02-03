package net.rhseung.reimagined.utils

import java.util.*

object Name {
	fun String.toDisplayName(): String {
		return this.lowercase().split("_")
			.joinToString(" ") { it.replaceFirstChar { it.titlecase() } }
	}
	
	fun String.toPathName(): String {
		return this.replace(" ", "_").lowercase()
	}
}