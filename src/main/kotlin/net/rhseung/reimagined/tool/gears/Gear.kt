package net.rhseung.reimagined.tool.gears

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Vanishable
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.rhseung.reimagined.registration.ModBlockTags
import net.rhseung.reimagined.registration.RegistryHelper
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.parts.BasicPart
import net.rhseung.reimagined.tool.parts.Part
import net.rhseung.reimagined.utils.Bunch
import net.rhseung.reimagined.utils.Bunch.Companion.with
import net.rhseung.reimagined.utils.Utils.append
import net.rhseung.reimagined.utils.Utils.createInstance
import kotlin.reflect.KClass

sealed class Gear constructor(
	val baseAttackDamage: Double,
	val baseAttackSpeed: Double,
	val effectiveBlocks: TagKey<Block>,
	vararg moreParts: Bunch<KClass<out BasicPart>, KClass<out Part>>,
) : Item(Settings().maxCount(1)), Vanishable {
	var includeStats: Set<Stat> = setOf()
	var includeParts: List<Bunch<KClass<out BasicPart>, KClass<out Part>>> = listOf()
	var belongInstance: List<BasicGear> = listOf()
	
	val className: String = this.javaClass.simpleName
	
	init {
		this.javaClass.annotations.forEach { annotation ->
			when (annotation) {
				is BelongTo -> {
					annotation.value.forEach {
						val instance = createInstance(it)
						belongInstance += instance
						includeStats += instance.commonStats
					}
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
	
	@BelongTo(BasicGear.MiningTool::class)
	class Pickaxe : Gear(
		baseAttackDamage = 1.0,
		baseAttackSpeed = 1.2,
		effectiveBlocks = BlockTags.PICKAXE_MINEABLE,
		
		BasicPart.Head::class with Part.PickaxeHead::class,
		BasicPart.Binding::class with Part.Binding::class,
		BasicPart.Handle::class with Part.Handle::class
	) {}
	
	@BelongTo(BasicGear.MiningTool::class, BasicGear.MeleeWeapon::class)
	class Axe : Gear(
		baseAttackDamage = 5.0,
		baseAttackSpeed = 0.9,
		effectiveBlocks = BlockTags.AXE_MINEABLE,
		
		BasicPart.Head::class with Part.AxeHead::class,
		BasicPart.Binding::class with Part.Binding::class,
		BasicPart.Handle::class with Part.Handle::class
	) {}
	
	@BelongTo(BasicGear.MiningTool::class)
	class Shovel : Gear(
		baseAttackDamage = 1.5,
		baseAttackSpeed = 1.0,
		effectiveBlocks = BlockTags.SHOVEL_MINEABLE,
		
		BasicPart.Head::class with Part.ShovelHead::class,
		BasicPart.Binding::class with Part.Binding::class,
		BasicPart.Handle::class with Part.Handle::class
	) {}
	
	@BelongTo(BasicGear.MiningTool::class)
	class Hoe : Gear(
		baseAttackDamage = 1.0,
		baseAttackSpeed = 1.4,
		effectiveBlocks = BlockTags.HOE_MINEABLE,
		
		BasicPart.Head::class with Part.HoeHead::class,
		BasicPart.Binding::class with Part.Binding::class,
		BasicPart.Handle::class with Part.Handle::class
	) {}
	
	@BelongTo(BasicGear.MeleeWeapon::class)
	class Sword : Gear(
		baseAttackDamage = 3.0,
		baseAttackSpeed = 1.6,
		effectiveBlocks = ModBlockTags.SWORD_MINEABLE,
		
		BasicPart.Head::class with Part.Blade::class,
		BasicPart.Binding::class with Part.Guard::class,
		BasicPart.Handle::class with Part.Handle::class
	) {}
	
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
