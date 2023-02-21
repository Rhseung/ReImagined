package net.rhseung.reimagined.tool.parts

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import net.rhseung.reimagined.registration.RegistryHelper
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.utils.Utils.createInstance
import kotlin.reflect.KClass

sealed class Part constructor(
	open val material: Material,
	vararg moreStats: Stat,
) : Item(Settings()) {
	lateinit var includeStats: Set<Stat>
	lateinit var belong: KClass<out BasicPart>
	
	val className: String = this.javaClass.simpleName
	
	// todo: 제작 시 필요한 재료 개수
	
	@BelongTo(BasicPart.Binding::class)
	class Binding(override val material: Material) : Part(
		material,
	) {}
	
	@BelongTo(BasicPart.Binding::class)
	class Guard(override val material: Material) : Part(
		material,
	) {}
	
	@BelongTo(BasicPart.Handle::class)
	class Handle(override val material: Material) : Part(
		material, Stat.MINING_SPEED
	) {}
	
	@BelongTo(BasicPart.Head::class)
	class Blade(override val material: Material) : Part(
		material,
	) {}
	
	@BelongTo(BasicPart.Head::class)
	class PickaxeHead(override val material: Material) : Part(
		material, Stat.MINING_SPEED, Stat.MINING_TIER
	) {}
	
	@BelongTo(BasicPart.Head::class)
	class AxeHead(override val material: Material) : Part(
		material, Stat.MINING_SPEED, Stat.MINING_TIER
	) {}
	
	@BelongTo(BasicPart.Head::class)
	class ShovelHead(override val material: Material) : Part(
		material, Stat.MINING_SPEED, Stat.MINING_TIER
	) {}
	
	@BelongTo(BasicPart.Head::class)
	class HoeHead(override val material: Material) : Part(
		material, Stat.MINING_SPEED, Stat.MINING_TIER
	) {}
	
	init {
		this.javaClass.annotations.forEach { annotation ->
			when (annotation) {
				is BelongTo -> {
					belong = annotation.value
					includeStats = createInstance(belong).commonStats + moreStats.toSet()
				}
			}
		}
	}
	
	companion object {
		val classes = Part::class.sealedSubclasses.toTypedArray()
		val instanceMap = classes.associateWith { RegistryHelper.registerParts(it) }
	}
	
	override fun toString(): String {
		return "${this.javaClass.simpleName}(" +
		       "material=$material, " +
		       "belong=$belong, " +
		       "includeStats=$includeStats)"
	}
	
	override fun equals(other: Any?): Boolean {
		return other != null && other is Part &&
		       other.material == this.material &&
		       other.includeStats == this.includeStats &&
		       other.belong == this.belong &&
		       other.className == this.className
	}
	
	fun getStat(stat: Stat) = material.getStat(stat)
	
	override fun isEnchantable(stack: ItemStack) = false
	
	override fun appendTooltip(
		stack: ItemStack,
		world: World?,
		tooltip: MutableList<Text>,
		context: TooltipContext,
	) {
		includeStats.forEach {
			tooltip.add(it.getDisplayTextUsingValue(stack, material.getStat(it)))
		}
	}
}
