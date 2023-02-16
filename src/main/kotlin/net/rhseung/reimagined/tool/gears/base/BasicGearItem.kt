package net.rhseung.reimagined.tool.gears.base

import com.google.common.collect.Multimap
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Vanishable
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.gears.util.GearHelper
import net.rhseung.reimagined.tool.parts.base.BasicPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType

open class BasicGearItem : Item(Settings().maxCount(1)), Vanishable {
	open val type: GearType? = null
	open val includeStats = if (type == null) emptySet() else type!!.includeStats
	open val includeParts= if (type == null) emptyList() else type!!.includeParts
	open val effectiveBlocks = type?.effectiveBlocks
	
	open fun getParts(stack: ItemStack): Map<PartType, BasicPartItem> = GearHelper.getParts(stack, includeParts)
	open fun getPart(
		stack: ItemStack,
		partType: PartType,
	): BasicPartItem = GearHelper.getPart(stack, partType)
	
	open fun getRemainDurability(stack: ItemStack): Int = GearHelper.getRemainDurability(stack)
	open fun getMaxDurability(stack: ItemStack): Int = GearHelper.getMaxDurability(stack)
	open fun getRemainDurabilityRatio(stack: ItemStack): Float = GearHelper.getRemainDurabilityRatio(stack)
	
	open fun getEnchantability(stack: ItemStack): Int = GearHelper.getEnchantability(stack)
	override fun isEnchantable(stack: ItemStack): Boolean = GearHelper.isEnchantable(stack)
	
	open fun getTier(stack: ItemStack): Int = GearHelper.getMiningTier(stack)
	
	override fun getMiningSpeedMultiplier(
		stack: ItemStack,
		state: BlockState,
	): Float =
		GearHelper.getMiningSpeed(stack, state, effectiveBlocks)
	
	open fun isBroken(stack: ItemStack): Boolean =
		GearHelper.isBroken(stack)
	
	open fun isNotBroken(stack: ItemStack): Boolean =
		GearHelper.isNotBroken(stack)
	
	override fun getAttributeModifiers(
		stack: ItemStack,
		slot: EquipmentSlot,
	): Multimap<EntityAttribute, EntityAttributeModifier> =
		GearHelper.getAttributeModifiers(
			stack, slot,
			attackDamageModifierId = ATTACK_DAMAGE_MODIFIER_ID,
			attackSpeedModifierId = ATTACK_SPEED_MODIFIER_ID,
			type = type
		)
	
	override fun appendTooltip(
		stack: ItemStack,
		world: World?,
		tooltip: MutableList<Text>,
		context: TooltipContext,
	) =
		GearHelper.appendTooltip(stack, world, tooltip, context, includeStats)
	
	override fun canRepair(
		stack: ItemStack,
		ingredient: ItemStack,
	): Boolean = GearHelper.canRepair(stack, ingredient, includeParts, type)
	
	override fun getName(stack: ItemStack): Text = GearHelper.getName(stack, type)
	
	override fun postProcessNbt(nbt: NbtCompound) = GearHelper.postProcessNbt(nbt, includeStats, includeParts)
	
	override fun postHit(
		stack: ItemStack,
		target: LivingEntity,
		attacker: LivingEntity,
	) = GearHelper.onAttack(stack, target, attacker)
	
	override fun postMine(
		stack: ItemStack,
		world: World,
		state: BlockState,
		pos: BlockPos,
		miner: LivingEntity,
	) = GearHelper.onMine(stack, world, state, pos, miner)
	
	override fun getItemBarStep(stack: ItemStack): Int = GearHelper.getItemBarStep(stack)
	override fun isItemBarVisible(stack: ItemStack): Boolean = GearHelper.isItemBarVisible(stack)
	override fun getItemBarColor(stack: ItemStack): Int = GearHelper.getItemBarColor(stack)
	
	override fun onCraft(
		stack: ItemStack,
		world: World,
		player: PlayerEntity,
	) = GearHelper.onCraft(stack, world, player)
}