package net.rhseung.reimagined.tool.gears

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.EnchantmentHelper
import net.rhseung.reimagined.utils.Tooltip.Draw
import net.rhseung.reimagined.utils.Color
import net.rhseung.reimagined.utils.Icon
import net.rhseung.reimagined.utils.Text.displayName
import net.rhseung.reimagined.utils.Text.removeZero
import net.rhseung.reimagined.utils.Text.round
import net.rhseung.reimagined.utils.Text.toText
import net.rhseung.reimagined.utils.Tooltip.Draw.Companion.getWidth
import net.rhseung.reimagined.utils.Tooltip.coloring

class GearTooltipComponent constructor(
	private val data: GearTooltipData,
) : TooltipComponent {
	val attackDamage = data.gearStack.displayAttackDamage.removeZero().toText()
	val attackSpeed = data.gearStack.attackSpeed.round(1).toText()
	val miningSpeed = data.gearStack.displayMiningSpeed.removeZero().toText()
	val miningTier = coloring(data.gearStack.miningTierMaterial.name.displayName(), data.gearStack.miningTierMaterial.color)
	val durability =
		"{${data.gearStack.remainDurability}}{/${data.gearStack.maxDurability}}".coloring(Color.WHITE, Color.DARK_GRAY)
	
	override fun getHeight(): Int {
		return Draw.getHeight(attackDamage, true) +
		       if (Screen.hasShiftDown()) {
			       Draw.getHeight(miningSpeed, true) +
			       Draw.getHeight(miningTier, true) +
			       Draw.getHeight(null, true) +
			       if (EnchantmentHelper.get(data.gearStack.stack).isNotEmpty())
				       Draw.getHeight(null, true)
			       else 0
		       } else 0
	}
	
	override fun getWidth(textRenderer: TextRenderer): Int {
		return textRenderer.getWidth(attackDamage, true) + textRenderer.getWidth(attackSpeed, true)
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
		
		if (Screen.hasShiftDown()) {
			draw.y += (Draw.ICON_HEIGHT + Draw.SPACE_HEIGHT)
			draw.addVertical(Icon.DURABILITY, durability)
			draw.addVertical(Icon.MINING_SPEED, miningSpeed)
			draw.addVertical(Icon.MINING_LEVEL, miningTier)
			if (EnchantmentHelper.get(data.gearStack.stack).isNotEmpty()) {
				draw.addVertical(Icon.ENCHANTMENT, "Enchantments".toText())
				// todo: "Enchantments" -> translatable text
			}
		}
	}
}