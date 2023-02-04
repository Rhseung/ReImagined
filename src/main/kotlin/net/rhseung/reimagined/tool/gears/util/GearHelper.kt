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
import net.minecraft.registry.tag.BlockTags
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
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.gears.util.GearData.NBT_ROOT
import net.rhseung.reimagined.tool.gears.util.GearData.putConstructionIfMissing
import net.rhseung.reimagined.tool.gears.util.GearData.putStatIfMissing
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Name.toPathName
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
	): Number {
		val statCompound = GearData.getData(stack, GearData.NBT_ROOT_STATS)
		
		if (statCompound.contains(stat.name.toPathName())) {
			when {
				stat.isInt -> return statCompound.getInt(stat.name.toPathName())
				!stat.isInt -> return statCompound.getFloat(stat.name.toPathName())
			}
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
				GearData.putConstructionIfMissing(stack, partType)
			}
			
			// todo: 파츠들 parts/pickaxe_head_wood, parts/handle_wood 로 register 해야됨
			val part = Registries.ITEM.get(Identifier(ReImagined.MOD_ID,
				"parts/${partType.name.toPathName()}_" + constructionCompound.getString(partType.name.toPathName())))
			
			constructions[partType] = partType.cast(part)
		}
		
		return constructions
	}
	
	fun getMiningTier(stack: ItemStack): Int {
		return getStat(stack, Stat.MINING_TIER) as Int
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
					getAttackSpeed(stack).toDouble() - 4.0,
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
			damageGear(stack, 1, attacker) { e ->
				e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
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
				damageGear(stack, 1, miner) { e ->
					e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
				}
			}
		}
		
		return true
	}
	
	fun onBroken(
		stack: ItemStack,
		player: LivingEntity
	) {
		player.world.playSoundFromEntity(
			null, player,
			SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1f, 1f
		)
		// todofar: 부서질 때 파티클까지 튀기 (안해도 됨)
	}
	
	fun onCraft(
		stack: ItemStack,
		world: World,
		player: PlayerEntity
	) {
		// note: 제작 시 `player.world`로 제작 효과음 재생할 수 있음
//		GearData.reCalculate(stack, world, player)
	}
	
	fun isSuitableFor(
		stack: ItemStack,
		state: BlockState,
		effectiveBlocks: TagKey<Block>
	): Boolean {
		val tier = getMiningTier(stack)
		
		// todo: for 로도 할 수 있게 해보자
		return if (tier < Material.Netherite.getMiningTier() && state.isIn(ModBlockTags.NEEDS_NETHERITE_TOOL)) false
		else if (tier < Material.Diamond.getMiningTier() && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) false
		else if (tier < Material.Iron.getMiningTier() && state.isIn(BlockTags.NEEDS_IRON_TOOL)) false
		else if (tier < Material.Copper.getMiningTier() && state.isIn(ModBlockTags.NEEDS_COPPER_TOOL)) false
		else if (tier < Material.Stone.getMiningTier() && state.isIn(BlockTags.NEEDS_STONE_TOOL)) false
		else state.isIn(effectiveBlocks)
	}
	
	fun broken(stack: ItemStack): Boolean {
		return getCurrentDurability(stack) <= 0
	}
	
	fun notBroken(stack: ItemStack): Boolean {
		return !broken(stack)
	}
	
	fun postProcessNbt(nbt: NbtCompound, includeStats: List<Stat>, includeParts: List<PartType>) {
		val root = nbt.getCompound(NBT_ROOT)
		
		includeStats.forEach { stat -> putStatIfMissing(root, stat) }
		includeParts.forEach { part -> putConstructionIfMissing(root, part) }
	}
	
	fun appendTooltip(
		stack: ItemStack,
		world: World?,
		tooltip: MutableList<Text>,
		context: TooltipContext,
		includeStats: List<Stat>
	) {
		for (stat in includeStats) {
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