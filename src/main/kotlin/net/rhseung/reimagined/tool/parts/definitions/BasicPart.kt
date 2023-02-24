package net.rhseung.reimagined.tool.parts.definitions

import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.BelongsTo
import net.rhseung.reimagined.utils.Utils.createInstance

sealed class BasicPart constructor(
	vararg _commonStats: Stat
) {
	class Common : BasicPart(Stat.DURABILITY, Stat.ATTACK_SPEED, Stat.ENCHANTABILITY)
	
	class Optional : BasicPart()
	
	@PartDefinition(belongsTo = Common::class)
	class Head : BasicPart(Stat.ATTACK_DAMAGE)
	
	@PartDefinition(belongsTo = Common::class)
	class Binding : BasicPart()
	
	@PartDefinition(belongsTo = Common::class)
	class Handle : BasicPart(Stat.ATTACK_DAMAGE)
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	val className: String = this.javaClass.simpleName
	var commonStats: Set<Stat> = _commonStats.toSet()
	
	init {
		this.javaClass.annotations.forEach { annotation ->
			when (annotation) {
				is PartDefinition -> {
					val instance = createInstance(annotation.belongsTo)
					commonStats += instance.commonStats
				}
			}
		}
	}
	
	companion object {
		val classes = BasicPart::class.sealedSubclasses
	}
	
	override fun toString(): String {
		return "$className(" +
		       "commonStats=$commonStats)"
	}
	
	override fun equals(other: Any?): Boolean {
		return other != null && other is BasicPart &&
		       other.commonStats == this.commonStats &&
		       other.className == this.className
	}
}
