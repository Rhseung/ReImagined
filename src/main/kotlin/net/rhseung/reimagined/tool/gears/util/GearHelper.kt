package net.rhseung.reimagined.tool.gears.util

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.enchantment.UnbreakingEnchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.rhseung.reimagined.registration.ModBlockTags
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.base.BasicGearItem
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.gears.util.GearData.NBT_ROOT
import net.rhseung.reimagined.tool.gears.util.GearData.putPartIfMissing
import net.rhseung.reimagined.tool.gears.util.GearData.putStatIfMissing
import net.rhseung.reimagined.tool.parts.base.BasicPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Color
import net.rhseung.reimagined.utils.Color.Companion.gradient
import net.rhseung.reimagined.utils.Sound
import net.rhseung.reimagined.utils.Text.displayName
import net.rhseung.reimagined.utils.Text.getEnchantmentFullName
import net.rhseung.reimagined.utils.Text.pathName
import net.rhseung.reimagined.utils.Tooltip.coloring
import java.util.*
import java.util.function.Consumer
import kotlin.math.roundToInt

object GearHelper {
	private const val BROKEN_MINING_SPEED = 0.0F
	private const val BROKEN_ATTACK_DAMAGE = 0.0F
	
	private val REACH_DISTANCE_MODIFIER_ID: UUID = UUID.fromString("5879BECA-71AF-4D6A-9B2D-B59EF091B395")
	private val MINING_SPEED_MODIFIER_ID: UUID = UUID.fromString("A0B5EBF0-D474-48CF-9997-54A21029F2D3")
	
	private const val TOOL_MODIFIER_NAME = "Tool modifier"
	private const val WEAPON_MODIFIER_NAME = "Weapon modifier"
	
	/**
	 * gets the NBT data
	 */
	fun getStat(
		stack: ItemStack,
		stat: Stat,
	): Float {
		val statCompound = GearData.getData(stack, GearData.NBT_ROOT_STATS)
		
		if (statCompound.contains(stat.name.pathName())) {
			return statCompound.getFloat(stat.name.pathName())
		} else {
			putStatIfMissing(stack, stat)
		}
		
		return stat.defaultValue
	}
	
	fun getPart(
		stack: ItemStack,
		partType: PartType,
		partCompoundInput: NbtCompound? = null,
	): BasicPartItem {
		val partCompound = partCompoundInput ?: GearData.getData(stack, GearData.NBT_ROOT_PARTS)
		
		if (partCompound.contains(partType.name.pathName())) {
			val materialName = partCompound.getString(partType.name.pathName())
			return ModItems.PARTS[partType]!!.find { it.material.name.pathName() == materialName }!!
		} else {
			putPartIfMissing(stack, partType)
		}
		
		return ModItems.PARTS[partType]!![0] // DUMMY is 0th index
	}
	
	fun getParts(
		stack: ItemStack,
		includeParts: List<PartType>,
	): Map<PartType, BasicPartItem> {
		val parts = mutableMapOf<PartType, BasicPartItem>()
		val partCompound = GearData.getData(stack, GearData.NBT_ROOT_PARTS)
		
		for (partType in includeParts) {
			parts[partType] = getPart(stack, partType, partCompound)
		}
		
		return parts
	}
	
	/**
	 * gets the gear stats
	 */
	fun getMiningTier(
		stack: ItemStack,
	): Int {
		return getStat(stack, Stat.MINING_TIER).toInt()
	}
	
	fun getMiningSpeed(
		stack: ItemStack,
		state: BlockState? = null,
		effectiveBlocks: TagKey<Block>? = null,
		getMax: Boolean = false,
	): Float {
		return if (getMax) {
			// getMiningSpeed(stack, getMax = true)
			getStat(stack, Stat.MINING_SPEED)
		}
		else {
			// getMiningSpeed(stack, state, effectiveBlocks)
			if (isBroken(stack)) BROKEN_MINING_SPEED
			else if (state!!.isIn(effectiveBlocks!!)) getStat(stack, Stat.MINING_SPEED)
			else 1.0F
		}
	}
	
	fun getAttackDamage(
		stack: ItemStack,
	): Float {
		return if (isBroken(stack)) BROKEN_ATTACK_DAMAGE
		else getStat(stack, Stat.ATTACK_DAMAGE)
	}
	
	fun getAttackSpeed(
		stack: ItemStack,
	): Float {
		return getStat(stack, Stat.ATTACK_SPEED)
	}
	
	fun getEnchantability(
		stack: ItemStack,
	): Int {
		return getStat(stack, Stat.ENCHANTABILITY).toInt()
	}
	
