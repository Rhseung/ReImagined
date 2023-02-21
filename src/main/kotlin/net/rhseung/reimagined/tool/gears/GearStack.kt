package net.rhseung.reimagined.tool.gears

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.BlockState
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.enchantment.UnbreakingEnchantment
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.Ingredient
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
import net.rhseung.reimagined.tool.gears.GearData.NBT_ROOT
import net.rhseung.reimagined.tool.gears.GearData.putPartIfMissing
import net.rhseung.reimagined.tool.gears.GearData.putStatIfMissing
import net.rhseung.reimagined.tool.parts.BasicPart
import net.rhseung.reimagined.tool.parts.Part
import net.rhseung.reimagined.utils.Bunch
import net.rhseung.reimagined.utils.Bunch.Companion.with
import net.rhseung.reimagined.utils.Color
import net.rhseung.reimagined.utils.Color.Companion.gradient
import net.rhseung.reimagined.utils.Math.pow
import net.rhseung.reimagined.utils.Sound
import net.rhseung.reimagined.utils.Text.displayName
import net.rhseung.reimagined.utils.Text.getEnchantmentFullName
import net.rhseung.reimagined.utils.Text.pathName
import net.rhseung.reimagined.utils.Tooltip.coloring
import net.rhseung.reimagined.utils.Utils.append
import net.rhseung.reimagined.utils.Utils.getClassName
import java.util.*
import java.util.function.Consumer
import kotlin.math.roundToInt
import kotlin.reflect.KClass

