package net.rhseung.reimagined.tool.gears.enums

import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.enums.PartType

enum class GearBasicType constructor(
	val includeStats: Set<Stat>,
	val includeParts: List<PartType>
) {
	GEAR(
		includeStats = setOf(
			Stat.DURABILITY,
			Stat.ENCHANTABILITY
		),
		includeParts = listOf(
			PartType.HANDLE,
			PartType.BINDING
		)
	),
	
	MINING_TOOL(
		includeStats = GEAR.includeStats + setOf(Stat.MINING_SPEED, Stat.MINING_TIER, Stat.ATTACK_SPEED, Stat.ATTACK_DAMAGE),
		includeParts = GEAR.includeParts
	),
	
	MELEE_WEAPON(
		includeStats = GEAR.includeStats + setOf(Stat.ATTACK_SPEED, Stat.ATTACK_DAMAGE),
		includeParts = GEAR.includeParts
	),
	
	RANGE_WEAPON(
		includeStats = setOf(), // todo: + setOf(Stat.CHARGE_SPEED, Stat.PROJECTILE_DAMAGE),
		includeParts = listOf() // 활이냐 석궁이냐 삼지창이냐 뭐냐에 따라 완전 천차만별이라 딱히 공통 parts가 없음
	);
}