	fun getUsedDurability(
		stack: ItemStack,
	): Int {
		return stack.damage
	}
	
	fun getRemainDurability(
		stack: ItemStack,
	): Int {
		return getMaxDurability(stack) - getUsedDurability(stack)
	}
	
	fun getMaxDurability(
		stack: ItemStack,
	): Int {
		return getStat(stack, Stat.DURABILITY).toInt() - 1
	}
	
	fun getRemainDurabilityRatio(
		stack: ItemStack,
	): Float {
		return getRemainDurability(stack).toFloat() / getMaxDurability(stack).toFloat()
	}
	
	fun getUsedDurabilityRatio(
		stack: ItemStack,
	): Float {
		return getUsedDurability(stack).toFloat() / getMaxDurability(stack).toFloat()
	}
	
	/**
	 * gets the other things
	 */
	fun getAttributeModifiers(
		stack: ItemStack,
		slot: EquipmentSlot,
		attackDamageModifierId: UUID,
		attackSpeedModifierId: UUID,
		type: GearType?,
	): Multimap<EntityAttribute, EntityAttributeModifier> {
		val builder = ImmutableMultimap.builder<EntityAttribute, EntityAttributeModifier>()
		
		if (isNotBroken(stack) && slot == EquipmentSlot.MAINHAND && type != null) {
			builder.put(
				EntityAttributes.GENERIC_ATTACK_DAMAGE, EntityAttributeModifier(
					attackDamageModifierId,
					TOOL_MODIFIER_NAME,
					getAttackDamage(stack).toDouble(),
					EntityAttributeModifier.Operation.ADDITION
				)
			)
			
			builder.put(
				EntityAttributes.GENERIC_ATTACK_SPEED, EntityAttributeModifier(
					attackSpeedModifierId,
					TOOL_MODIFIER_NAME,
					type.baseAttackSpeed + getAttackSpeed(stack).toDouble() - 4.0,
					EntityAttributeModifier.Operation.ADDITION
				)
			)
			
			// todofar: reach distance attribute도 추가하기
			// todofar: WEAPON_MODIFIER_NAME 사용
			// todo: how to make custom attribute?
		}
		
		return builder.build()
	}
	
	fun register(
		id: String,
		attribute: EntityAttribute,
	): EntityAttribute {
		return Registry.register(Registries.ATTRIBUTE, id, attribute) as EntityAttribute
	}
	
	fun getName(stack: ItemStack, type: GearType?): Text {
		if (type == null) return Text.literal("unknown")
		return Text.translatable(stack.item.getTranslationKey(stack), getPart(stack, PartType.HEAD(type)).material.name.displayName())
	}
	
	/**
	 * processing functions
	 */
	fun damage(
		stack: ItemStack,
		amountInput: Int,
		random: Random,
		player: ServerPlayerEntity?,
	): Boolean {
		var amount = amountInput
		val unbreakingLevel = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack)
		
		if (amount > 0) {
			var reducedAmount = 0
			
			if (unbreakingLevel > 0) {
				for (i in 0 until amount) {
					if (UnbreakingEnchantment.shouldPreventDamage(stack, unbreakingLevel, random))
						reducedAmount++
				}
			}
			
			amount -= reducedAmount
			if (amount <= 0) return false
		}
		
		if (player != null && amount != 0) {
			Criteria.ITEM_DURABILITY_CHANGED.trigger(
				player,
				stack,
				getUsedDurability(stack) + amount
			)
		}
		
		val newUsedDurability = getUsedDurability(stack) + amount
		stack.damage = newUsedDurability
		
