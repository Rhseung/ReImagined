package net.rhseung.reimagined.tool.gears.util

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.registration.ModBlockTags
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.gears.util.GearData.NBT_ROOT
import net.rhseung.reimagined.tool.gears.util.GearData.NBT_ROOT_CONSTRUCTION
import net.rhseung.reimagined.tool.gears.util.GearData.getData
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

object GearHelper {
	val BROKEN_MINING_SPEED = 0.0F
	val BROKEN_ATTACK_DAMAGE = 0.0F
	
	val ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF")
	val ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3")
	val REACH_DISTANCE_MODIFIER_ID = UUID.fromString("5879BECA-71AF-4D6A-9B2D-B59EF091B395")
	
	val TOOL_MODIFIER_NAME = "Tool modifier"
	val WEAPON_MODIFIER_NAME = "Weapon modifier"
	
	fun getStat(
		stack: ItemStack,
		stat: Stat
	): Float {
		val statCompound = GearData.getData(stack, GearData.NBT_ROOT_STATS)
		
		if (statCompound.contains(stat.name.toPathName())) {
			return statCompound.getFloat(stat.name.toPathName())
		} else {
			GearData.putStatIfMissing(stack, stat)
		}
		
		return stat.defaultValue
	}
	
	fun getConstructions(
		stack: ItemStack,
		includeParts: List<PartType>
	): Map<PartType, IPartItem> {
		val constructions = mutableMapOf<PartType, IPartItem>()
		val constructionCompound = GearData.getData(stack, GearData.NBT_ROOT_CONSTRUCTION)
		
		for (partType in includeParts) {
			if (!constructionCompound.contains(partType.name.toPathName())) {
				GearData.putPartIfMissing(stack, partType)
			}
			
			// note: NBT 구조
			//  "parts": {
			//      "pickaxe_head": "wood",
			//      "binding": "copper",
			//      "handle": "iron"
			//  }
			val part = ModItems.PARTS[partType]!!.find {
				ReImagined.LOGGER.debug("constructionCompound.getString(partType.name.toPathName()) = ${constructionCompound.getString(partType.name.toPathName())}")
				it.material.name.toPathName() == constructionCompound.getString(partType.name.toPathName())
			}
			
			// bug: part가 null이래
			constructions[partType] = part?: ModItems.PARTS[partType]!![0]
		}
		
		return constructions
	}
	
	fun getMiningTier(stack: ItemStack): Int {
		return getStat(stack, Stat.MINING_TIER).toInt()
	}
	
	fun getMiningSpeed(
		stack: ItemStack,
		state: BlockState,
		effectiveBlocks: TagKey<Block>
	): Float {
		return if (broken(stack)) BROKEN_MINING_SPEED
		else if (state.isIn(effectiveBlocks)) getStat(stack, Stat.MINING_SPEED).toFloat()
		else 1.0F
	}
	
	fun getAttackDamage(
		stack: ItemStack
	): Float {
		return if (broken(stack)) BROKEN_ATTACK_DAMAGE
		else getStat(stack, Stat.ATTACK_DAMAGE).toFloat()
	}
	
	fun getAttackSpeed(
		stack: ItemStack
	): Float {
		return getStat(stack, Stat.ATTACK_SPEED).toFloat()
	}
	
	fun getEnchantability(stack: ItemStack): Int {
		return getStat(stack, Stat.ENCHANTABILITY).toInt()
	}
	
	fun getCurrentDurability(stack: ItemStack): Int {
		return getMaxDurability(stack) - stack.damage
	}
	
	fun getMaxDurability(stack: ItemStack): Int {
		return getStat(stack, Stat.DURABILITY).toInt() - 1
	}
	
	fun getRatioDurability(stack: ItemStack): Float {
		return getCurrentDurability(stack).toFloat() / getMaxDurability(stack).toFloat()
	}
	
