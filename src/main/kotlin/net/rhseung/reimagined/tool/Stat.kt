package net.rhseung.reimagined.tool

import net.rhseung.reimagined.utils.Color
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.rhseung.reimagined.tool.Material.Companion.getColor
import net.rhseung.reimagined.tool.gears.util.GearHelper.getStat
import net.rhseung.reimagined.utils.Text.displayName
import net.rhseung.reimagined.utils.Text.round
import net.rhseung.reimagined.utils.Tooltip.coloring

enum class Stat constructor(
	val color: Color,
	val isInt: Boolean = false,
	val defaultValue: Float,
	val formatString: String = "" // todo
) {
	DURABILITY(Color.DARK_GREEN, isInt = true, defaultValue = 1.0F,
		"{Durability:} {%d}{/}{%d}"),
	ATTACK_DAMAGE(Color.SMOOTH_RED, defaultValue = 0.0F),
	ATTACK_SPEED(Color.SMOOTH_VIOLET, defaultValue = 1.0F),
	MINING_TIER(getColor(-1), isInt = true, defaultValue = 0.0F),
	MINING_SPEED(Color.SMOOTH_BLUE, defaultValue = 1.0F),
	ENCHANTABILITY(Color.GOLD, isInt = true, defaultValue = 10.0F);
	// todofar: 다른 스탯도 추가하기
	
	fun getDisplayTextUsingNBT(
		stack: ItemStack,
		colorOfStatName: Color = Color.GRAY,
		colorOfStatValue: ((ItemStack) -> Color)? = null
	): MutableText {
		return ("{${name.displayName()}:} " +
			"{${if (isInt) getStat(stack, this).toInt() else getStat(stack, this).round()}}").coloring(
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
		return ("{${name.displayName()}:} {${if (isInt) value.toInt() else value.round()}}").coloring(
			colorOfStatName, colorOfStatValue ?: color
		)
		// todofar: Text.translatable
		// todofar: mining tier처럼 알파벳으로 출력해야하는 상황에 대처하는 방안 만들기
	}
}