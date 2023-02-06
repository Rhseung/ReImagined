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
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.rhseung.reimagined.registration.ModBlockTags
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.gears.util.GearData.NBT_ROOT
import net.rhseung.reimagined.tool.gears.util.GearData.putPartIfMissing
import net.rhseung.reimagined.tool.gears.util.GearData.putStatIfMissing
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Color
import net.rhseung.reimagined.utils.Name.toPathName
import net.rhseung.reimagined.utils.Sound
import net.rhseung.reimagined.utils.Tooltip.textf
import java.util.*
import java.util.function.Consumer
import kotlin.math.roundToInt

object GearHelper {
	private const val BROKEN_MINING_SPEED = 0.0F
	private const val BROKEN_ATTACK_DAMAGE = 0.0F
	
//	private val ATTACK_DAMAGE_MODIFIER_ID: UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF")
//	private val ATTACK_SPEED_MODIFIER_ID: UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3")
	private val REACH_DISTANCE_MODIFIER_ID: UUID = UUID.fromString("5879BECA-71AF-4D6A-9B2D-B59EF091B395")
	
	private const val TOOL_MODIFIER_NAME = "Tool modifier"
	private const val WEAPON_MODIFIER_NAME = "Weapon modifier"
	
	/**
	 * gets the NBT data
	 */
	fun getStat(
		stack: ItemStack,
		stat: Stat
	): Float {
		val statCompound = GearData.getData(stack, GearData.NBT_ROOT_STATS)
		
		if (statCompound.contains(stat.name.toPathName())) {
			return statCompound.getFloat(stat.name.toPathName())
		} else {
			putStatIfMissing(stack, stat)
		}
		
		return stat.defaultValue
	}
	
	fun getPart(
		stack: ItemStack,
		partType: PartType,
		partCompoundInput: NbtCompound? = null
	): IPartItem {
		val partCompound = partCompoundInput ?: GearData.getData(stack, GearData.NBT_ROOT_PARTS)
		
		if (partCompound.contains(partType.name.toPathName())) {
			val materialName = partCompound.getString(partType.name.toPathName())
			return ModItems.PARTS[partType]!!.find { it.material.name.toPathName() == materialName }!!
		} else {
			putPartIfMissing(stack, partType)
		}
		
		return ModItems.PARTS[partType]!![0] // DUMMY is 0th index
	}
	
	fun getParts(
		stack: ItemStack,
		includeParts: List<PartType>
	): Map<PartType, IPartItem> {
		val parts = mutableMapOf<PartType, IPartItem>()
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
		stack: ItemStack
	): Int {
		return getStat(stack, Stat.MINING_TIER).toInt()
	}
	
	fun getMiningSpeed(
		stack: ItemStack,
		state: BlockState,
		effectiveBlocks: TagKey<Block>
	): Float {
		return if (isBroken(stack)) BROKEN_MINING_SPEED
		else if (state.isIn(effectiveBlocks)) getStat(stack, Stat.MINING_SPEED)
		else 1.0F
	}
	
	fun getAttackDamage(
		stack: ItemStack
	): Float {
		return if (isBroken(stack)) BROKEN_ATTACK_DAMAGE
		else getStat(stack, Stat.ATTACK_DAMAGE)
	}
	
	fun getAttackSpeed(
		stack: ItemStack
	): Float {
		return getStat(stack, Stat.ATTACK_SPEED)
	}
	
	fun getEnchantability(
		stack: ItemStack
	): Int {
		return getStat(stack, Stat.ENCHANTABILITY).toInt()
	}
	
	fun getUsedDurability(
		stack: ItemStack
	): Int {
		return stack.damage
	}
	
	fun getRemainDurability(
		stack: ItemStack
	): Int {
		return getMaxDurability(stack) - getUsedDurability(stack)
	}
	
	fun getMaxDurability(
		stack: ItemStack
	): Int {
		return getStat(stack, Stat.DURABILITY).toInt() - 1
	}
	
	fun getRemainDurabilityRatio(
		stack: ItemStack
	): Float {
		return getRemainDurability(stack).toFloat() / getMaxDurability(stack).toFloat()
	}
	
