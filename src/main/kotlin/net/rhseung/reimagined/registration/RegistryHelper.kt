package net.rhseung.reimagined.registration

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.gears.Gear
import net.rhseung.reimagined.tool.parts.Part
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
		
		println("Registering item: $item, Identifier: ${Identifier.of(ReImagined.MOD_ID, path)}")
		return Registry.register(Registries.ITEM, Identifier.of(ReImagined.MOD_ID, path), item)
	}
	
	fun registerGear(gear: Gear): Gear {
		return registerItem("gear/${gear.className.pathName()}", gear)
	}
	
	fun registerParts(part: KClass<out Part>): List<Part> {
		val ret = mutableListOf<Part>()
		
		for (material in Material.getValues()) {
			if (part !in material.canParts) continue
			
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
		group: ItemGroup? = null
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
		recipeSerializer: RecipeSerializer<*>
	) {
		Registry.register(
			Registries.RECIPE_SERIALIZER, Identifier(ReImagined.MOD_ID, id), recipeSerializer
		)
		Registry.register(
			Registries.RECIPE_TYPE, Identifier(ReImagined.MOD_ID, id), recipeType
		)
	}
}