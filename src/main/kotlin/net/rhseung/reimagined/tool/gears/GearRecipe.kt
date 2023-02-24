package net.rhseung.reimagined.tool.gears

import com.google.gson.JsonObject
import kotlinx.coroutines.selects.select
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
import net.rhseung.reimagined.tool.gears.definition.Gear
import net.rhseung.reimagined.tool.parts.definitions.Part

class GearRecipe constructor(
	private val id: Identifier,
) : CraftingRecipe {
	private var selected: Gear? = null
	
	companion object {
		const val ID = "crafting_gear"
	}
	
	override fun matches(
		inventory: CraftingInventory,
		world: World?,
	): Boolean {
		val has = Part.notOptionalClasses.associateWith { false }.toMutableMap()
		var count = 0
		
		for (i in 0 until inventory.size()) {
			val slot = inventory.getStack(i)
			
			if (!slot.isEmpty) count++
			
			if (slot.item is Part) {
				// note: 같은 파츠가 2개 이상 사용되는 제작법 -> 대신 그 파츠 2개가 합쳐진 또다른 하나의 파츠로 제작할 수 있게함
				if (has[(slot.item as Part)::class] == true)
					return false
				
				if (!(slot.item as Part).isOptional)
					has[(slot.item as Part)::class] = true;
			}
		}
		
		for (gear in ModItems.GEARS_LIST) {
			if (gear.notOptionalParts.map { it.element }.toSet() == has.filterValues { it }.keys &&
				gear.includeParts.count() in count..count+1)
			{  // optional 파츠는 1개니까 count+1
				selected = gear
				break
			} else selected = null
		}
		
		return selected != null
	}
	
	override fun craft(inventory: CraftingInventory): ItemStack {
		if (selected == null) return ItemStack.EMPTY
		
		val output = ItemStack(selected)
		var parts = arrayOf<Part>()
		
		for (i in 0 until inventory.size()) {
			val slot = inventory.getStack(i)
			
			if (slot.item is Part) {
				parts += slot.item as Part
			}
		}
		
		GearData.writeParts(output, selected!!.includeParts.map { it.element }, *parts)
		GearData.reCalculate(output, selected!!.includeStats, *parts)
		
		println("do you have optional part: " + selected!!.getPart(output, selected!!.optionalParts[0].element))
		
		return output
	}
	
	override fun fits(
		width: Int,
		height: Int,
	): Boolean {
		return if (selected == null) false
		else width * height >= selected!!.includeStats.count()
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
			json: JsonObject,
		): GearRecipe {
			return GearRecipe(id)
		}
		
		override fun read(
			id: Identifier,
			buf: PacketByteBuf,
		): GearRecipe {
			return GearRecipe(id)
		}
		
		override fun write(
			buf: PacketByteBuf,
			recipe: GearRecipe,
		) {
		}
	}
}