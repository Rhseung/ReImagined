package net.rhseung.reimagined.tool

import net.rhseung.reimagined.utils.Color
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.rhseung.reimagined.tool.gears.GearStack
import net.rhseung.reimagined.utils.Text.displayName
import net.rhseung.reimagined.utils.Text.round
import net.rhseung.reimagined.utils.Tooltip.coloring

enum class Stat constructor(
	val calculateType: CalculateType,
	val color: Color,
	val isInt: Boolean = false,
	val defaultValue: Float,
) {
	DURABILITY(CalculateType.SUM,
		Color.DARK_GREEN,
		isInt = true,
		defaultValue = 1.0F
	),
	ATTACK_DAMAGE(CalculateType.AVERAGE,
		Color.SMOOTH_RED,
		defaultValue = 0.0F
	),
	ATTACK_SPEED(CalculateType.SUM,
		Color.SMOOTH_VIOLET,
		defaultValue = 0.0F
	),
	MINING_TIER(CalculateType.SUM,
		Color.WHITE,
		isInt = true,
		defaultValue = 0.0F
	),
	MINING_SPEED(CalculateType.AVERAGE,
		Color.SMOOTH_BLUE,
		defaultValue = 0.0F
	),
	ENCHANTABILITY(CalculateType.AVERAGE,
		Color.GOLD,
		isInt = true,
		defaultValue = 10.0F
	),
	
	RANGED_DAMAGE(CalculateType.AVERAGE,
		Color.AQUA,
		defaultValue = 0.0F
	),
	DRAW_SPEED(CalculateType.SUM,
		Color.BLUE,
		defaultValue = 0.0F
	);
	// todofar: 다른 스탯도 추가하기
	
	fun getDisplayTextUsingNBT(
		gearStack: GearStack,
		colorOfStatName: Color = Color.GRAY,
		colorOfStatValue: ((GearStack) -> Color)? = null,
	): MutableText {
		return ("{${name.displayName()}:} " +
				"{${if (isInt) gearStack.getStat(this).toInt() else gearStack.getStat(this).round(2)}}").coloring(
			colorOfStatName, if (colorOfStatValue == null) color else colorOfStatValue(gearStack)
		)
		// todofar: Text.translatable
		// todofar: mining tier처럼 알파벳으로 출력해야하는 상황에 대처하는 방안 만들기
	}
	
	fun getDisplayTextUsingValue(
		stack: ItemStack,
		value: Float,
		colorOfStatName: Color = Color.GRAY,
		colorOfStatValue: Color? = null,
	): MutableText {
		return ("{${name.displayName()}:} {${if (isInt) value.toInt() else value.round()}}").coloring(
			colorOfStatName, colorOfStatValue ?: color
		)
		// todofar: Text.translatable
		// todofar: mining tier처럼 알파벳으로 출력해야하는 상황에 대처하는 방안 만들기
	}
	
	enum class CalculateType {
		SUM,
		AVERAGE,
		MULTIPLY;
	}
}