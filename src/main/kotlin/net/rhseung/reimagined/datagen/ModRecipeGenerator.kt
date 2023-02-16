package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.tool.gears.recipes.GearRecipe
import java.util.function.Consumer

class ModRecipeGenerator(output: FabricDataOutput) : FabricRecipeProvider(output) {
	
	override fun generate(exporter: Consumer<RecipeJsonProvider>) {
		exporter.generate(GearRecipe.Serializer.INSTANCE, "gear")
	}
	
	private fun <T : CraftingRecipe> Consumer<RecipeJsonProvider>.generate(
		serializer: RecipeSerializer<T>,
		path: String
	) {
		ComplexRecipeJsonBuilder.create(serializer).offerTo(this, "${ReImagined.MOD_ID}:${path}")
	}
}