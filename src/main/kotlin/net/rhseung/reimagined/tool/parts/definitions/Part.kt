package net.rhseung.reimagined.tool.parts.definitions

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import net.rhseung.reimagined.registration.RegistryHelper
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.BelongsTo
import net.rhseung.reimagined.tool.parts.MoreStats
import net.rhseung.reimagined.tool.parts.Type
import net.rhseung.reimagined.utils.Utils.createInstance
import kotlin.reflect.KClass

sealed class Part constructor(
	open val material: Material,
	open val amount: Number = 1,
) : Item(Settings()) {
	
	@PartDefinition(
		belongsTo = BasicPart.Binding::class
	)
	class Binding(override val material: Material) : Part(material, amount = 0.5)
	
	@PartDefinition(
		belongsTo = BasicPart.Binding::class,
		materialType = Type(
			disallow = [Material.MaterialType.FIBER, Material.MaterialType.CLOTH]
		)
	)
	class Guard(override val material: Material) : Part(material, amount = 0.5)
	
	@PartDefinition(
		belongsTo = BasicPart.Handle::class,
		materialType = Type(
			disallow = [Material.MaterialType.FIBER, Material.MaterialType.CLOTH]
		)
	)
	class Rod(override val material: Material) : Part(material, amount = 0.5)
	
	@PartDefinition(
		belongsTo = BasicPart.Handle::class,
		materialType = Type(
			disallow = [Material.MaterialType.FIBER, Material.MaterialType.CLOTH]
		),
		moreStats = [Stat.MINING_SPEED]
	)
	class Handle(override val material: Material) : Part(material, amount = 0.5)
	
	@PartDefinition(
		belongsTo = BasicPart.Head::class,
		materialType = Type(
			disallow = [Material.MaterialType.FIBER, Material.MaterialType.CLOTH]
		),
		moreStats = [Stat.KNOCKBACK]
	)
	class Blade(override val material: Material) : Part(material, amount = 1)
	
	@PartDefinition(
		belongsTo = BasicPart.Head::class,
		materialType = Type(
			disallow = [Material.MaterialType.FIBER, Material.MaterialType.CLOTH]
		),
		moreStats = [Stat.MINING_SPEED, Stat.MINING_TIER]
	)
	class PickaxeHead(override val material: Material) : Part(material, amount = 1)
	
	@PartDefinition(
		belongsTo = BasicPart.Head::class,
		materialType = Type(
			disallow = [Material.MaterialType.FIBER, Material.MaterialType.CLOTH]
		),
		moreStats = [Stat.MINING_SPEED, Stat.MINING_TIER]
	)
	class AxeHead(override val material: Material) : Part(material, amount = 1)
	
	@PartDefinition(
		belongsTo = BasicPart.Head::class,
		materialType = Type(
			disallow = [Material.MaterialType.FIBER, Material.MaterialType.CLOTH]
		),
		moreStats = [Stat.MINING_SPEED, Stat.MINING_TIER]
	)
	class ShovelHead(override val material: Material) : Part(material, amount = 1)
	
	@PartDefinition(
		belongsTo = BasicPart.Head::class,
		materialType = Type(
			disallow = [Material.MaterialType.FIBER, Material.MaterialType.CLOTH]
		),
		moreStats = [Stat.MINING_SPEED, Stat.MINING_TIER]
	)
	class HoeHead(override val material: Material) : Part(material, amount = 1)
	
	@PartDefinition(
		belongsTo = BasicPart.Optional::class,
		materialType = Type(
			allow = [Material.MaterialType.CLOTH]
		),
		moreStats = [Stat.ATTACK_SPEED, Stat.MINING_SPEED]
	)
	class Grip(override val material: Material): Part(material)
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	var includeStats: Set<Stat> = setOf()
	val className: String = this.javaClass.simpleName
	lateinit var belong: KClass<out BasicPart>
	val isOptional by lazy { belong == BasicPart.Optional::class }
	
	init {
		this.javaClass.annotations.forEach { annotation ->
			when (annotation) {
				is PartDefinition -> {
					belong = annotation.belongsTo
					includeStats += createInstance(belong).commonStats + annotation.moreStats
				}
			}
		}
	}
	
	companion object {
		val classes = Part::class.sealedSubclasses.toTypedArray().toSet()
		val optionalClasses = classes.filter { clazz -> (clazz.annotations.find { it is PartDefinition } as PartDefinition).belongsTo == BasicPart.Optional::class }.toSet()
		val notOptionalClasses = classes - optionalClasses
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
