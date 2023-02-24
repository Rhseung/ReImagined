package net.rhseung.reimagined.utils

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text

object Tooltip {
	fun coloring(
		text: String,
		color: Color
	): MutableText {
		val t = Text.literal(text)
		t.style = t.style.withColor(color.toHex())
		return t
	}
	
	fun String.coloring(
		vararg colors: Color
	): MutableText {
		var newText = this
		newText = newText.replace("\\{(.*?)}".toRegex(), "\n#$1\n")
		
		var arr = newText.split("\n")
		arr = arr.filter { e -> e != "" }
		
		var ret = Text.literal("")
		var idx = 0
		for (e in arr) {
			ret = if (e.startsWith('#')) {
				ret.append(coloring(e.substring(1), if (colors.count() > idx) colors[idx++] else Color.WHITE))
			} else {
				ret.append(e)
			}
		}
		
		return ret
	}
	
	fun button(
		text: String,
		isPressed: Boolean
	): MutableText {
		return "{[}{${text}}{]} {+}".coloring(
			if (isPressed) Color.DARK_GRAY else Color.GRAY,
			if (isPressed) Color.WHITE else Color.DARK_GRAY,
			if (isPressed) Color.DARK_GRAY else Color.GRAY,
			if (isPressed) Color.WHITE else Color.DARK_GRAY
		)
	}
	
	class Draw constructor(
		private val matrices: MatrixStack,
		private val textRenderer: TextRenderer,
		val itemRenderer: ItemRenderer,
		private val pos0: Triple<Int, Int, Int>
	) {
		var x = pos0.first
		var y = pos0.second
		var z = pos0.third
		
		fun addHorizontal(
			icon: Icon,
			text: Text? = null,
			check: Boolean = true,
			index: Int = 0,
		) {
			if (check)
				x = draw(matrices, textRenderer, text, x, y, z, icon, index = index)
		}
		
		fun addVertical(
			icon: Icon,
			text: Text? = null,
			check: Boolean = true,
			index: Int = 0,
		) {
			x = pos0.first
			if (check)
				y = draw(matrices, textRenderer, text, x, y, z, icon, isHorizontal = false, index = index)
		}
		
		companion object {
			const val NEXT_WIDTH = 8
			
			const val SPACE_WIDTH = 3
			const val SPACE_HEIGHT = 2
			
			const val ICON_WIDTH = 9
			const val ICON_HEIGHT = 9
			
			const val TEXTURE_WIDTH = ICON_WIDTH
			const val TEXTURE_HEIGHT = ICON_HEIGHT
			
			fun TextRenderer.getWidth(
				text: Text?,
				check: Boolean,
			): Int {
				return if (!check) 0
				else if (text == null) ICON_WIDTH
				else ICON_WIDTH + SPACE_WIDTH + this.getWidth(text) + NEXT_WIDTH
			}
			
			fun getHeight(
				text: Text?,
				check: Boolean
			): Int {
				return if (check) ICON_HEIGHT + SPACE_HEIGHT
				else 0
			}
			
			fun draw(
				matrices: MatrixStack,
				textRenderer: TextRenderer,
				text: Text?,
				x: Int,
				y: Int,
				z: Int,
				texture: Icon,
				index: Int = 0,
				isHorizontal: Boolean = true,
				width: Int = ICON_WIDTH,
				height: Int = ICON_HEIGHT,
			): Int {
				RenderSystem.setShaderTexture(0, texture.path(index))
				DrawableHelper.drawTexture(matrices, x, y, z, 0F, 0F, width, height,
					TEXTURE_WIDTH,
					TEXTURE_HEIGHT
				)
				
				if (text != null) {
					matrices.translate(0.0, 0.0, 400.0);
					DrawableHelper.drawTextWithShadow(
						matrices,
						textRenderer,
						text,
						x + ICON_WIDTH + SPACE_WIDTH,
						y + 1,
						0xFFFFFF
					)
					matrices.translate(0.0, 0.0, -400.0);
				}
				
				return if (isHorizontal) x + textRenderer.getWidth(text, true)
				else y + getHeight(text, true)
			}
		}
	}
}