package net.rhseung.reimagined.tool.gears.base

import com.google.common.collect.Multimap
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType

interface IGearItem : ItemConvertible {
	val includeStats: List<Stat>
	val includeParts: List<PartType>
	val effectiveBlocks: TagKey<Block>
	
	fun getType(): GearType
	fun getParts(stack: ItemStack): Map<PartType, IPartItem>
	
	fun getCurrentDurability(stack: ItemStack): Int
	fun getMaxDurability(stack: ItemStack): Int
	fun getRatioDurability(stack: ItemStack): Float
	
	fun getEnchantability(stack: ItemStack): Int
	fun isEnchantable(stack: ItemStack): Boolean
	
	fun getTier(stack: ItemStack): Int
	
	fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState): Float
	
	fun broken(stack: ItemStack): Boolean
	fun notBroken(stack: ItemStack): Boolean
	
	fun getAttributeModifiers(stack: ItemStack, slot: EquipmentSlot): Multimap<EntityAttribute, EntityAttributeModifier>
	
	fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext)
	
	fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean
	
	fun getName(stack: ItemStack): Text
	
	fun postProcessNbt(nbt: NbtCompound)
	
	fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean
	
	fun postMine(stack: ItemStack, world: World, state: BlockState, pos: BlockPos, miner: LivingEntity): Boolean
	
	fun isSuitableFor(stack: ItemStack, state: BlockState): Boolean
	
	fun getItemBarStep(stack: ItemStack): Int
	fun isItemBarVisible(stack: ItemStack): Boolean
	fun getItemBarColor(stack: ItemStack): Int
	
	fun onCraft(stack: ItemStack, world: World, player: PlayerEntity)
}