		return newUsedDurability >= getMaxDurability(stack)
	}
	
	fun <T : LivingEntity> damage(
		stack: ItemStack,
		amount: Int,
		entity: T,
		breakCallback: Consumer<T>,
	) {
		if (!entity.world.isClient && (entity !is PlayerEntity || !(entity as PlayerEntity).abilities.creativeMode)) {
			if (damage(stack, amount, entity.random, if (entity is ServerPlayerEntity) entity else null)) {
				breakCallback.accept(entity)
				onBroken(stack, entity)
			}
		}
	}
	
	fun postProcessNbt(
		nbt: NbtCompound,
		includeStats: Set<Stat>,
		includeParts: List<PartType>,
	) {
		val root = nbt.getCompound(NBT_ROOT)
		
		includeStats.forEach { stat -> putStatIfMissing(root, stat) }
		includeParts.forEach { part -> putPartIfMissing(root, part) }
	}
	
	fun appendTooltip(
		stack: ItemStack,
		world: World?,
		tooltip: MutableList<Text>,
		context: TooltipContext,
		includeStats: Set<Stat>,
	) {
		stack.addHideFlag(ItemStack.TooltipSection.MODIFIERS)
		stack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS)
		
		val enchantments = EnchantmentHelper.get(stack)
		
		if (enchantments.isNotEmpty()) {
			for ((enchantment, level) in enchantments) {
				tooltip.add(
					"  {-} {${getEnchantmentFullName(enchantment, level)}}".coloring(
						Color.GRAY,
						if (enchantment.isCursed) Color.DARK_RED
						else if (enchantment.isTreasure) Color.DARK_PINK
						else Color.DARK_GRAY
					)
				)
			}
		}
		// todo: Mining Tier는 알파벳으로 써주는게 좋음
		// todo: translatable text
	}
	
	/**
	 * on(event) functions
	 */
	fun onAttack(
		stack: ItemStack,
		target: LivingEntity,
		attacker: LivingEntity,
	): Boolean {
		if (isNotBroken(stack)) {
			damage(stack, 1, attacker) { e ->
				e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
			}
			if (isBroken(stack)) {
				onBroken(stack, attacker)
			}
		}
		
		return true
	}
	
	fun onMine(
		stack: ItemStack,
		world: World,
		state: BlockState,
		pos: BlockPos,
		miner: LivingEntity,
	): Boolean {
		if (isNotBroken(stack)) {
			if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
				damage(stack, 1, miner) { e ->
					e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
				}
				if (isBroken(stack)) {
					onBroken(stack, miner)
				}
			}
		}
		
		return true
	}
	
	fun onBroken(
		stack: ItemStack,
		player: LivingEntity,
	) {
		// note: item break sound
		//Sound.play(player, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS)
		
		// todo: item break particle
		// player.world.addParticle(ItemStackParticleEffect(ParticleTypes.ITEM, stack), player.x, player.y, player)
		
		player.sendToolBreakStatus(Hand.MAIN_HAND)  // 여기에 particle, sound 다 있음
		// todo: particle tint (custom particle class)
		//  - sendToolBreakStatus()에 particle 관련 코드가 어딨는지 찾아야함
	}
	
	fun onCraft(
		stack: ItemStack,
		world: World,
		player: PlayerEntity,
		needParts: Set<PartType> = emptySet(),
		needStats: Set<Stat> = emptySet(),
	) {
		Sound.play(player, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS)
		
		needParts.forEach { putPartIfMissing(stack, it) }
		needStats.forEach { putStatIfMissing(stack, it) }
	}
	
	/**
	 * is(Boolean) functions
	 */
	fun canRepair(
		stack: ItemStack,
		ingredient: ItemStack,
		includeParts: List<PartType>,
		gearType: GearType?,
	): Boolean {
		if (gearType == null) return false
		return getPart(stack, PartType.PICKAXE_HEAD).material.repairIngredient.test(ingredient)
	}
	
	fun isSuitableFor(
		stack: ItemStack,
		state: BlockState,
		effectiveBlocks: TagKey<Block>?,
	): Boolean {
		if (effectiveBlocks == null) return false
		
		val tier = getMiningTier(stack)
		
		for (i in Material.MAX_TIER downTo 1)
			if (tier < i && state.isIn(ModBlockTags.getTag(i))) return false
		return state.isIn(effectiveBlocks)
	}
	
	@JvmStatic
	fun isBroken(
		stack: ItemStack,
	): Boolean {
		return getRemainDurability(stack) <= 0
	}
	
	@JvmStatic
	fun isNotBroken(
		stack: ItemStack,
	): Boolean {
		return !isBroken(stack)
	}
	
	@JvmStatic
	fun isGear(
		stack: ItemStack,
	): Boolean {
		return stack.item is BasicGearItem
	}
	
	fun isNotGear(
		stack: ItemStack,
	): Boolean {
		return !isGear(stack)
	}
	
	fun isEnchantable(
		stack: ItemStack,
	): Boolean {
		return isNotBroken(stack)
	}
	
	/**
	 * item bar functions
	 */
	fun getItemBarStep(
		stack: ItemStack,
	): Int {
		return (13.0F - 13.0F * getUsedDurabilityRatio(stack)).roundToInt()
	}
	
	fun getItemBarColor(
		stack: ItemStack,
	): Int {
		// todofar: unique bar color?
		return Pair(Color.GREEN, Color.RED).gradient(getRemainDurabilityRatio(stack)).toHex()
	}
	
	fun isItemBarVisible(
		stack: ItemStack,
	): Boolean {
		return getUsedDurability(stack) > 0
	}
}