	fun getUsedDurabilityRatio(
		stack: ItemStack
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
		attackSpeedModifierId: UUID
	): Multimap<EntityAttribute, EntityAttributeModifier> {
		val builder = ImmutableMultimap.builder<EntityAttribute, EntityAttributeModifier>()
		
		if (isNotBroken(stack) && slot == EquipmentSlot.MAINHAND) {
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
					1.2 + getAttackSpeed(stack).toDouble() - 4.0,   // note: 1.2 = 곡괭이의 기본 속도
					EntityAttributeModifier.Operation.ADDITION
				)
			)
			
			// todofar: reach distance attribute도 추가하기
			// todofar: WEAPON_MODIFIER_NAME 사용
		}
		
		return builder.build()
	}
	
	fun getName(stack: ItemStack): Text {
		return Text.translatable(stack.item.getTranslationKey(stack))
	}
	
	/**
	 * processing functions
	 */
	fun damage(
		stack: ItemStack,
		amountInput: Int,
		random: Random,
		player: ServerPlayerEntity?
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
		breakCallback: Consumer<T>
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
		includeStats: List<Stat>,
		includeParts: List<PartType>
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
		includeStats: List<Stat>
	) {
		tooltip.add(
			textf(
				"{Durability:} {${getRemainDurability(stack)}}{/}{${getMaxDurability(stack)}}",
				Color.GRAY,
				Color.DARK_GREEN.gradient(Color.DARK_RED, getRemainDurabilityRatio(stack)),
				Color.DARK_GRAY,
				Color.DARK_GREEN
			)
		)
		for (stat in includeStats) {
			if (stat == Stat.DURABILITY) continue
			tooltip.add(stat.getDisplayText(stack))
		}
		// todofar: Mining Tier는 알파벳으로 써주는게 좋음
		// todofar: translatable text
	}
	
	/**
	 * on(event) functions
	 */
	fun onAttack(
		stack: ItemStack,
		target: LivingEntity,
		attacker: LivingEntity
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
		miner: LivingEntity
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
		player: LivingEntity
	) {
		Sound.play(player, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS)
		// todofar: 부서질 때 파티클까지 튀기 (안해도 됨)
	}
	
	fun onCraft(
		stack: ItemStack,
		world: World,
		player: PlayerEntity,
		needParts: List<PartType> = emptyList(),
		needStats: List<Stat> = emptyList()
	) {
		// note: 제작 시 `player.world`로 제작 효과음 재생할 수 있음
		Sound.play(player, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.PLAYERS)
		
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
		gearType: GearType
	): Boolean {
		return if (isNotGear(stack)) false
		else getParts(stack, includeParts)[PartType.HEAD(gearType)]!!.material.repairIngredient.test(ingredient)
	}
	
	fun isSuitableFor(
		stack: ItemStack,
		state: BlockState,
		effectiveBlocks: TagKey<Block>
	): Boolean {
		val tier = getMiningTier(stack)
		
		for (i in Material.MAX_TIER downTo 1)
			if (tier < i && state.isIn(ModBlockTags.getTag(i))) return false
		return state.isIn(effectiveBlocks)
	}
	
	@JvmStatic
	fun isBroken(
		stack: ItemStack
	): Boolean {
		return getRemainDurability(stack) <= 0
	}
	
	@JvmStatic
	fun isNotBroken(
		stack: ItemStack
	): Boolean {
		return !isBroken(stack)
	}
	
	@JvmStatic
	fun isGear(
		stack: ItemStack
	): Boolean {
		return stack.item is IGearItem
	}
	
	fun isNotGear(
		stack: ItemStack
	): Boolean {
		return !isGear(stack)
	}
	
	fun isEnchantable(
		stack: ItemStack
	): Boolean {
		return isNotBroken(stack)
	}
	
	/**
	 * item bar functions
	 */
	fun getItemBarStep(
		stack: ItemStack
	): Int {
		return (13.0F - 13.0F * getUsedDurabilityRatio(stack)).roundToInt()
	}
	
	fun getItemBarColor(
		stack: ItemStack
	): Int {
		// todofar: unique bar color?
		return Color.GREEN.gradient(Color.RED, getRemainDurabilityRatio(stack)).toHex()
	}
	
	fun isItemBarVisible(
		stack: ItemStack
	): Boolean {
		return getUsedDurability(stack) > 0
	}
}