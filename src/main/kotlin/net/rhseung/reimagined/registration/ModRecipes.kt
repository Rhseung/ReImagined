package net.rhseung.reimagined.registration

import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.registration.RegistryHelper.registerRecipe
import net.rhseung.reimagined.tool.gears.GearRecipe

object ModRecipes {
	fun registerAll() {
		registerRecipe(GearRecipe.ID, GearRecipe.Type.INSTANCE, GearRecipe.Serializer.INSTANCE)
	}
}