class GearStack(
	val stack: ItemStack,
	val gear: Gear,
) {
	companion object {
		private const val BROKEN_MINING_SPEED = 0.0F
		private const val BROKEN_ATTACK_DAMAGE = 0.0F
		
		private val REACH_DISTANCE_MODIFIER_ID: UUID = UUID.fromString("5879BECA-71AF-4D6A-9B2D-B59EF091B395")
		private val MINING_SPEED_MODIFIER_ID: UUID = UUID.fromString("A0B5EBF0-D474-48CF-9997-54A21029F2D3")
	}
	
	/**
	 * gets the gear stats
	 */
	val miningTier = getStat(Stat.MINING_TIER).toInt()
	
	val miningTierMaterial = Material.getMaterialFromMiningLevel(miningTier)
	
	val usedDurability = stack.damage
	
	val maxDurability = getStat(Stat.DURABILITY).toInt() - 1
	
	val remainDurability = maxDurability - usedDurability
	
	val remainDurabilityRatio = remainDurability.toFloat() / maxDurability.toFloat()
	
	val usedDurabilityRatio = usedDurability.toFloat() / maxDurability.toFloat()
	
	val isBroken = remainDurability <= 0
	
	val isNotBroken = !isBroken
	
	val isGear = stack.item is Gear
	
	val isNotGear = !isGear
	
	val isEnchantable = isNotBroken
	
	/**
	 * item bar functions
	 */
	val itemBarStep = (13.0F - 13.0F * usedDurabilityRatio).roundToInt()
	
	val itemBarColor = Pair(Color.GREEN, Color.RED).gradient(remainDurabilityRatio).toHex()
	
	val isItemBarVisible = remainDurability in 1 until maxDurability
	
	val name = Text.translatable(
		stack.item.getTranslationKey(stack),
		getPart(getPartTypeFromBasicPartType(BasicPart.Head::class)).material.name.displayName()
	)
	
	val attackDamage = if (isBroken) BROKEN_ATTACK_DAMAGE
	else (getStat(Stat.ATTACK_DAMAGE).toDouble() + gear.baseAttackDamage).toFloat()
	
	val displayAttackDamage = (getStat(
		Stat.ATTACK_DAMAGE
	).toDouble() + gear.baseAttackDamage).toFloat() + 1.0F + EnchantmentHelper.getAttackDamage(
		stack, EntityGroup.DEFAULT
	)
	
	val displayMiningSpeed = getStat(Stat.MINING_SPEED) + if (EnchantmentHelper.getLevel(
			Enchantments.EFFICIENCY, stack
		) > 0
	) EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack).pow(2) + 1 else 0
	
	val attackSpeed = (getStat(Stat.ATTACK_SPEED).toDouble() + gear.baseAttackSpeed).toFloat()
	
	val enchantability = getStat(Stat.ENCHANTABILITY).toInt()
	
	/**
	 * gets the other things
	 */
	fun getAttributeModifiers(
		slot: EquipmentSlot,
		attackDamageModifierId: UUID,
		attackSpeedModifierId: UUID,
	): Multimap<EntityAttribute, EntityAttributeModifier> {
		val builder = ImmutableMultimap.builder<EntityAttribute, EntityAttributeModifier>()
		
		if (gear.belongInstance.first().modifierName.isBlank())
			error("${gear.belongInstance} has blank modifier name")
		
		if (slot == EquipmentSlot.MAINHAND) {
			if (isNotBroken) {
				builder.put(
					EntityAttributes.GENERIC_ATTACK_DAMAGE, EntityAttributeModifier(
						attackDamageModifierId, gear.belongInstance.first().modifierName, attackDamage.toDouble(),
						EntityAttributeModifier.Operation.ADDITION
					)
				)
				
				builder.put(
					EntityAttributes.GENERIC_ATTACK_SPEED, EntityAttributeModifier(
						attackSpeedModifierId, gear.belongInstance.first().modifierName, attackSpeed.toDouble() - 4.0,
						EntityAttributeModifier.Operation.ADDITION
					)
				)
				
				// todofar: reach distance attribute도 추가하기
				// note: how to make custom attribute?
			}
		}
		
		return builder.build()
	}
	
	/**
	 * processing functions
	 */
	fun damage(
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
				player, stack, usedDurability + amount
			)
		}
		
		val newUsedDurability = usedDurability + amount
		stack.damage = newUsedDurability
		
		return newUsedDurability >= maxDurability
	}
	
	fun <T : LivingEntity> damage(
		amount: Int,
		entity: T,
		breakCallback: Consumer<T>,
	) {
		if (!entity.world.isClient && (entity !is PlayerEntity || !(entity as PlayerEntity).abilities.creativeMode)) {
			if (damage(amount, entity.random, if (entity is ServerPlayerEntity) entity else null)) {
				breakCallback.accept(entity)
				onBroken(entity)
			}
		}
	}
	
	fun postProcessNbt(
		nbt: NbtCompound,
	) {
		val root = nbt.getCompound(NBT_ROOT)
		
		gear.includeStats.forEach { stat -> putStatIfMissing(root, stat) }
		gear.includeParts.forEach { part -> putPartIfMissing(root, part.element) }
	}
	
	fun appendTooltip(
		world: World?,
		tooltip: MutableList<Text>,
		context: TooltipContext,
	) {        // todo: component보다 위에 하나(constructions), 밑에 하나(stats, press shift)
		stack.addHideFlag(ItemStack.TooltipSection.MODIFIERS)
		stack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS)
		
		val enchantments = EnchantmentHelper.get(stack)
		
		if (enchantments.isNotEmpty() && Screen.hasShiftDown()) {
			for ((enchantment, level) in enchantments) {
				tooltip.add(
					"  {-} {${getEnchantmentFullName(enchantment, level)}}".coloring(
						Color.GRAY, if (enchantment.isCursed) Color.DARK_RED
						else if (enchantment.isTreasure) Color.DARK_PINK
						else Color.DARK_GRAY
					)
				)
			}
		}        // todo: Mining Tier는 알파벳으로 써주는게 좋음
	}
	
	/**
	 * on(event) functions
	 */
	fun onAttack(
		target: LivingEntity,
		attacker: LivingEntity,
	): Boolean {
		if (isNotBroken) {
			damage(1, attacker) { e ->
				e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
			}
			if (isBroken) {
				onBroken(attacker)
			}
		}
		
		return true
	}
	
	fun onMine(
		world: World,
		state: BlockState,
		pos: BlockPos,
		miner: LivingEntity,
	): Boolean {
		if (isNotBroken) {
			if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
				damage(1, miner) { e ->
					e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
				}
				if (isBroken) {
					onBroken(miner)
				}
			}
		}
		
		return true
	}
	
	fun onBroken(
		player: LivingEntity,
	) {        // note: item break sound
		//Sound.play(player, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS)
		
		// todo: item break particle
		// player.world.addParticle(ItemStackParticleEffect(ParticleTypes.ITEM, stack), player.x, player.y, player)
		
		player.sendToolBreakStatus(Hand.MAIN_HAND)  // 여기에 particle, sound 다 있음
		// todo: particle tint (custom particle class)
		//  - sendToolBreakStatus()에 particle 관련 코드가 어딨는지 찾아야함
	}
	
	fun onCraft(
		world: World,
		player: PlayerEntity,
	) {
		Sound.play(player, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS)
	}
	
	/**
	 * is(Boolean) functions
	 */
	fun canRepair(
		ingredient: ItemStack,
	): Boolean {
		val repairIngredient = getPart(getPartTypeFromBasicPartType(BasicPart.Head::class)).material.ingredient
		
		return if (repairIngredient == null) false
		else Ingredient.ofItems(repairIngredient).test(ingredient)
	}
	
	fun isSuitableFor(
		state: BlockState,
	): Boolean {
		val maxTier = Material.getValues().maxOf { it.tier }
		
		for (i in maxTier downTo 1) // STONE(1) 까지임, WOOD(0) 는 손과 같아서 따로 태그도 없음
			if (miningTier < i && state.isIn(ModBlockTags.getTag(i))) return false
		return state.isIn(gear.effectiveBlocks)
	}
	
	/**
	 * gets the NBT data
	 */
	fun getStat(
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
	
	fun getPartTypeFromBasicPartType(
		type: KClass<out BasicPart>,
	) = gear.includeParts.find { it.basicType == type }?.element
	    ?: error("$type not found in ${gear.includeParts.map { it.basicType }}")
	
	fun getPart(
		part: KClass<out Part>,
		partCompoundInput: NbtCompound? = null,
	): Part {
		val partCompound = partCompoundInput ?: GearData.getData(stack, GearData.NBT_ROOT_PARTS)
		
		if (partCompound.contains(getClassName(part).pathName())) {
			val materialName = partCompound.getString(getClassName(part).pathName())
			return Part.instanceMap[part]!!.find { it.material.name.pathName() == materialName }!!
		} else {
			putPartIfMissing(stack, part)
		}
		
		return Part.instanceMap[part]!![0] // DUMMY is 0th index
	}
	
	fun getParts(
		slotAndType: List<Bunch<KClass<out BasicPart>, KClass<out Part>>>,
	): List<Bunch<KClass<out BasicPart>, Part>> {
		var parts = listOf<Bunch<KClass<out BasicPart>, Part>>()
		val partCompound = GearData.getData(stack, GearData.NBT_ROOT_PARTS)
		
		for ((slot, type) in slotAndType) {
			parts = parts.append(slot with getPart(type, partCompound))
		}
		
		return parts
	}
	
	fun getMiningSpeed(
		state: BlockState? = null,
		value: Float = getStat(Stat.MINING_SPEED),
	): Float {
		return if (isBroken) BROKEN_MINING_SPEED
		else if (state!!.isIn(gear.effectiveBlocks)) value
		else 1.0F
	}
}