package net.rhseung.reimagined.tool.gears.tooltip

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.EnchantmentHelper
import net.rhseung.reimagined.tool.Material.Companion.getColor
import net.rhseung.reimagined.utils.Tooltip.Draw
import net.rhseung.reimagined.tool.gears.util.GearHelper
import net.rhseung.reimagined.utils.Color
import net.rhseung.reimagined.utils.Icon
import net.rhseung.reimagined.utils.Text.removeZero
import net.rhseung.reimagined.utils.Text.round
import net.rhseung.reimagined.utils.Text.toText
import net.rhseung.reimagined.utils.Tooltip.Draw.Companion.NEXT_WIDTH
import net.rhseung.reimagined.utils.Tooltip.Draw.Companion.getWidth
import net.rhseung.reimagined.utils.Tooltip.coloring
import java.lang.Integer.max

class GearTooltipComponent constructor(
	private val data: GearTooltipData
): TooltipComponent {
	val attackDamage = GearHelper.getAttackDamage(data.stack).removeZero().toText()
	val attackSpeed = (data.gear.getType().baseAttackSpeed + GearHelper.getAttackSpeed(data.stack)).round(1).toText()
	val miningSpeed = GearHelper.getMiningSpeed(data.stack, getMax = true).removeZero().toText()
	val miningTier = coloring(GearHelper.getMiningTier(data.stack).toString(), getColor(GearHelper.getMiningTier(data.stack)))
	val durability = "{${GearHelper.getRemainDurability(data.stack)}}{/${GearHelper.getMaxDurability(data.stack)}}".coloring(
		Color.WHITE, Color.DARK_GRAY
	)
	
	override fun getHeight(): Int {
		return Draw.getHeight(attackDamage, true) +
			Draw.getHeight(miningSpeed, true) +
			Draw.getHeight(miningTier, true) +
			Draw.getHeight(null, true) +
			if (EnchantmentHelper.get(data.stack).isNotEmpty()) Draw.getHeight(null, true) else 0
	}
	
	override fun getWidth(textRenderer: TextRenderer): Int {
		return textRenderer.getWidth(attackDamage, true) +
				textRenderer.getWidth(attackSpeed, true) +
				textRenderer.getWidth(durability, true) - NEXT_WIDTH
	}
	
	override fun drawItems(
		textRenderer: TextRenderer,
		x0: Int,
		y0: Int,
		matrices: MatrixStack,
		itemRenderer: ItemRenderer,
		z0: Int,
	) {
		val draw = Draw(matrices, textRenderer, itemRenderer, Triple(x0, y0, z0))
		
		draw.addHorizontal(Icon.ATTACK_DAMAGE, attackDamage)
		draw.addHorizontal(Icon.ATTACK_SPEED, attackSpeed)
		draw.addHorizontal(Icon.DURABILITY, durability)
		
		draw.y += (Draw.ICON_HEIGHT + Draw.SPACE_HEIGHT) * 2
		
		draw.addVertical(Icon.MINING_SPEED, miningSpeed)
		draw.addVertical(Icon.MINING_LEVEL, miningTier)
		if (EnchantmentHelper.get(data.stack).isNotEmpty()) {
			draw.addVertical(Icon.ENCHANTMENT, "Enchantments".toText())
		}
	}
}