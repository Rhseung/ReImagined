package net.rhseung.reimagined.registration

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.ReImagined.modID
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.gears.definition.Gear
import net.rhseung.reimagined.tool.parts.definitions.BasicPart
import net.rhseung.reimagined.tool.parts.definitions.Part
import net.rhseung.reimagined.tool.parts.definitions.PartDefinition
import net.rhseung.reimagined.utils.Text.pathName
import net.rhseung.reimagined.utils.Utils.createInstance
import net.rhseung.reimagined.utils.Utils.getClassName
import kotlin.reflect.KClass

object RegistryHelper {
	fun <T : Item> registerItem(
		path: String,
		item: T,
		group: ItemGroup? = null,
	): T {
		if (group != null) {
			ItemGroupEvents.modifyEntriesEvent(group)
				.register(ItemGroupEvents.ModifyEntries { entries -> entries.add(item) })
		}
		
		return Registry.register(Registries.ITEM, Identifier.of(ReImagined.MOD_ID, path), item)
	}
	
	fun registerGear(gear: Gear): Gear {
		return registerItem("gear/${gear.className.pathName()}", gear)
	}
	
	fun registerParts(part: KClass<out Part>): List<Part> {
		val ret = mutableListOf<Part>()
		val annotation = (part.annotations.find { it is PartDefinition }!! as PartDefinition)
		val typeInfo = annotation.materialType
		val canMaterials = ((if (typeInfo.allow.isEmpty()) Material.MaterialType.values()
			.toSet() else typeInfo.allow.toSet()) - typeInfo.disallow.toSet()).toMutableSet()
		
		if (annotation.belongsTo == BasicPart.Optional::class) {
			canMaterials.add(Material.MaterialType.DUMMY)
		}
		
		for (material in Material.values()) {
			if (material.type !in canMaterials) continue
			
			if (material != Material.DUMMY)
				ret.add(
					registerItem(
						"parts/${getClassName(part).pathName()}_${material.name.pathName()}",
						createInstance(part, material),
						ModItemGroups.PARTS
					)
				)
		}
		
		return ret
	}
	
	fun <T : Block> registerBlock(
		path: String,
		block: T,
		group: ItemGroup? = null,
	): T {
		if (group != null) {
			ItemGroupEvents.modifyEntriesEvent(group)
				.register(ItemGroupEvents.ModifyEntries { entries -> entries.add(block) })
		}
		Registry.register(Registries.ITEM, Identifier.of(ReImagined.MOD_ID, path), BlockItem(block, Item.Settings()))
		return Registry.register(Registries.BLOCK, Identifier.of(ReImagined.MOD_ID, path), block)
	}
	
	fun registerRecipe(
		id: String,
		recipeType: RecipeType<*>,
		recipeSerializer: RecipeSerializer<*>,
	) {
		Registry.register(
			Registries.RECIPE_SERIALIZER, modID(id), recipeSerializer
		)
		Registry.register(
			Registries.RECIPE_TYPE, modID(id), recipeType
		)
	}
	
	fun registerRecipe(
		recipe: KClass<out CraftingRecipe>
	) {
	
	}
}