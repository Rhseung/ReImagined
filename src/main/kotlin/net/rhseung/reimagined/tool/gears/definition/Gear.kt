package net.rhseung.reimagined.tool.gears.definition

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Vanishable
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.rhseung.reimagined.registration.ModBlockTags
import net.rhseung.reimagined.registration.RegistryHelper
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.BelongsTo
import net.rhseung.reimagined.tool.gears.GearStack
import net.rhseung.reimagined.tool.gears.Property
import net.rhseung.reimagined.tool.parts.definitions.BasicPart
import net.rhseung.reimagined.tool.parts.definitions.Part
import net.rhseung.reimagined.utils.Bunch
import net.rhseung.reimagined.utils.Bunch.Companion.to
import net.rhseung.reimagined.utils.Utils.append
import net.rhseung.reimagined.utils.Utils.createInstance
import kotlin.properties.Delegates
import kotlin.reflect.KClass

sealed class Gear constructor(
	val effectiveBlocks: TagKey<Block>,
	val moreParts: Array<Bunch<KClass<out BasicPart>, KClass<out Part>>>,
) : Item(Settings().maxCount(1)), Vanishable {
	
	@BelongsTo(BasicGear.MiningTool::class)
	@Property(1.0, 1.2)
	class Pickaxe : Gear(
		effectiveBlocks = BlockTags.PICKAXE_MINEABLE,
		moreParts = arrayOf(
			BasicPart.Head::class    to Part.PickaxeHead::class,
			BasicPart.Binding::class to Part.Binding::class,
			BasicPart.Handle::class  to Part.Handle::class,
			BasicPart.Optional::class  to Part.Grip::class,
		)
	) {}
	
	@BelongsTo(BasicGear.MiningTool::class)
	@Property(5.0, 0.9)
	class Axe : Gear(
		effectiveBlocks = BlockTags.AXE_MINEABLE,
		moreParts = arrayOf(
			BasicPart.Head::class    to Part.AxeHead::class,
			BasicPart.Binding::class to Part.Binding::class,
			BasicPart.Handle::class  to Part.Handle::class,
			BasicPart.Optional::class  to Part.Grip::class,
		)
	) {}
	
	@BelongsTo(BasicGear.MiningTool::class)
	@Property(1.5, 1.0)
	class Shovel : Gear(
		effectiveBlocks = BlockTags.SHOVEL_MINEABLE,
		moreParts = arrayOf(
			BasicPart.Head::class    to Part.ShovelHead::class,
			BasicPart.Binding::class to Part.Binding::class,
			BasicPart.Handle::class  to Part.Handle::class,
			BasicPart.Optional::class  to Part.Grip::class,
		)
	) {}
	
	@BelongsTo(BasicGear.MiningTool::class)
	@Property(1.0, 1.4)
	class Hoe : Gear(
		effectiveBlocks = BlockTags.HOE_MINEABLE,
		moreParts = arrayOf(
			BasicPart.Head::class    to Part.HoeHead::class,
			BasicPart.Binding::class to Part.Binding::class,
			BasicPart.Handle::class  to Part.Handle::class,
			BasicPart.Optional::class  to Part.Grip::class,
		)
	) {}
	
	@BelongsTo(BasicGear.MeleeWeapon::class)
	@Property(3.0, 1.6)
	class Sword : Gear(
		effectiveBlocks = ModBlockTags.SWORD_MINEABLE,
		moreParts = arrayOf(
			BasicPart.Head::class    to Part.Blade::class,
			BasicPart.Binding::class to Part.Guard::class,
			BasicPart.Handle::class  to Part.Rod::class,
			BasicPart.Optional::class  to Part.Grip::class,
		)
	) {}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	var includeStats: Set<Stat> = setOf()
	var includeParts: List<Bunch<KClass<out BasicPart>, KClass<out Part>>> = listOf()
	var optionalParts: List<Bunch<KClass<out BasicPart>, KClass<out Part>>> = listOf()
	var notOptionalParts: List<Bunch<KClass<out BasicPart>, KClass<out Part>>> = listOf()
	var belongInstance: List<BasicGear> = listOf()
	val className: String = this.javaClass.simpleName
	var baseAttackDamage by Delegates.notNull<Double>()
	var baseAttackSpeed by Delegates.notNull<Double>()
	
	init {
		this.javaClass.annotations.forEach { annotation ->
			when (annotation) {
				is BelongsTo -> {
					annotation.value.forEach {
						val instance = createInstance(it)
						belongInstance += instance
						includeStats += instance.commonStats
					}
				}
				is Property  -> {
					baseAttackDamage = annotation.baseAttackDamage
					baseAttackSpeed = annotation.baseAttackSpeed
				}
			}
		}
		
		belongInstance.forEach { instance ->
			instance.commonPartSlots.forEach { partBasicType ->
				if (partBasicType !in includeParts.map { it.basicType }) {
					includeParts = includeParts.append(
						moreParts.find { it.basicType == partBasicType } ?:
							error("$partBasicType not found in ${moreParts.toList()}")
					)
				}
			}
		}
		
		optionalParts = includeParts.filter { it.basicType == BasicPart.Optional::class }
		notOptionalParts = includeParts - optionalParts.toSet()
	}
	
	companion object {
		val classes = Gear::class.sealedSubclasses
		val instanceMap = classes.associateWith { RegistryHelper.registerGear(createInstance(it)) }
	}
	
	override fun toString(): String {
		return "${this.javaClass.simpleName}(" +
		       "baseAttackDamage=$baseAttackDamage, " +
		       "baseAttackSpeed=$baseAttackSpeed, " +
		       "effectiveBlocks=$effectiveBlocks, " +
		       "includeStats=$includeStats, " +
		       "includeParts=$includeParts, " +
		       "belongInstance=$belongInstance)"
	}
	
	override fun equals(other: Any?): Boolean {
		return other != null && other is Gear &&
		       other.baseAttackDamage == this.baseAttackDamage &&
		       other.baseAttackSpeed == this.baseAttackSpeed &&
		       other.effectiveBlocks == this.effectiveBlocks &&
		       other.includeStats == this.includeStats &&
		       other.includeParts == this.includeParts &&
		       other.belongInstance == this.belongInstance &&
		       other.className == this.className
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	open fun getPart(
		stack: ItemStack,
		part: KClass<out Part>,
	) = GearStack(stack, this).getPart(part)
	
	open fun getParts(
		stack: ItemStack,
	) = GearStack(stack, this).getParts(includeParts)
	
	open fun getRemainDurability(
		stack: ItemStack,
	) = GearStack(stack, this).remainDurability
	
	open fun getMaxDurability(
		stack: ItemStack,
	) = GearStack(stack, this).maxDurability
	
	open fun getRemainDurabilityRatio(
		stack: ItemStack,
	) = GearStack(stack, this).remainDurability
	
	open fun getEnchantability(
		stack: ItemStack,
	) = GearStack(stack, this).enchantability
	
	override fun isEnchantable(
		stack: ItemStack,
	) = GearStack(stack, this).isEnchantable
	
	open fun getTier(
		stack: ItemStack,
	) = GearStack(stack, this).miningTier
	
	override fun getMiningSpeedMultiplier(
		stack: ItemStack,
		state: BlockState,
	) = GearStack(stack, this).getMiningSpeed(state)
	
	open fun isBroken(
		stack: ItemStack,
	) = GearStack(stack, this).isBroken
	
	open fun isNotBroken(
		stack: ItemStack,
	) = GearStack(stack, this).isNotBroken
	
	override fun isSuitableFor(
		stack: ItemStack,
		state: BlockState,
	) = GearStack(stack, this).isSuitableFor(state)
	
	override fun getAttributeModifiers(
		stack: ItemStack,
		slot: EquipmentSlot,
	) = GearStack(stack, this).getAttributeModifiers(
		slot, ATTACK_DAMAGE_MODIFIER_ID, ATTACK_SPEED_MODIFIER_ID
	)
	
	override fun appendTooltip(
		stack: ItemStack,
		world: World?,
		tooltip: MutableList<Text>,
		context: TooltipContext,
	) = GearStack(stack, this).appendTooltip(world, tooltip, context)
	
	override fun canRepair(
		stack: ItemStack,
		ingredient: ItemStack,
	) = GearStack(stack, this).canRepair(ingredient)
	
	override fun getName(
		stack: ItemStack,
	): MutableText = GearStack(stack, this).name
	
	override fun postHit(
		stack: ItemStack,
		target: LivingEntity,
		attacker: LivingEntity,
	) = GearStack(stack, this).onAttack(target, attacker)
	
	override fun postMine(
		stack: ItemStack,
		world: World,
		state: BlockState,
		pos: BlockPos,
		miner: LivingEntity,
	) = GearStack(stack, this).onMine(world, state, pos, miner)
	
	override fun getItemBarStep(
		stack: ItemStack,
	) = GearStack(stack, this).itemBarStep
	
	override fun isItemBarVisible(
		stack: ItemStack,
	) = GearStack(stack, this).isItemBarVisible
	
	override fun getItemBarColor(
		stack: ItemStack,
	) = GearStack(stack, this).itemBarColor
	
	override fun onCraft(
		stack: ItemStack,
		world: World,
		player: PlayerEntity,
	) = GearStack(stack, this).onCraft(world, player)
}
