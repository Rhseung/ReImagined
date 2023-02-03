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
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.gears.util.GearHelper
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.parts.enums.PartType

class PickaxeGear constructor (

) : IGearItem, Item(Item.Settings()) {
	override val includeStats = listOf(
		Stat.ATTACK_DAMAGE,
		Stat.ATTACK_SPEED,
		Stat.MINING_SPEED,
		Stat.MINING_TIER,
		Stat.DURABILITY,
		Stat.ENCHANTABILITY
	)
	
	override val includeParts = listOf(
		PartType.HEAD,
		PartType.BINDING,
		PartType.HANDLE
	)
	
	override fun getType(): GearType {
		return GearType.Pickaxe
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
	
	override fun getEnchantability(): Int {
		return 10   // fixme
		
//		todo: enchantability는 nbt에서 정해지는게 아니라 아이템 고유의 특성이라서 난감함
//		 - 파츠에서 뽑으려고 했는데 getConstructions도 인자로 ItemStack이 필요해서 안됨
//		return GearHelper.getEnchantability(stack)
	}
	
	override fun broken(stack: ItemStack): Boolean {
		return GearHelper.broken(stack)
	}
	
	override fun getTier(stack: ItemStack): Int {
		return GearHelper.getMiningTier(stack)
	}
	
	override fun onCraft(
		stack: ItemStack,
		world: World,
		player: PlayerEntity
	) {
		return GearHelper.onCraft(stack, world, player)
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
		GearHelper.appendTooltip(stack, world, tooltip, context, includeStats)
	}
	
	override fun canRepair(
		stack: ItemStack,
		ingredient: ItemStack
	): Boolean {
		return GearHelper.canRepair(stack, ingredient, includeParts)
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
		GearHelper.postProcessNbt(nbt)
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
}