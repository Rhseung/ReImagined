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
import net.rhseung.reimagined.tool.Stat
import net.rhseung.reimagined.tool.gears.PickaxeGear
import net.rhseung.reimagined.tool.gears.util.GearData
import net.rhseung.reimagined.tool.gears.util.GearHelper
import net.rhseung.reimagined.tool.parts.BindingPart
import net.rhseung.reimagined.tool.parts.HandlePart
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart
import net.rhseung.reimagined.utils.Math.average

class PickaxeGearRecipe constructor(
	private val id: Identifier
) : CraftingRecipe {
	
	companion object {
		val ID = "crafting_pickaxe_gear"
	}
	
	override fun matches(
		inventory: CraftingInventory,
		world: World?
	): Boolean {
		var hasHead = false
		var hasBinding = false
		var hasHandle = false
		
		for (i in 0 until inventory.size()) {
			val slotStack = inventory.getStack(i)
			
			if (slotStack.isEmpty) continue
			else if (slotStack.item is PickaxeHeadPart && !hasHead) hasHead = true
			else if (slotStack.item is HandlePart && !hasHandle) hasHandle = true
			else if (slotStack.item is BindingPart && !hasBinding) hasBinding = true
			else return false
		}
		
		return hasHead && hasBinding && hasHandle
	}
	
	override fun craft(inventory: CraftingInventory): ItemStack {
		val output = ItemStack(ModItems.PICKAXE)
		
		var head: PickaxeHeadPart? = null;
		var binding: BindingPart? = null;
		var handle: HandlePart? = null;
		
		for (i in 0 until inventory.size()) {
			val slotStack = inventory.getStack(i)
			
			if (slotStack.isEmpty) continue
			else if (slotStack.item is PickaxeHeadPart)
				head = slotStack.item as PickaxeHeadPart
			else if (slotStack.item is HandlePart)
				handle = slotStack.item as HandlePart
			else if (slotStack.item is BindingPart)
				binding = slotStack.item as BindingPart
		}
		
		GearData.writeParts(output, head!!, binding!!, handle!!)
		
		PickaxeGear.includeStats.forEach { stat ->
			GearData.writeStats(output,
				stat to average(head.getStat(stat), binding.getStat(stat), handle.getStat(stat)))
		}
		GearData.writeStats(output, Stat.MINING_TIER to head.getStat(Stat.MINING_TIER))
		
		return output
	}
	
	override fun fits(
		width: Int,
		height: Int
	): Boolean {
		return width * height >= PickaxeGear.includeParts.count()
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
	
	class Type private constructor() : RecipeType<PickaxeGearRecipe> {
		companion object {
			val INSTANCE: Type = Type()
			val ID: String = PickaxeGearRecipe.ID
		}
	}
	
	class Serializer : RecipeSerializer<PickaxeGearRecipe> {
		companion object {
			val INSTANCE: Serializer = Serializer()
			val ID: String = PickaxeGearRecipe.ID
		}
		
		override fun read(
			id: Identifier,
			json: JsonObject
		): PickaxeGearRecipe {
			return PickaxeGearRecipe(id)
		}
		
		override fun read(
			id: Identifier,
			buf: PacketByteBuf
		): PickaxeGearRecipe {
			return PickaxeGearRecipe(id)
		}
		
		override fun write(
			buf: PacketByteBuf,
			recipe: PickaxeGearRecipe
		) {
		}
	}
}