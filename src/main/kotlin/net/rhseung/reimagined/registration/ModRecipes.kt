package net.rhseung.reimagined.registration

import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.gears.recipes.PickaxeGearRecipe

object ModRecipes {
	fun registerAll() {
		GearType.values().forEach {
			register(getRecipe(it))
		}
	}
	
	fun getRecipe(gearType: GearType): Triple<String, RecipeType<*>, RecipeSerializer<*>> {
		return when (gearType) {
			GearType.PICKAXE ->
				Triple(PickaxeGearRecipe.ID, PickaxeGearRecipe.Type.INSTANCE, PickaxeGearRecipe.Serializer.INSTANCE)
		}
	}
	
	fun register(
		id: String,
		recipeType: RecipeType<*>,
		recipeSerializer: RecipeSerializer<*>
	) {
		Registry.register(
			Registries.RECIPE_SERIALIZER, Identifier(ReImagined.MOD_ID, id), recipeSerializer
		)
		Registry.register(
			Registries.RECIPE_TYPE, Identifier(ReImagined.MOD_ID, id), recipeType
		)
	}
	
	fun register(
		arg: Triple<String, RecipeType<*>, RecipeSerializer<*>>
	) {
		register(arg.first, arg.second, arg.third)
	}
}