package net.rhseung.reimagined.tool.parts

import net.minecraft.item.Item
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.Gear
import net.rhseung.reimagined.utils.Utils.createInstance

sealed class BasicPart constructor(
	var commonStats: Set<Stat> = setOf()
) {
	
	val className = this.javaClass.simpleName
	
	class Common : BasicPart(
		commonStats = setOf(Stat.DURABILITY, Stat.ATTACK_SPEED, Stat.ENCHANTABILITY)
	)
	
	@BelongTo(Common::class)
	class Head : BasicPart(
		commonStats = setOf(Stat.ATTACK_DAMAGE)
	) {}
	
	@BelongTo(Common::class)
	class Binding : BasicPart(
	) {}
	
	@BelongTo(Common::class)
	class Handle : BasicPart(
		commonStats = setOf(Stat.ATTACK_DAMAGE)
	) {}
	
	init {
		this.javaClass.annotations.forEach { annotation ->
			when (annotation) {
				is BelongTo -> {
					val instance = createInstance(annotation.value)
					
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
