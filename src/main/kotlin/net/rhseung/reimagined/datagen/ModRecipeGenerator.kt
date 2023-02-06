package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.tool.gears.recipes.PickaxeGearRecipe
import java.util.function.Consumer

class ModRecipeGenerator(output: FabricDataOutput) : FabricRecipeProvider(output) {
	
	override fun generate(exporter: Consumer<RecipeJsonProvider>) {
		// todo: gear/pickaxe 말고 iterateEveryDummyTool 같은 거에다가 인자로 받아서 "gear/${gearType}"
		exporter.generate(PickaxeGearRecipe.Serializer.INSTANCE, "gear/pickaxe")
	}
	
	fun <T : CraftingRecipe> Consumer<RecipeJsonProvider>.generate(
		serializer: RecipeSerializer<T>,
		path: String
	) {
		ComplexRecipeJsonBuilder.create(serializer).offerTo(this, "${ReImagined.MOD_ID}:${path}")
	}
}