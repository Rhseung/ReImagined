package net.rhseung.reimagined.tool.gears

import com.google.common.collect.Multimap
import net.minecraft.block.Block
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
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.base.IMiningGearItem
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.gears.util.GearHelper
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType

class PickaxeGear : Item(Settings()), IMiningGearItem, Vanishable { // todo: trait 적용
	override val includeStats = PickaxeGear.includeStats
	override val includeParts = PickaxeGear.includeParts
	override val effectiveBlocks = PickaxeGear.effectiveBlocks
	
	companion object {
		val includeStats = listOf<Stat>(
			Stat.ATTACK_DAMAGE,
			Stat.ATTACK_SPEED,
			Stat.MINING_SPEED,
			Stat.MINING_TIER,
			Stat.DURABILITY,
			Stat.ENCHANTABILITY
		)
		
		val includeParts = listOf<PartType>(
			PartType.HANDLE,
			PartType.PICKAXE_HEAD,
			PartType.BINDING
		)
		
		val effectiveBlocks: TagKey<Block> = BlockTags.PICKAXE_MINEABLE
	}

	override fun getType(): GearType {
		return GearType.PICKAXE
	}
	
	override fun getCurrentDurability(stack: ItemStack): Int {
		return GearHelper.getRemainDurability(stack)
	}
	
	override fun getMaxDurability(stack: ItemStack): Int {
		return GearHelper.getMaxDurability(stack)
	}
	
	override fun getRatioDurability(stack: ItemStack): Float {
		return GearHelper.getRemainDurabilityRatio(stack)
	}
	
	override fun getEnchantability(stack: ItemStack): Int {
		return GearHelper.getEnchantability(stack)
	}
	
	override fun getTier(stack: ItemStack): Int {
		return GearHelper.getMiningTier(stack)
	}
	
	override fun getMiningSpeedMultiplier(
		stack: ItemStack,
		state: BlockState
	): Float {
		return GearHelper.getMiningSpeed(stack, state, effectiveBlocks)
	}
	
	override fun onCraft(
		stack: ItemStack,
		world: World,
		player: PlayerEntity
	) {
		return GearHelper.onCraft(stack, world, player,
			needParts = includeParts,
			needStats = includeStats
		)
	}
	
	override fun broken(stack: ItemStack): Boolean {
		return GearHelper.isBroken(stack)
	}
	
	override fun notBroken(stack: ItemStack): Boolean {
		return GearHelper.isNotBroken(stack)
	}
	
	override fun isEnchantable(stack: ItemStack): Boolean {
		return GearHelper.isEnchantable(stack)
	}
	
	override fun getParts(
		stack: ItemStack
	): Map<PartType, IPartItem> {
		return GearHelper.getParts(stack, includeParts)
	}
	
	override fun getAttributeModifiers(
		stack: ItemStack,
		slot: EquipmentSlot
	): Multimap<EntityAttribute, EntityAttributeModifier> {
		return GearHelper.getAttributeModifiers(stack, slot,
			attackDamageModifierId = ATTACK_DAMAGE_MODIFIER_ID,
			attackSpeedModifierId = ATTACK_SPEED_MODIFIER_ID
		)
	}
	
	override fun appendTooltip(
		stack: ItemStack,
		world: World?,
		tooltip: MutableList<Text>,
		context: TooltipContext
	) {
		super.appendTooltip(stack, world, tooltip, context)
		GearHelper.appendTooltip(stack, world, tooltip, context, includeStats)
	}
	
	override fun canRepair(
		stack: ItemStack,
		ingredient: ItemStack
	): Boolean {
		return GearHelper.canRepair(stack, ingredient, includeParts, getType())
	}
	
	override fun getName(stack: ItemStack): Text {
		return GearHelper.getName(stack)
	}
	
	override fun postProcessNbt(nbt: NbtCompound) {
//		GearHelper.postProcessNbt(nbt, includeStats, includeParts)
	}
	
	override fun postHit(
		stack: ItemStack,
		target: LivingEntity,
		attacker: LivingEntity
	): Boolean {
		return GearHelper.onAttack(stack, target, attacker)
	}
	
	override fun postMine(
		stack: ItemStack,
		world: World,
		state: BlockState,
		pos: BlockPos,
		miner: LivingEntity
	): Boolean {
		return GearHelper.onMine(stack, world, state, pos, miner)
	}
	
	override fun isSuitableFor(
		stack: ItemStack,
		state: BlockState
	): Boolean {
		return GearHelper.isSuitableFor(stack, state, effectiveBlocks)
	}
	
	override fun getItemBarStep(stack: ItemStack): Int {
		return GearHelper.getItemBarStep(stack)
	}
	
	override fun isItemBarVisible(stack: ItemStack): Boolean {
		return GearHelper.isItemBarVisible(stack)
	}
	
	override fun getItemBarColor(stack: ItemStack): Int {
		return GearHelper.getItemBarColor(stack)
	}
}