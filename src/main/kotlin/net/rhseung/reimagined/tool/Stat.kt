package net.rhseung.reimagined.tool

import net.rhseung.reimagined.utils.Color
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.rhseung.reimagined.tool.Material.Companion.getColor
import net.rhseung.reimagined.tool.gears.util.GearHelper.getMiningTier
import net.rhseung.reimagined.tool.gears.util.GearHelper.getStat
import net.rhseung.reimagined.utils.Name.displayName
import net.rhseung.reimagined.utils.Tooltip.point
import net.rhseung.reimagined.utils.Tooltip.textf

enum class Stat constructor(
	val defaultValue: Float,
	val color: Color,
	
	val isInt: Boolean = false
) {
	DURABILITY(1.0F, Color.DARK_GREEN, isInt = true),
	ATTACK_DAMAGE(0F, Color.SMOOTH_RED),
	ATTACK_SPEED(1.0F, Color.SMOOTH_VIOLET),
	MINING_TIER(0.0F, getColor(-1), isInt = true),
	MINING_SPEED(1.0F, Color.SMOOTH_BLUE),
	ENCHANTABILITY(10.0F, Color.GOLD, isInt = true);
	// todofar: 다른 스탯도 추가하기
	
	fun getDisplayTextUsingNBT(
		stack: ItemStack,
		colorOfStatName: Color = Color.GRAY,
		colorOfStatValue: ((ItemStack) -> Color)? = null
	): MutableText {
		return textf("{${name.displayName()}:} " +
			"{${if (isInt) getStat(stack, this).toInt() else point(getStat(stack, this))}}",
			colorOfStatName, if (colorOfStatValue == null) color else colorOfStatValue(stack)
		)
		// todofar: Text.translatable
		// todofar: mining tier처럼 알파벳으로 출력해야하는 상황에 대처하는 방안 만들기
	}
	
	fun getDisplayTextUsingValue(
		stack: ItemStack,
		value: Float,
		colorOfStatName: Color = Color.GRAY,
		colorOfStatValue: Color? = null
	): MutableText {
		return textf("{${name.displayName()}:} " +
				"{${if (isInt) value.toInt() else point(value)}}",
			colorOfStatName, if (colorOfStatValue == null) color else colorOfStatValue
		)
		// todofar: Text.translatable
		// todofar: mining tier처럼 알파벳으로 출력해야하는 상황에 대처하는 방안 만들기
	}
}