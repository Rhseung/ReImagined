package net.rhseung.reimagined.tool.gears.recipes

import com.google.gson.JsonObject
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.Identifier
import net.minecraft.world.World
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.gears.util.GearData
import net.rhseung.reimagined.tool.parts.base.BasicPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType

class GearRecipe constructor(
	private val id: Identifier
) : CraftingRecipe {
	private var selectedType: GearType? = null
	
	companion object {
		const val ID = "crafting_gear"
	}
	
	override fun matches(
		inventory: CraftingInventory,
		world: World?
	): Boolean {
		val hasParts = PartType.getValues().associateWith { false }.toMutableMap()
		
		for (i in 0 until inventory.size()) {
			val slotStack = inventory.getStack(i)
			
			if (slotStack.item is BasicPartItem) {
				if (hasParts[(slotStack.item as BasicPartItem).type!!] == true)
					return false
				
				hasParts[(slotStack.item as BasicPartItem).type!!] = true;
			}
		}
		
		for (gearType in GearType.getValues()) {
			if (gearType.includeParts.toSet() == hasParts.filterValues { it }.keys) {
				selectedType = gearType
				break
			}
			else {
				selectedType = null
			}
		}
		
		return selectedType != null
	}
	
	override fun craft(inventory: CraftingInventory): ItemStack {
		if (selectedType == null) return ItemStack.EMPTY
		
		val output = ItemStack(ModItems.GEARS[selectedType])
		
		val parts = mutableListOf<BasicPartItem>()
		for (i in 0 until inventory.size()) {
			val slotStack = inventory.getStack(i)
			
			if (slotStack.item is BasicPartItem) {
				parts.add(slotStack.item as BasicPartItem)
			}
		}
		
		GearData.writeParts(output, *parts.toTypedArray())
		GearData.recalculate(output, selectedType!!.includeStats, *parts.toTypedArray())
		
		return output
	}
	
	override fun fits(
		width: Int,
		height: Int
	): Boolean {
		if (selectedType == null) return false
		return width * height >= selectedType!!.includeParts.count()
	}
	
	override fun getOutput(): ItemStack {
		return ItemStack.EMPTY
	}
	
	override fun getId(): Identifier {
		return this.id
	}
	
	override fun getSerializer(): RecipeSerializer<*> {
		return Serializer.INSTANCE
	}
	
	override fun getCategory(): CraftingRecipeCategory {
		return CraftingRecipeCategory.EQUIPMENT
	}
	
	class Type private constructor() : RecipeType<GearRecipe> {
		companion object {
			val INSTANCE: Type = Type()
			val ID: String = GearRecipe.ID
		}
	}
	
	class Serializer : RecipeSerializer<GearRecipe> {
		companion object {
			val INSTANCE: Serializer = Serializer()
			val ID: String = GearRecipe.ID
		}
		
		override fun read(
			id: Identifier,
			json: JsonObject
		): GearRecipe {
			return GearRecipe(id)
		}
		
		override fun read(
			id: Identifier,
			buf: PacketByteBuf
		): GearRecipe {
			return GearRecipe(id)
		}
		
		override fun write(
			buf: PacketByteBuf,
			recipe: GearRecipe
		) {
		}
	}
}