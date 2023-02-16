package net.rhseung.reimagined.tool.gears.base

import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.rhseung.reimagined.tool.gears.util.GearHelper

open class BasicMiningGearItem : BasicGearItem() {
	override fun isSuitableFor(
		stack: ItemStack,
		state: BlockState,
	): Boolean = GearHelper.isSuitableFor(stack, state, effectiveBlocks)
}