	fun getAttributeModifiers(
		stack: ItemStack,
		slot: EquipmentSlot
	): Multimap<EntityAttribute, EntityAttributeModifier> {
		val builder = ImmutableMultimap.builder<EntityAttribute, EntityAttributeModifier>()
		
		if (notBroken(stack) && slot == EquipmentSlot.MAINHAND) {
			builder.put(
				EntityAttributes.GENERIC_ATTACK_DAMAGE, EntityAttributeModifier(
					ATTACK_DAMAGE_MODIFIER_ID,
					TOOL_MODIFIER_NAME,
					getAttackDamage(stack).toDouble(),
					EntityAttributeModifier.Operation.ADDITION
				)
			)
			
			builder.put(
				EntityAttributes.GENERIC_ATTACK_SPEED, EntityAttributeModifier(
					ATTACK_SPEED_MODIFIER_ID,
					TOOL_MODIFIER_NAME,
					1.2 + getAttackSpeed(stack).toDouble(),   // note: 1.2 = 곡괭이의 기본 속도
					EntityAttributeModifier.Operation.ADDITION
				)
			)
			
			// todofar: reach distance attribute도 추가하기
			// todofar: WEAPON_MODIFIER_NAME 사용
		}
		
		return builder.build()
	}
	
	fun <T : LivingEntity> damageGear(
		stack: ItemStack,
		amount: Int,
		entity: T,
		breakCallback: Consumer<T>
	) {
		if (notBroken(stack) && (!entity.world.isClient && (entity !is PlayerEntity || !(entity as PlayerEntity).abilities.creativeMode))) {
			if (stack.isDamageable) {
				stack.damage(amount, entity.random, if (entity is ServerPlayerEntity) entity else null)
				if (broken(stack)) {
					breakCallback.accept(entity)
					onBroken(stack, entity)
				}
			}
		}
	}
	
	fun onAttack(
		stack: ItemStack,
		target: LivingEntity,
		attacker: LivingEntity
	): Boolean {
		if (notBroken(stack)) {
			stack.damage(1, attacker) { e ->
				e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
			}
			if (GearHelper.broken(stack)) {
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
		if (notBroken(stack)) {
			if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
				stack.damage(1, miner) { e ->
					e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
				}
				if (GearHelper.broken(stack)) {
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
		includeParts: List<PartType> = emptyList(),
		includeStats: List<Stat> = emptyList()
	) {
		// note: 제작 시 `player.world`로 제작 효과음 재생할 수 있음
		Sound.play(player, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS)
		
		includeParts.forEach { GearData.putPartIfMissing(stack, it) }
		includeStats.forEach { GearData.putStatIfMissing(stack, it) }
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
	fun broken(stack: ItemStack): Boolean {
		return getCurrentDurability(stack) <= 0
	}
	
	fun notBroken(stack: ItemStack): Boolean {
		return !broken(stack)
	}
	
	fun postProcessNbt(nbt: NbtCompound, includeStats: List<Stat>, includeParts: List<PartType>) {
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
		tooltip.add(textf(
			"{Durability:} {${GearHelper.getCurrentDurability(stack)}}{/}{${GearHelper.getMaxDurability(stack)}}",
			Color.GRAY, Color.DARK_GREEN.gradient(Color.DARK_RED, GearHelper.getRatioDurability(stack)), Color.DARK_GRAY, Color.DARK_GREEN
		))
		for (stat in includeStats) {
			if (stat == Stat.DURABILITY) continue
			tooltip.add(stat.getDisplayText(stack))
		}
		// todofar: Mining Tier는 알파벳으로 써주는게 좋음
	}
	
	fun getName(stack: ItemStack): Text {
		return Text.translatable(stack.item.getTranslationKey(stack))
	}
	
	fun canRepair(
		stack: ItemStack,
		ingredient: ItemStack,
		includeParts: List<PartType>,
		gearType: GearType
	): Boolean {
		return if (isNotGear(stack)) false
		else getConstructions(stack, includeParts)[PartType.HEAD(gearType)]!!.material.repairIngredient.test(ingredient)
	}
	
	@JvmStatic
	fun isGear(stack: ItemStack): Boolean {
		return stack.item is IGearItem
	}
	
	fun isNotGear(stack: ItemStack): Boolean {
		return !isGear(stack)
	}
	
	fun isEnchantable(stack: ItemStack): Boolean {
		return notBroken(stack)
	}
}