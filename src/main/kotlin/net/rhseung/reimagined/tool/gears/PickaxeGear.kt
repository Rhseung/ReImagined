package net.rhseung.reimagined.tool.gears

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
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.BlockTags
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.base.IMiningGearItem
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.gears.util.GearHelper
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Color
import java.lang.Integer.min

class PickaxeGear constructor(

) : IMiningGearItem, Item(Item.Settings().maxDamageIfAbsent(500)) { // todo: trait 적용
	override val includeParts = PickaxeGear.includeParts
	
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
	}

	override fun getType(): GearType {
		return GearType.PICKAXE
	}
	
	override fun getCurrentDurability(stack: ItemStack): Int {
		return GearHelper.getCurrentDurability(stack)
	}
	
	override fun getMaxDurability(stack: ItemStack): Int {
		return GearHelper.getMaxDurability(stack)
	}
	
	override fun getRatioDurability(stack: ItemStack): Float {
		return GearHelper.getRatioDurability(stack)
	}
	
	override fun getEnchantability(stack: ItemStack): Int {
		return GearHelper.getEnchantability(stack)
	}
	
	override fun isEnchantable(stack: ItemStack): Boolean {
		return GearHelper.isEnchantable(stack)
	}
	
	override fun onCraft(
		stack: ItemStack,
		world: World,
		player: PlayerEntity
	) {
		return GearHelper.onCraft(stack, world, player,
			includeParts = includeParts, includeStats = includeStats)
	}
	
	override fun broken(stack: ItemStack): Boolean {
		return GearHelper.broken(stack)
	}
	
	override fun getConstructions(
		stack: ItemStack
	): Map<PartType, IPartItem> {
		return GearHelper.getConstructions(stack, includeParts)
	}
	
	override fun getTier(stack: ItemStack): Int {
		return GearHelper.getMiningTier(stack)
	}
	
	override fun getAttributeModifiers(
		stack: ItemStack,
		slot: EquipmentSlot
	): Multimap<EntityAttribute, EntityAttributeModifier> {
		return GearHelper.getAttributeModifiers(stack, slot)
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
	
	override fun getMiningSpeedMultiplier(
		stack: ItemStack,
		state: BlockState
	): Float {
		return GearHelper.getMiningSpeed(stack, state, BlockTags.PICKAXE_MINEABLE)
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
		return GearHelper.isSuitableFor(stack, state, BlockTags.PICKAXE_MINEABLE)
	}
	
	override fun getItemBarStep(stack: ItemStack): Int {
		return Math.round(13.0F - 13.0F * (1.0F - getRatioDurability(stack)))
	}
	
	override fun isItemBarVisible(stack: ItemStack): Boolean {
		return true
	}
	
	override fun getItemBarColor(stack: ItemStack): Int {
		return Color.DARK_GREEN.gradient(Color.DARK_RED, getRatioDurability(stack)).toHex()
		// todo: unique bar color?
	}
}