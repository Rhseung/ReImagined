package net.rhseung.reimagined.tool.gears.base

import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
import net.rhseung.reimagined.tool.gears.util.GearHelper

open class BasicWeaponGearItem : BasicGearItem() {
	override val modifierName = "Weapon modifier"
	
	override fun getMiningSpeedMultiplier(
		stack: ItemStack,
		state: BlockState,
	): Float {
		return if (state.material == Material.PLANT ||
			       state.material == Material.REPLACEABLE_PLANT ||
			       state.material == Material.GOURD) 1.5F
		else
			GearHelper.getMiningSpeed(stack, state, effectiveBlocks, value = 15.0F)
	}
	
//	override fun isSuitableFor(state: BlockState): Boolean {
//		return state.isIn(effectiveBlocks)
//	}
}