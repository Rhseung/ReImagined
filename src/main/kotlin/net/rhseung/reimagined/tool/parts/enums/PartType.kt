package net.rhseung.reimagined.tool.parts.enums

import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.parts.AxeHeadPart
import net.rhseung.reimagined.tool.parts.BindingPart
import net.rhseung.reimagined.tool.parts.HandlePart
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart
import net.rhseung.reimagined.tool.parts.base.BasicPartItem
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

enum class PartType constructor(
	private val partClass: KClass<out BasicPartItem>,
	val includeStats: Set<Stat>
) {
	PICKAXE_HEAD(PickaxeHeadPart::class, setOf(
		Stat.DURABILITY,
		Stat.MINING_SPEED,
		Stat.MINING_TIER,
		Stat.ATTACK_DAMAGE
	)),
	AXE_HEAD(AxeHeadPart::class, setOf(
		Stat.DURABILITY,
		Stat.MINING_SPEED,
		Stat.MINING_TIER,
		Stat.ATTACK_DAMAGE
	)),
	BINDING(BindingPart::class, setOf(
		Stat.DURABILITY,
		Stat.ENCHANTABILITY
	)),
	HANDLE(HandlePart::class, setOf(
		Stat.DURABILITY,
		Stat.ATTACK_SPEED,
		Stat.MINING_SPEED
	));
	
	//	fun instance(material: Material) = partClass.primaryConstructor!!.call(material)
	fun instance(material: Material) = when (this) {
		PICKAXE_HEAD -> PickaxeHeadPart(material)
		AXE_HEAD -> AxeHeadPart(material)
		BINDING -> BindingPart(material)
		HANDLE -> HandlePart(material)
	}
	
	companion object {
		val HEAD = arrayOf(PICKAXE_HEAD, AXE_HEAD)
		fun HEAD(gearType: GearType): PartType = gearType.includeParts[1]   // layer1 이 head 텍스쳐이기 때문
		
		fun getValues() = PartType.values()
	}
}