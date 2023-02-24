package net.rhseung.reimagined.tool.gears.definition

import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.BelongsTo
import net.rhseung.reimagined.tool.parts.definitions.BasicPart
import net.rhseung.reimagined.utils.Utils.createInstance
import net.rhseung.reimagined.utils.Utils.up
import kotlin.reflect.KClass

sealed class BasicGear constructor(
	var commonStats: Set<Stat> = setOf(),
	var commonPartSlots: List<KClass<out BasicPart>> = listOf(),
	var modifierName: String = "",
) {
	
	val className = this.javaClass.simpleName
	
	class Common : BasicGear(
		commonStats = setOf(Stat.DURABILITY, Stat.ENCHANTABILITY),
		commonPartSlots = listOf()
	) {}
	
	@BelongsTo(Common::class)
	class Closed : BasicGear(
		commonStats = setOf(Stat.ATTACK_DAMAGE, Stat.ATTACK_SPEED),
		commonPartSlots = listOf()
	) {}
	
	@BelongsTo(Common::class)
	class Ranged : BasicGear(
		commonStats = setOf(Stat.RANGED_DAMAGE, Stat.DRAW_SPEED)
	) {}
	
	@BelongsTo(Closed::class)
	class MiningTool : BasicGear(
		commonStats = setOf(Stat.MINING_TIER, Stat.MINING_SPEED),
		commonPartSlots = listOf(BasicPart.Handle::class)
							.up(BasicPart.Head::class)
							.up(BasicPart.Binding::class),
		modifierName = "Tool modifier"
	) {}
	
	@BelongsTo(Closed::class)
	class MeleeWeapon : BasicGear(
		commonPartSlots = listOf(BasicPart.Handle::class)
							.up(BasicPart.Head::class)
							.up(BasicPart.Binding::class),
		modifierName = "Weapon modifier"
	) {}
	
	init {
		commonPartSlots = commonPartSlots.up(BasicPart.Optional::class)
		
		this.javaClass.annotations.forEach { annotation ->
			when (annotation) {
				is BelongsTo -> {
					annotation.value.forEach {
						val instance = createInstance(it)
						
						commonStats += instance.commonStats
						if (commonPartSlots.isEmpty()) commonPartSlots = instance.commonPartSlots
					}
				}
			}
		}
	}
	
	companion object {
	}
	
	override fun toString(): String {
		return "$className(" +
		       "commonStats=$commonStats, " +
		       "commonPartSlots=$commonPartSlots)"
	}
	
	override fun equals(other: Any?): Boolean {
		return other != null && other is BasicGear &&
		       other.commonStats == this.commonStats &&
		       other.commonPartSlots == this.commonPartSlots &&
		       other.modifierName == this.modifierName &&
		       other.className == this.className
	}
}
