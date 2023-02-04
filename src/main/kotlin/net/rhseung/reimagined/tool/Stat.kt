package net.rhseung.reimagined.tool

import net.rhseung.reimagined.utils.Color
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.rhseung.reimagined.tool.Material.Companion.getColor
import net.rhseung.reimagined.tool.gears.util.GearHelper.getRatioDurability
import net.rhseung.reimagined.tool.gears.util.GearHelper.getMiningTier
import net.rhseung.reimagined.tool.gears.util.GearHelper.getStat
import net.rhseung.reimagined.utils.Name.toDisplayName
import net.rhseung.reimagined.utils.Tooltip.point
import net.rhseung.reimagined.utils.Tooltip.textf

enum class Stat constructor(
	val defaultValue: Number,
	val color: (ItemStack) -> Color,
	
	val isInt: Boolean = false
) {
	DURABILITY(1, { stack ->
		Color.DARK_GREEN.gradient(Color.DARK_RED, getRatioDurability(stack))
	}, isInt = true),
	ATTACK_DAMAGE(0F, { stack ->
		Color.SMOOTH_RED
	}),
	ATTACK_SPEED(1.0F, { stack ->
		Color.SMOOTH_VIOLET
	}),
	MINING_TIER(0, { stack ->
		getColor(getMiningTier(stack))
	}, isInt = true),
	MINING_SPEED(1.0F, { stack ->
		Color.SMOOTH_BLUE
	}),
	ENCHANTABILITY(10, { stack ->
		Color.GOLD
	}, isInt = true);
	// todofar: 다른 스탯도 추가하기
	
	fun getDisplayText(
		stack: ItemStack,
		colorOfStatName: Color = Color.GRAY
	): MutableText {
		return textf(
			"{${name.toDisplayName()}:} " +
					"{${if (isInt) getStat(stack, this).toInt() else point(getStat(stack, this).toFloat())}}",
			colorOfStatName, color(stack)
		)
		// todofar: Text.translatable
		// todofar: mining tier처럼 알파벳으로 출력해야하는 상황에 대처하는 방안 만들기
	}
}